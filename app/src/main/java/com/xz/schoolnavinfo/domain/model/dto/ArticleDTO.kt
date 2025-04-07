package com.xz.schoolnavinfo.domain.model.dto

import com.xz.schoolnavinfo.domain.model.entity.Article
import com.xz.schoolnavinfo.domain.model.entity.Image
import com.xz.schoolnavinfo.domain.model.entity.UserInfo

data class ArticleDTO(
    val article: Article? = null,
    val userInfo: UserInfo? = null,
    val images: List<Image>? = null
)