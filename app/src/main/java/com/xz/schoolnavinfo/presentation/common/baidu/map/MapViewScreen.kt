package com.xz.schoolnavinfo.presentation.common.baidu.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.baidu.mapapi.map.MapView

@Composable
fun MapViewScreen(
    modifier: Modifier = Modifier,
    mapView: MapView,
) {
    MapViewLifeCycle(mapView)
    AndroidView(
        modifier = modifier
            .fillMaxSize(),
        factory = {
            mapView
        }
    )
}

@Composable
private fun MapViewLifeCycle(mapView: MapView) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, context) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mapView.onPause()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}