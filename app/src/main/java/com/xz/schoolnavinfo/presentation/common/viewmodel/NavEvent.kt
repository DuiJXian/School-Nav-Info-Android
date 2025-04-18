package com.xz.schoolnavinfo.presentation.common.viewmodel

import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.presentation.campus.CampusMenu

sealed class NavEvent {
    data object LoginOrRegister : NavEvent()
    data class ArticleDetail(val articleDTO: ArticleDTO, val articleType: ArticleType) : NavEvent()
    data object BackPage : NavEvent()
    data class PublishArticle(val campusMenu: CampusMenu) : NavEvent()
    data object ImagePreview : NavEvent()
    data object MapLocationSelect : NavEvent()
    data object PublishStuff : NavEvent()
    data class StuffDetail(val id:String): NavEvent()
}