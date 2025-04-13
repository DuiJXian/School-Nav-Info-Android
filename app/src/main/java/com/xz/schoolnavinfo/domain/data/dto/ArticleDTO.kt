package com.xz.schoolnavinfo.domain.data.dto

import com.xz.schoolnavinfo.domain.data.entity.Article
import com.xz.schoolnavinfo.domain.data.entity.UserInfo

data class ArticleDTO(
    val article: Article? = null,
    val userInfo: UserInfo? = null,
    val imageList: List<String>? = null
)