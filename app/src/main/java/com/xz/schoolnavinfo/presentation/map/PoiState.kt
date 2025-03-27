package com.xz.schoolnavinfo.presentation.map

import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.PoiDetailInfo
import com.baidu.mapapi.search.core.PoiInfo

data class PoiState(
    val searchText: String = "",
    val isFocused: Boolean = false,
    val poiInfoList: List<PoiInfo> = emptyList(),
    val centerPoint: LatLng = LatLng(39.5427, 116.2317),
    val poiDetailInfo: PoiDetailInfo? = null,
    val isShowDetailCard: Boolean = false,
    val isShowSearch: Boolean = true
)