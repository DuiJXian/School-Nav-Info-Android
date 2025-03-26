package com.xz.schoolnavinfo.presentation.common.baidu

import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.MyLocationConfiguration

data class BDMapConfig(
    var showZoomControls: Boolean = false,
    var isMyLocationEnabled: Boolean = true,
    var myLocationConfiguration: MyLocationConfiguration = MyLocationConfiguration.Builder(
        MyLocationConfiguration.LocationMode.NORMAL,
        true
    ).build()
)

object BDMapSetting {

    var config = BDMapConfig()
    private var bDMapView: MapView? = null

    fun setConfig(mapView: MapView) {
        this.bDMapView = mapView
        val baiduMap = mapView.map
        mapView.showZoomControls(config.showZoomControls)
        baiduMap.isMyLocationEnabled = config.isMyLocationEnabled
        baiduMap.setMyLocationConfiguration(config.myLocationConfiguration)
    }

    fun onDestroy(){
        bDMapView?.onDestroy()
    }

    fun onPause(){
        bDMapView?.onPause()
    }

    fun onResume(){
        bDMapView?.onResume()
    }
}