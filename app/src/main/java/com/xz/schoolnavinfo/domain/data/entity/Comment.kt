package com.xz.schoolnavinfo.domain.data.entity

data class Comment(
    val articleId: String,
    val content: String,
    val createTime: String?
)