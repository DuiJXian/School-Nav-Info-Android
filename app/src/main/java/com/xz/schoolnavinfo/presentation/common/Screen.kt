package com.xz.schoolnavinfo.presentation.common

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object ArticleDetail : Screen("article/{articleId}") {
        fun createRoute(articleId: String) = "detail/$articleId"
    }
}
