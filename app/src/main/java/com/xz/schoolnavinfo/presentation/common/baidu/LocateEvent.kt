package com.xz.schoolnavinfo.presentation.common.baidu

sealed class LocateEvent {
    data class GpsChange(val res: Boolean) : LocateEvent()
    data class PermissionChange(val res: Boolean) : LocateEvent()
}