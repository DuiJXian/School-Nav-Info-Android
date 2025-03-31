package com.xz.schoolnavinfo.presentation.map

import com.xz.schoolnavinfo.domain.model.LocalPoiInfo

data class LocalPoiState(
    val localPoiInfos: List<LocalPoiInfo> = emptyList(),
    val isFavoritePoi: Boolean = false,
    val favoritePoi: LocalPoiInfo? = null
)