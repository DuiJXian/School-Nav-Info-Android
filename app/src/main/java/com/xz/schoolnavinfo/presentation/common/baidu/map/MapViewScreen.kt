package com.xz.schoolnavinfo.presentation.common.baidu.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.model.LatLng
import com.xz.schoolnavinfo.common.utils.DataStoreUtils

val TAG = "MapViewScreen"

@Composable
fun MapViewScreen(
    modifier: Modifier = Modifier,
    mapView: MapView,
    locateViewModel: LocateViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val deviceState by locateViewModel.deviceState.collectAsState()
    //启动时地图显示到上次定位的位置
//    LaunchedEffect(true) {
//        val latitude =
//            DataStoreUtils.getData(context, DataStoreUtils.Keys.LATITUDE, defaultPoint.latitude)
//        val longitude =
//            DataStoreUtils.getData(context, DataStoreUtils.Keys.LONGITUDE, defaultPoint.longitude)
//
//        if (latitude != defaultPoint.latitude) {
//            MapSetting.moveMapToLocation(LatLng(latitude, longitude))
//        }
//
//        locateViewModel.moveMap.collectLatest {
//            MapSetting.moveMapToLocation(deviceState.locationPoint)
//        }
//    }


    MapLifeCycle(mapView)
    AndroidView(
        modifier = modifier
            .fillMaxSize(),
        factory = {
            mapView
        }
    )
}

@Composable
private fun MapLifeCycle(mapView: MapView) {
    rememberCoroutineScope()
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