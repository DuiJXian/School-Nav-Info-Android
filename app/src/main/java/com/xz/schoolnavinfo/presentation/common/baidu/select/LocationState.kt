package com.xz.schoolnavinfo.presentation.common.baidu.select

import com.baidu.mapapi.model.LatLng

data class LocationState(
    val name: String,
    val address: String,
    val location: LatLng
)