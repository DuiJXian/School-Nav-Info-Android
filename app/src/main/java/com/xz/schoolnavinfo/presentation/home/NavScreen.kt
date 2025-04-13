package com.xz.schoolnavinfo.presentation.home

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.xz.schoolnavinfo.common.event.NetExceptionEvent
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.presentation.campus.detail.ArticleDetailScreen
import com.xz.schoolnavinfo.presentation.campus.publish.PublishArticleViewModel
import com.xz.schoolnavinfo.presentation.campus.publish.PublishDiscussScreen
import com.xz.schoolnavinfo.presentation.common.Screen
import com.xz.schoolnavinfo.presentation.common.compose.ImagePreviewScreen
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.user.LoginOrRegisterScreen
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun NavScreen(
    commonViewModel: CommonViewModel = hiltViewModel()
) {

    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }

    var articleDTO by remember { mutableStateOf(ArticleDTO()) }

    val publishArticleViewModel = hiltViewModel<PublishArticleViewModel>()


    //全局路由
    LaunchedEffect(true) {
        commonViewModel.navEventFlow.collectLatest {
            when (it) {
                is NavEvent.LoginOrRegister -> {
                    navController.navigate(Screen.Login.route)
                }

                is NavEvent.PublishArticle -> {
                    navController.navigate("${Screen.PublishArticle.route}/${it.type}")
                }

                is NavEvent.BackPage -> {
                    if (navController.currentBackStackEntry?.destination?.route != Screen.Home.route) {
                        navController.popBackStack()
                    }
                }

                is NavEvent.ImagePreview -> {
                    navController.navigate(Screen.ImagePreviewScreen.route) {
                        launchSingleTop = true
                        popUpTo(Screen.ImagePreviewScreen.route) {
                            inclusive = false
                        }
                    }
                }

                is NavEvent.ArticleDetail -> {
                    articleDTO = it.articleDTO
                    navController.navigate("${Screen.ArticleDetail.route}/${it.type}")
                }
            }
        }
    }
    //网络错误代码
    LaunchedEffect(true) {
        commonViewModel.globalFlow.netErrFlow.collectLatest {
            when (it) {
                is NetExceptionEvent.Code401 -> {
                    navController.navigate(Screen.Login.route) {

                    }
                }

                else -> {
                    showSnackBarMsg(it.msg, snackBarHostState)
                }
            }
        }
    }
    //全局消息提心
    LaunchedEffect(true) {
        commonViewModel.globalFlow.snackBarMsgShow.collectLatest {
            showSnackBarMsg(it, snackBarHostState)
        }
    }
    //获取个人信息
    LaunchedEffect(
        true
    ) {

    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
//            startDestination = "test"
        ) {
            composable(route = Screen.Login.route) {
                LoginOrRegisterScreen(navController = navController)
            }
            composable(route = Screen.Home.route) {
                HomeScreen(commonViewModel = commonViewModel)
            }
            composable(
                route = "${Screen.PublishArticle.route}/{type}",
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type")
                PublishDiscussScreen(
                    commonViewModel = commonViewModel,
                    publishArticleViewModel = publishArticleViewModel,
                    title = type ?: "err"
                )
            }

            composable(route = "test") {
                val list = listOf(
                    "http://192.168.1.107:8080/uploads/609f2b0a-b02d-4859-b2ac-7ac36edd9ce9.jpeg",
                    "http://192.168.1.107:8080/uploads/b118e1d4-09b8-4b1b-adf3-8d13f2dcd87a.jpg",
                    "http://192.168.1.107:8080/uploads/0228a45e-4d76-494e-b0b2-c2a9634ca534.jpeg",
                    "https://pic1.zhimg.com/80/v2-a390d6364bdb5e472f411479a80686d1_1440w.webp?source=1def8aca"
                )
                ImagePreviewScreen(list)
            }
            composable(route = Screen.ImagePreviewScreen.route) {
                ImagePreviewScreen(
                    imageList = commonViewModel.imageUrlState.list,
                    startIndex = commonViewModel.imageUrlState.startIndex,
                    displayMaxHeight = commonViewModel.imageUrlState.displayHeight
                )
            }

            composable(
                route = "${Screen.ArticleDetail.route}/{type}",
                arguments = listOf(navArgument("type") { type = NavType.StringType })
            ) { backStackEntry ->
                val type = backStackEntry.arguments?.getString("type")
                ArticleDetailScreen(
                    articleDTO = articleDTO,
                    type = type ?: "err",
                    commonViewModel = commonViewModel
                )
            }
        }
    }
}

private suspend fun showSnackBarMsg(msg: String, snackbarHostState: SnackbarHostState) {
    snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
}