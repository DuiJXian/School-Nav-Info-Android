package com.xz.schoolnavinfo.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.xz.schoolnavinfo.presentation.campus.publish.PublishArticleViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel

@Composable
fun AppNavGraphProvider(
    navController: NavHostController = rememberNavController(),
    commonViewModel: CommonViewModel = hiltViewModel(),
    publishArticleViewModel: PublishArticleViewModel = hiltViewModel()
) {
    val navigator = remember(navController) { Navigator(navController) }

    CompositionLocalProvider(
        LocalAppNavigator provides navigator
    ) {
        SchoolNavInfoNavGraph(
            navController = navController,
            commonViewModel = commonViewModel,
            publishArticleViewModel = publishArticleViewModel
        )
    }
}