package com.xz.schoolnavinfo.presentation.home

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xz.schoolnavinfo.common.net.NetException
import com.xz.schoolnavinfo.presentation.campus.discuss.publish.PublishDiscussScreen
import com.xz.schoolnavinfo.presentation.common.Screen
import com.xz.schoolnavinfo.presentation.common.compose.HiltActivityViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.user.LoginOrRegisterScreen
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavScreen() {

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val commonViewModel = hiltViewModel<CommonViewModel>()
    LaunchedEffect(true) {
        //nav事件
        commonViewModel.navEvent.collectLatest {
            when (it) {
                NavEvent.ArticleDetail -> {
                    navController.navigate(Screen.ArticleDetail.route)
                }

                NavEvent.LoginOrRegister -> {
                    navController.navigate(Screen.Login.route)
                }

                NavEvent.PublishDiscuss -> {
                    navController.navigate(Screen.PublishDiscuss.route)
                }
            }
        }
        //网络异常事件
        commonViewModel.netExceptionFlow.collectLatest {
            when (it) {
                is NetException.Code401 -> {
                    navController.navigate(Screen.Login.route) {

                    }
                }

                is NetException.Code403 -> {
                    snackbarHostState.showSnackbar(it.msg, duration = SnackbarDuration.Short)
                }

                else -> {
                    snackbarHostState.showSnackbar(it.msg, duration = SnackbarDuration.Short)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        NavHost(
            navController = navController,
            startDestination = Screen.PublishDiscuss.route
        ) {
            composable(route = Screen.Login.route) {
                LoginOrRegisterScreen(navController = navController)
            }
            composable(route = Screen.Home.route) {
                HomeScreen()
            }
            composable(route = Screen.PublishDiscuss.route) {
                PublishDiscussScreen()
            }
        }
    }
}