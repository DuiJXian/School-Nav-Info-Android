package com.xz.schoolnavinfo.presentation.common

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object ArticleDetail : Screen("article/detail")
    data object PublishArticle : Screen("article/publish")
    data object ImagePreview : Screen("imagePreview")
    data object MapLocationSelect: Screen("mapSelect")
    data object PublishStuff: Screen("stuff/publish")
    data object StuffDetail: Screen("stuff/detail")
}
