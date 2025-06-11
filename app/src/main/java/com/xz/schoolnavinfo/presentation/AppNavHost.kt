package com.xz.schoolnavinfo.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.xz.schoolnavinfo.presentation.nav.NavController
import com.xz.schoolnavinfo.presentation.campus.publish.PublishArticleViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel

@Composable
fun AppNavGraph(
    commonViewModel: CommonViewModel = hiltViewModel(),
    publishArticleViewModel: PublishArticleViewModel = hiltViewModel()
) {
    val navController = remember { NavController() }
    SchoolNavInfoNavGraph(
        navController = navController,
        commonViewModel = commonViewModel,
        publishArticleViewModel = publishArticleViewModel
    )
}