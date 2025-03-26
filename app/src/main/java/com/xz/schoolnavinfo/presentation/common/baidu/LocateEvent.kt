package com.xz.schoolnavinfo.presentation.common.baidu

import com.baidu.mapapi.model.LatLng

sealed class LocateEvent {
    data class GpsChange(val res: Boolean) : LocateEvent()
    data class PermissionChange(val res: Boolean) : LocateEvent()
    data class LocationChange(val latLng: LatLng) : LocateEvent()
}