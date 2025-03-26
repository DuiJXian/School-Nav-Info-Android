package com.xz.schoolnavinfo.presentation.common.baidu

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import com.xz.schoolnavinfo.presentation.common.utils.DataStoreUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

val TAG = "BDMapScreen"

@Composable
fun BDMapScreen(
    modifier: Modifier = Modifier,
    viewModel: LocationMapViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val deviceState by viewModel.deviceState.collectAsState()

    val mapView = remember {
        MapView(context)
    }
    BDMapSetting.setConfig(mapView)

    //启动时地图显示到上次定位的位置
    LaunchedEffect(true) {
        var latitude = DataStoreUtils.getData(context, DataStoreUtils.Keys.LATITUDE, 39.5427)
        var longitude = DataStoreUtils.getData(context, DataStoreUtils.Keys.LONGITUDE, 116.2317)
        BDMapSetting.moveMapToLocation(LatLng(latitude, longitude))
        viewModel.moveMap.collectLatest {
            BDMapSetting.moveMapToLocation(it)
        }
    }

    BDMapSetting.setMyLocationData(
        MyLocationData
            .Builder()
            .latitude(deviceState.locationPoint.latitude)
            .longitude(deviceState.locationPoint.longitude)
            .direction(deviceState.direction)
            .build()
    )

    MapLifeCycle()
    AndroidView(
        modifier = modifier
            .fillMaxSize(),
        factory = {
            mapView
        }
    )
}

@Composable
private fun MapLifeCycle(
    locationMapViewModel: LocationMapViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val deviceState by locationMapViewModel.deviceState.collectAsState()
    DisposableEffect(lifecycleOwner, context) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    scope.launch {
                        DataStoreUtils.saveData(
                            context,
                            DataStoreUtils.Keys.LONGITUDE,
                            deviceState.locationPoint.longitude
                        )
                        DataStoreUtils.saveData(
                            context,
                            DataStoreUtils.Keys.LATITUDE,
                            deviceState.locationPoint.latitude
                        )
                    }
                    BDMapSetting.onResume()
                }

                Lifecycle.Event.ON_PAUSE -> {
                    BDMapSetting.onPause()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    BDMapSetting.isFinishSetConfig = false
                    BDMapSetting.onDestroy()
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