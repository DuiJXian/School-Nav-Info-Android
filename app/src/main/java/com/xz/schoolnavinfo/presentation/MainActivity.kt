package com.xz.schoolnavinfo.presentation

import AppTheme
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Scaffold
import androidx.core.view.WindowCompat
import com.baidu.location.LocationClient
import com.baidu.mapapi.SDKInitializer
import com.xz.schoolnavinfo.presentation.home.HomeScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    //定义地图相关变量
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //设置隐私协议
        SDKInitializer.setAgreePrivacy(applicationContext, true)
        SDKInitializer.initialize(applicationContext)
        LocationClient.setAgreePrivacy(true)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold {
                    HomeScreen()
                }
            }
        }
    }
}