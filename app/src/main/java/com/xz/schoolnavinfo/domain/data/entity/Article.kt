package com.xz.schoolnavinfo.domain.data.entity

data class Article(
    val id: String? = null,
    val title: String? = null,
    val content: String? = null,
    val userId: String? = null,
    val createTime: String? = null,
    val type: String? = null,
    val location: String? = null,
    val address: String? = null,
    val banner: Boolean = false
)
