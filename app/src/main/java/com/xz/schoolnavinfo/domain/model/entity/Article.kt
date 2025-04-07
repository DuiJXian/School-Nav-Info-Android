package com.xz.schoolnavinfo.domain.model.entity

import java.time.LocalDateTime

data class Article(
    val id: String,
    val title: String,
    val content: String,
    val userId: String,
    val createTime: String,
    val type: String,
    val location: String,
    val address: String
)
