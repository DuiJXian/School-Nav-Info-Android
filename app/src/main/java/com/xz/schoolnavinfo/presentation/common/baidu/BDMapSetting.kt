package com.xz.schoolnavinfo.presentation.common.baidu

import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.MyLocationConfiguration
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
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
    private var bDMapView: MapView? = null
    var baiduMap: BaiduMap? = null
    private var routePlanSearch: RoutePlanSearch? = null

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

    fun routePlan(
        listener: OnGetRoutePlanResultListener,
        startPoint: LatLng,
        endPoint: LatLng,
    ) {
        routePlanSearch = RoutePlanSearch.newInstance()
        routePlanSearch?.setOnGetRoutePlanResultListener(listener)

        val stNode = PlanNode.withLocation(startPoint)
        val enNode = PlanNode.withLocation(endPoint)

        routePlanSearch?.walkingSearch(
            (WalkingRoutePlanOption())
                .from(stNode)
                .to(enNode)
        )
    }

    fun moveMapToLocation(latLng: LatLng, zoom: Float = 18f) {
        baiduMap?.animateMapStatus(
            MapStatusUpdateFactory.newLatLngZoom(
                latLng,
                zoom
            )
        )
    }

    fun routePlanDestroy() {
        baiduMap?.clear()
        routePlanSearch?.destroy()
    }

    fun onDestroy() {
        bDMapView?.onDestroy()
    }

    fun onPause() {
        bDMapView?.onPause()
    }

    fun onResume() {
        bDMapView?.onResume()
    }
}