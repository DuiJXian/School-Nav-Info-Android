package com.xz.schoolnavinfo.presentation.common.viewmodel

sealed class NavEvent {
    data object LoginOrRegister: NavEvent()
    data object ArticleDetail: NavEvent()
    data object PublishDiscuss: NavEvent()
}