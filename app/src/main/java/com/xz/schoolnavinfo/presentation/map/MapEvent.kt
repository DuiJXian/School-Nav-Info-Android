package com.xz.schoolnavinfo.presentation.map

import com.baidu.mapapi.model.LatLng

sealed class MapEvent {
    data class SearchTextChange(val text: String, val centerPoint: LatLng): MapEvent()
    data object ClearSearchText : MapEvent()
    data object ClearInfoList : MapEvent()
    data object DestroyPoiSearch : MapEvent()
    data class GetPoiDetailInfo(val poiUid: String) : MapEvent()
    data object CloseDetailCard: MapEvent()
}