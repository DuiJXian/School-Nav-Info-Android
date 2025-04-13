package com.xz.schoolnavinfo.presentation.common

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object PublishArticle : Screen("publishArticle")
    data object ImagePreviewScreen : Screen("imagePreviewScreen")

    data object ArticleDetail : Screen("article/articleDetail")
}
