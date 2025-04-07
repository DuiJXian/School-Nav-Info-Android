package com.xz.schoolnavinfo.presentation

import com.xz.schoolnavinfo.presentation.theme.AppTheme
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xz.schoolnavinfo.presentation.user.LoginOrRegisterScreen
import com.xz.schoolnavinfo.presentation.common.Screen
import com.xz.schoolnavinfo.presentation.home.HomeScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //定义地图相关变量
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ){
                    composable(route = Screen.Login.route) {
                        LoginOrRegisterScreen(navController = navController)
                    }
                    composable(route = Screen.Home.route) {
                        HomeScreen(navController = navController)
                    }
                }

            }
        }
    }

}