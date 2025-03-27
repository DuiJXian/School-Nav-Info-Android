package com.xz.schoolnavinfo.presentation.map

import com.baidu.mapapi.model.LatLng

sealed class PoiEvent {
    data class SearchTextChange(val text: String): PoiEvent()
    data object ClearSearchText : PoiEvent()
    data class FocusedChange(val isFocused: Boolean): PoiEvent()
    data object ClearInfoList : PoiEvent()
    data object DestroyPoiSearch : PoiEvent()
    data class GetPoiDetailInfo(val poiUid: String) : PoiEvent()
    data object CloseDetailCard: PoiEvent()
    data class CenterPointChange(val centerPoint: LatLng): PoiEvent()
    data class IsShowSearchPoi(val isShow: Boolean): PoiEvent()
}