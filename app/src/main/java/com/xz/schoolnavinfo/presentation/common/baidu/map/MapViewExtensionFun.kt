package com.xz.schoolnavinfo.presentation.common.baidu.map

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.Marker
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.model.LatLng
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun BaiduMap.addMarker(
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

    val marker = this.addOverlay(option) as Marker
    onMarker(marker)
}

fun BaiduMap.setMakerDragListener(onMarkerDragListener: OnMarkerDragListener) {
    this.setOnMarkerDragListener(onMarkerDragListener)
}


//设置样式
fun MapView.setStyle(context: Context, isDark: Boolean) {
    if (isDark) {
        val path = getCustomStyleFilePath(context, "dark.sty")
        this.setMapCustomStylePath(path)
    }
    this.setMapCustomStyleEnable(isDark)
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


//移动地图窗口
fun BaiduMap.scrollMapView(latLng: LatLng, zoom: Float = 18f, animate: Boolean = true) {
    if (animate) {
        this.animateMapStatus(
            MapStatusUpdateFactory.newLatLngZoom(
                latLng,
                zoom
            )
        )
    } else {
        this.setMapStatus(
            MapStatusUpdateFactory.newLatLngZoom(
                latLng,
                zoom
            )
        )
    }
}

fun BaiduMap.adjustMapZoom(value: Float) {
    val mapStatus = this.mapStatus
    this.animateMapStatus(
        MapStatusUpdateFactory.newLatLngZoom(
            mapStatus.target,
            mapStatus.zoom + value
        )
    )
}