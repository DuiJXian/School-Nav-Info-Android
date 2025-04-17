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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xz.schoolnavinfo.common.event.NetExceptionEvent
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.presentation.TestScreen
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.campus.detail.ArticleDetailScreen
import com.xz.schoolnavinfo.presentation.campus.publish.PublishArticleViewModel
import com.xz.schoolnavinfo.presentation.campus.publish.PublishDiscussScreen
import com.xz.schoolnavinfo.presentation.campus.stuff.detail.StuffDetailScreen
import com.xz.schoolnavinfo.presentation.campus.stuff.pub.PublishStuffScreen
import com.xz.schoolnavinfo.presentation.common.Screen
import com.xz.schoolnavinfo.presentation.common.baidu.select.MapLocationSelectScreen
import com.xz.schoolnavinfo.presentation.common.compose.ImagePreview
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.user.LoginOrRegisterScreen
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun NavScreen(
    commonViewModel: CommonViewModel = hiltViewModel(),
    publishArticleViewModel: PublishArticleViewModel = hiltViewModel()
) {

    val navController = rememberNavController()
    val snackBarHostState = remember { SnackbarHostState() }

    var articleDTO by remember { mutableStateOf(ArticleDTO()) }

    var stuffDetailId by remember { mutableStateOf("") }

    var articlePubMenu by remember { mutableStateOf<CampusMenu?>(null) }

    var articleDetailMenu by remember { mutableStateOf<CampusMenu?>(null) }

    //全局路由
    LaunchedEffect(true) {
        commonViewModel.navEventFlow.collectLatest {
            when (it) {
                is NavEvent.LoginOrRegister -> {
                    navController.navigate(Screen.Login.route)
                }

                is NavEvent.PublishArticle -> {
                    articlePubMenu = it.campusMenu
                    navController.navigate(Screen.PublishArticle.route)
                }

                is NavEvent.BackPage -> {
                    if (navController.currentBackStackEntry?.destination?.route != Screen.Home.route) {
                        navController.popBackStack()
                    }
                }

                is NavEvent.ImagePreview -> {
                    navController.navigate(Screen.ImagePreview.route) {
                        launchSingleTop = true
                        popUpTo(Screen.ImagePreview.route) {
                            inclusive = false
                        }
                    }
                }

                is NavEvent.ArticleDetail -> {
                    articleDTO = it.articleDTO
                    articleDetailMenu = it.campusMenu
                    navController.navigate(Screen.ArticleDetail.route)
                }

                NavEvent.MapLocationSelect -> {
                    navController.navigate(Screen.MapLocationSelect.route)
                }

                NavEvent.PublishStuff -> {
                    navController.navigate(Screen.PublishStuff.route)
                }

                is NavEvent.StuffDetail -> {
                    stuffDetailId = it.id
                    navController.navigate(Screen.StuffDetail.route)
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
        commonViewModel.globalFlow.snackBarFlow.collectLatest {
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
                route = Screen.PublishArticle.route,
            ) {
                articlePubMenu?.let {
                    PublishDiscussScreen(
                        commonViewModel = commonViewModel,
                        publishArticleViewModel = publishArticleViewModel,
                        campusMenu = it
                    )
                }

            }

            composable(route = "test") {
                TestScreen()
            }
            composable(route = Screen.ImagePreview.route) {
                ImagePreview(
                    imageList = commonViewModel.imageUrlState.list,
                    startIndex = commonViewModel.imageUrlState.startIndex,
                    displayMaxHeight = commonViewModel.imageUrlState.displayHeight
                )
            }

            composable(
                route = Screen.ArticleDetail.route
            ) {
                articleDetailMenu?.let { menu ->
                    ArticleDetailScreen(
                        articleDTO = articleDTO,
                        campusMenu = menu,
                        commonViewModel = commonViewModel
                    )
                }
            }

            composable(route = Screen.MapLocationSelect.route) {
                MapLocationSelectScreen(
                    commonViewModel = commonViewModel
                ) {
                    commonViewModel.onLocationSelectEvent(it)
                }
            }

            composable(route = Screen.PublishStuff.route) {
                PublishStuffScreen(
                    commonViewModel = commonViewModel
                )
            }

            composable(route = Screen.StuffDetail.route) {
                StuffDetailScreen(
                    id = stuffDetailId,
                    commonViewModel = commonViewModel
                )
            }
        }
    }
}

private suspend fun showSnackBarMsg(msg: String, snackbarHostState: SnackbarHostState) {
    snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
}