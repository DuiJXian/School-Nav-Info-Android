package com.xz.schoolnavinfo.presentation.common.baidu

import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.MyLocationConfiguration
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.overlayutil.OverlayManager
import com.baidu.mapapi.search.route.BikingRoutePlanOption
import com.baidu.mapapi.search.route.DrivingRoutePlanOption
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener
import com.baidu.mapapi.search.route.PlanNode
import com.baidu.mapapi.search.route.RoutePlanSearch
import com.baidu.mapapi.search.route.WalkingRoutePlanOption


data class BDMapConfig(
    var showZoomControls: Boolean = false,
    var isMyLocationEnabled: Boolean = true,
    var myLocationConfiguration: MyLocationConfiguration = MyLocationConfiguration.Builder(
        MyLocationConfiguration.LocationMode.NORMAL,
        true
    ).build(),
)

object BDMapSetting {
    var config = BDMapConfig()
    var isFinishSetConfig = false
    var bDMapView: MapView? = null
    var baiduMap: BaiduMap? = null
    var routePlanSearch: RoutePlanSearch? = null
    private var overlayManager: OverlayManager? = null
    private var routePlanListener: OnGetRoutePlanResultListener? = null
    var myLocation: LatLng = LatLng(39.5427, 116.2317)
    private var startNode:PlanNode ? =null
    private var endNode:PlanNode ? =null

    fun setConfig(mapView: MapView) {
        if (isFinishSetConfig) return
        isFinishSetConfig = true
        bDMapView = mapView
        baiduMap = mapView.map
        mapView.showZoomControls(config.showZoomControls)
        baiduMap?.isMyLocationEnabled = config.isMyLocationEnabled
        baiduMap?.setMyLocationConfiguration(config.myLocationConfiguration)
    }

    fun setOnMapClickListener(listener: OnMapClickListener) {
        baiduMap?.setOnMapClickListener(listener)
    }

    fun setMyLocationData(myLocationData: MyLocationData) {
        baiduMap?.setMyLocationData(myLocationData)
    }

    fun setRoutePlanListener(
        listener: OnGetRoutePlanResultListener,
        startPoint: LatLng = myLocation,
        endPoint: LatLng,
    ) {
        startNode = PlanNode.withLocation(startPoint)
        endNode = PlanNode.withLocation(endPoint)
        routePlanListener = listener
        routePlanSearch = RoutePlanSearch.newInstance()
        routePlanSearch?.setOnGetRoutePlanResultListener(routePlanListener)
    }

    fun startRoutePlan(routePlanType: RoutePlanType = RoutePlanType.Walking) {
        removeOverlay()
        when (routePlanType) {
            is RoutePlanType.Walking -> {
                routePlanSearch?.walkingSearch(
                    (WalkingRoutePlanOption())
                        .from(startNode)
                        .to(endNode)
                )
            }

            is RoutePlanType.Biking -> {
                routePlanSearch?.bikingSearch(
                    BikingRoutePlanOption()
                        .from(startNode)
                        .to(endNode)
                )
            }

            is RoutePlanType.Driving -> {
                routePlanSearch?.drivingSearch(
                    DrivingRoutePlanOption()
                        .from(startNode)
                        .to(endNode)
                )
            }
        }
    }

    fun setOverlayManager(overlayManager: OverlayManager) {
        this.overlayManager = overlayManager
    }

    fun removeOverlay() {
        overlayManager?.removeFromMap()
    }

    fun moveMapToLocation(latLng: LatLng, zoom: Float = 18f) {
        baiduMap?.animateMapStatus(
            MapStatusUpdateFactory.newLatLngZoom(
                latLng,
                zoom
            )
        )
    }

    fun onDestroy() {
        routePlanSearch?.destroy()
        bDMapView?.onDestroy()
    }

    fun onPause() {
        bDMapView?.onPause()
    }

    fun onResume() {
        bDMapView?.onResume()
    }
}

sealed class RoutePlanType {
    data object Walking : RoutePlanType()
    data object Driving : RoutePlanType()
    data object Biking : RoutePlanType()
}