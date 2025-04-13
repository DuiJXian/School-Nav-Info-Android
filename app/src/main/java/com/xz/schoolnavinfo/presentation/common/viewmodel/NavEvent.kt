package com.xz.schoolnavinfo.presentation.common.viewmodel

import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO

sealed class NavEvent {
    data object LoginOrRegister : NavEvent()
    data class ArticleDetail(val articleDTO: ArticleDTO, val type: String) : NavEvent()
    data object BackPage : NavEvent()
    data class PublishArticle(val type: String) : NavEvent()
    data object ImagePreview : NavEvent()
}