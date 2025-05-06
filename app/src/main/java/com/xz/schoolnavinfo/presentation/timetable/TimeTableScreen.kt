package com.xz.schoolnavinfo.presentation.timetable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngineCache

@Composable
fun TimetableScreen() {

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val flutterView = FlutterView(context)
            val flutterEngine = FlutterEngineCache.getInstance().get("flutter_engine_id")

            if (flutterEngine != null) {
                flutterView.attachToFlutterEngine(flutterEngine)
            }
            flutterView
        }
    )

}