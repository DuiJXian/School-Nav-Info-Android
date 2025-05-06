package com.xz.schoolnavinfo.presentation.map

sealed class RouteEvent{
    data class DisAndDurChange(val distance: String,val duration: String):RouteEvent()
    data class ShowRouteMenu(val isShow: Boolean):RouteEvent()
}