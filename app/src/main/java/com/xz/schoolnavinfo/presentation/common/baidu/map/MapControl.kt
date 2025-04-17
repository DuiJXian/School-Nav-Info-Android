package com.xz.schoolnavinfo.presentation.common.baidu.map

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.Marker
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.map.MyLocationConfiguration
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.overlayutil.BikingRouteOverlay
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay
import com.baidu.mapapi.overlayutil.OverlayManager
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay
import com.baidu.mapapi.search.core.PoiDetailInfo
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener
import com.baidu.mapapi.search.poi.PoiDetailResult
import com.baidu.mapapi.search.poi.PoiDetailSearchOption
import com.baidu.mapapi.search.poi.PoiDetailSearchResult
import com.baidu.mapapi.search.poi.PoiIndoorResult
import com.baidu.mapapi.search.poi.PoiNearbySearchOption
import com.baidu.mapapi.search.poi.PoiResult
import com.baidu.mapapi.search.poi.PoiSearch
import com.baidu.mapapi.search.route.BikingRoutePlanOption
import com.baidu.mapapi.search.route.BikingRouteResult
import com.baidu.mapapi.search.route.DrivingRoutePlanOption
import com.baidu.mapapi.search.route.DrivingRouteResult
import com.baidu.mapapi.search.route.IndoorRouteResult
import com.baidu.mapapi.search.route.IntegralRouteResult
import com.baidu.mapapi.search.route.MassTransitRouteResult
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener
import com.baidu.mapapi.search.route.PlanNode
import com.baidu.mapapi.search.route.RoutePlanSearch
import com.baidu.mapapi.search.route.TransitRouteResult
import com.baidu.mapapi.search.route.WalkingRoutePlanOption
import com.baidu.mapapi.search.route.WalkingRouteResult
import com.baidu.mapapi.utils.DistanceUtil
import com.xz.schoolnavinfo.common.utils.DataStoreUtils
import com.xz.schoolnavinfo.common.utils.LocateUtils
import com.xz.schoolnavinfo.common.utils.TimeUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


var defaultLocation: LatLng = LatLng(39.5427, 116.2317)

object MapControl {
//    @SuppressLint("StaticFieldLeak")
//    private var mapView: MapView? = null

    private var routePlanSearch: RoutePlanSearch? = null
    private var overlayManager: OverlayManager? = null


    private var routeStartNode: PlanNode? = null
    private var routeEndNode: PlanNode? = null

//    fun createInstance(context: Context): MapView {
//        if (mapView != null) return mapView as MapView
//        val option = BaiduMapOptions().apply {
//            zoomControlsEnabled(false)
//        }
//        mapView = MapView(context, option)
//        setConfig(mapView as MapView)
//        return mapView as MapView
//    }

    //添加maker
    fun addMarker(
        mapView: MapView,
        location: LatLng,
        bitmap: Bitmap,
        onMarker: (Marker) -> Unit
    ) {
        val option = MarkerOptions()
            .position(location)
            .icon(
                BitmapDescriptorFactory
                    .fromBitmap(bitmap)
            )
            .draggable(true)

        val marker = mapView.map.addOverlay(option) as Marker
        onMarker(marker)
    }

    fun setMakerDragListener(mapView: MapView, onMarkerDragListener: OnMarkerDragListener) {
        mapView.map.setOnMarkerDragListener(onMarkerDragListener)
    }

    //设置地图参数
    fun setConfig(mapView: MapView) {
        mapView.map.isMyLocationEnabled = true
        mapView.map.setMyLocationConfiguration(
            MyLocationConfiguration.Builder(MyLocationConfiguration.LocationMode.NORMAL, true)
                .build()
        )
    }

    //设置样式
    fun setStyle(mapView: MapView, context: Context, isDark: Boolean) {
        if (isDark) {
            val path = getCustomStyleFilePath(context, "dark.sty")
            mapView.setMapCustomStylePath(path)
        }
        mapView.setMapCustomStyleEnable(isDark)
    }

    private fun getCustomStyleFilePath(context: Context, customStyleFileName: String): String? {
        return try {
            val inputStream = context.assets.open(customStyleFileName)
            val buffer = inputStream.readBytes()
            val parentPath = context.filesDir.absolutePath
            val customStyleFile = File("$parentPath/$customStyleFileName")

            if (customStyleFile.exists()) {
                customStyleFile.delete()
            }
            customStyleFile.createNewFile()
            FileOutputStream(customStyleFile).use { it.write(buffer) }

            "$parentPath/$customStyleFileName"
        } catch (e: IOException) {
            Log.e("CustomMapDemo", "Copy custom style file failed", e)
            null
        }
    }

    //设置地图的点击监听
    fun setOnMapClickListener(listener: OnMapClickListener, mapView: MapView) {
        mapView.map.setOnMapClickListener(listener)
    }

    //设置地图的位置信息
    fun setMyLocationData(myLocationData: MyLocationData, mapView: MapView) {
        mapView.map.setMyLocationData(myLocationData)
    }

    //准备路线规划监听
    fun preRoutePlan(
        startLocation: LatLng,
        endLocation: LatLng,
        mapView: MapView,
        calOver: (String, String) -> Unit,
    ) {
        val onGetPlanListener = object : OnGetRoutePlanResultListener {
            override fun onGetWalkingRouteResult(walkingRes: WalkingRouteResult) {
                val overlay = WalkingRouteOverlay(mapView.map)
                overlayManager = overlay
                if (walkingRes.routeLines.size > 0) {
                    val routeLine = walkingRes.routeLines[0]
                    overlay.setData(routeLine)
                    overlay.addToMap()
                    val distance = LocateUtils.metersToKilometers(routeLine.distance)
                    val duration = TimeUtils.formatTime(routeLine.duration)
                    calOver(distance, duration)
                }

            }

            override fun onGetTransitRouteResult(p0: TransitRouteResult?) {}
            override fun onGetMassTransitRouteResult(p0: MassTransitRouteResult?) {}
            override fun onGetDrivingRouteResult(drivingRes: DrivingRouteResult) {
                val overlay = DrivingRouteOverlay(mapView.map)
                overlayManager = overlay
                if (drivingRes.routeLines.size > 0) {
                    val routeLine = drivingRes.routeLines[0]
                    overlay.setData(routeLine)
                    overlay.addToMap()
                    val distance = LocateUtils.metersToKilometers(routeLine.distance)
                    val duration = TimeUtils.formatTime(routeLine.duration)
                    calOver(distance, duration)
                }
            }

            override fun onGetIndoorRouteResult(p0: IndoorRouteResult?) {}
            override fun onGetBikingRouteResult(bikingRes: BikingRouteResult) {
                val overlay = BikingRouteOverlay(mapView.map)
                overlayManager = overlay
                if (bikingRes.routeLines.size > 0) {
                    val routeLine = bikingRes.routeLines[0]
                    overlay.setData(routeLine)
                    overlay.addToMap()
                    val distance = LocateUtils.metersToKilometers(routeLine.distance)
                    val duration = TimeUtils.formatTime(routeLine.duration)
                    calOver(distance, duration)
                }
            }

            override fun onGetIntegralRouteResult(p0: IntegralRouteResult?) {}
        }
        routeStartNode = PlanNode.withLocation(startLocation)
        routeEndNode = PlanNode.withLocation(endLocation)
        routePlanSearch = RoutePlanSearch.newInstance()
        routePlanSearch?.setOnGetRoutePlanResultListener(onGetPlanListener)
    }

    //开始回绘制路线
    fun startRoutePlan(routePlanType: RoutePlanType = RoutePlanType.Walking) {
        removeOverlay()
        when (routePlanType) {
            is RoutePlanType.Walking -> {
                routePlanSearch?.walkingSearch(
                    (WalkingRoutePlanOption())
                        .from(routeStartNode)
                        .to(routeEndNode)
                )
            }

            is RoutePlanType.Biking -> {
                routePlanSearch?.bikingSearch(
                    BikingRoutePlanOption()
                        .from(routeStartNode)
                        .to(routeEndNode)
                )
            }

            is RoutePlanType.Driving -> {
                routePlanSearch?.drivingSearch(
                    DrivingRoutePlanOption()
                        .from(routeStartNode)
                        .to(routeEndNode)
                )
            }
        }
    }

    //移除路线
    fun removeOverlay() {
        overlayManager?.removeFromMap()
    }

    //移动地图窗口
    fun setMapWindow(mapView: MapView, latLng: LatLng, zoom: Float = 18f, type: Int = 1) {
        if (type == 0) {
            mapView.map.setMapStatus(
                MapStatusUpdateFactory.newLatLngZoom(
                    latLng,
                    zoom
                )
            )
        } else {
            mapView.map.animateMapStatus(
                MapStatusUpdateFactory.newLatLngZoom(
                    latLng,
                    zoom
                )
            )
        }
    }

    //销毁地图
    fun onDestroy(mapView: MapView) {
        mapView.onDestroy()
    }

    //保存位置到本地
    suspend fun saveLocation(context: Context, point: LatLng) {
        DataStoreUtils.saveData(
            context,
            DataStoreUtils.Keys.LONGITUDE,
            point.longitude
        )
        DataStoreUtils.saveData(
            context,
            DataStoreUtils.Keys.LATITUDE,
            point.latitude
        )
    }

    //加载本地位置
    suspend fun loadLocation(context: Context): LatLng {
        val latitude =
            DataStoreUtils.getData(
                context,
                DataStoreUtils.Keys.LATITUDE,
                defaultLocation.latitude
            )
        val longitude =
            DataStoreUtils.getData(
                context,
                DataStoreUtils.Keys.LONGITUDE,
                defaultLocation.longitude
            )
        return LatLng(latitude, longitude)
    }


}

sealed class RoutePlanType {
    data object Walking : RoutePlanType()
    data object Driving : RoutePlanType()
    data object Biking : RoutePlanType()
}