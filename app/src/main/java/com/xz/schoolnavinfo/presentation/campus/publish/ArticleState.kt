package com.xz.schoolnavinfo.presentation.campus.publish

import com.baidu.mapapi.model.LatLng

data class ArticleState(
    val title: String = "",
    val content: String = "",
    val address: String = "",
    val location: LatLng?,
)