package com.xz.schoolnavinfo.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.xz.schoolnavinfo.presentation.home.NavScreen
import com.xz.schoolnavinfo.presentation.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var flutterEngine: FlutterEngine

    //定义地图相关变量
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        initFlutterEngine()
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF0091EA),
                    primaryContainer = Color(0xFF91D3FF)
                )
            ) {
                AppTheme {
                    NavScreen()
                }
            }

        }
    }

    private fun initFlutterEngine() {
        flutterEngine = FlutterEngine(this)
        flutterEngine.navigationChannel.setInitialRoute("/")
        flutterEngine.lifecycleChannel.appIsResumed()
        flutterEngine.dartExecutor.executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        FlutterEngineCache.getInstance().put("flutter_engine_id", flutterEngine)
    }

    override fun onDestroy() {
        super.onDestroy()
        flutterEngine.lifecycleChannel.appIsInactive()
        flutterEngine.destroy()
    }
}