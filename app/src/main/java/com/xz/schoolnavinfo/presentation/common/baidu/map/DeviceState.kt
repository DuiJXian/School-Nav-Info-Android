package com.xz.schoolnavinfo.presentation.common.baidu.map

import com.baidu.mapapi.model.LatLng

data class DeviceState(
    val isOpenGps: Boolean = false,
    val isGrantedPermission: Boolean = false,
    val direction: Float = 0f,
    val locationPoint: LatLng = LatLng(39.5427, 116.2317)
)