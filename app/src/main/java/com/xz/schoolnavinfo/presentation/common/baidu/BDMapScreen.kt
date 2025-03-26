package com.xz.schoolnavinfo.presentation.common.baidu

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import com.xz.schoolnavinfo.presentation.common.utils.DataStoreUtils
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

val TAG ="BDMapScreen"
@Composable
fun BDMapScreen(
    modifier: Modifier = Modifier,
    viewModel: LocationMapViewModel = hiltViewModel(),
    bdMapSetting: BDMapSetting = BDMapSetting
) {
    val context = LocalContext.current
    val deviceState by viewModel.deviceState.collectAsState()

    val configuration = LocalConfiguration.current
    val isLandscape = remember(configuration) {
        configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    }

    val mapView = remember {
        MapView(context)
    }

    //首次定位移动到当前位置
//    LaunchedEffect(true) {
//        bdMapSetting.setConfig(mapView)
//        viewModel.firstMove.collectLatest {
//            moveMapToLocation(it, mapView)
//        }
//    }
    LaunchedEffect(true) {
        bdMapSetting.setConfig(mapView)
        var latitude = DataStoreUtils.getData(context, DataStoreUtils.Keys.LATITUDE, 39.5427)
        var longitude = DataStoreUtils.getData(context, DataStoreUtils.Keys.LONGITUDE, 116.2317)

        Log.e(TAG, "LaunchedEffect: $latitude $longitude")
        moveMapToLocation(LatLng(latitude, longitude), mapView)
        viewModel.moveMap.collectLatest {
            moveMapToLocation(it, mapView)
        }
    }

    //屏幕旋转恢复到当前位置
    LaunchedEffect(isLandscape) {
        if (deviceState.locationPoint.latitude != 39.5427){
            moveMapToLocation(deviceState.locationPoint, mapView)
        }
    }

    mapView.map.setMyLocationData(
        MyLocationData
            .Builder()
            .latitude(deviceState.locationPoint.latitude)
            .longitude(deviceState.locationPoint.longitude)
            .direction(deviceState.direction)
            .build()
    )

    MapLifeCycle(mapView, viewModel)
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
    mapView: MapView,
    locationMapViewModel: LocationMapViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val deviceState by locationMapViewModel.deviceState.collectAsState()
    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    scope.launch {
                        DataStoreUtils.saveData(context, DataStoreUtils.Keys.LONGITUDE, deviceState.locationPoint.longitude)
                        DataStoreUtils.saveData(context, DataStoreUtils.Keys.LATITUDE, deviceState.locationPoint.latitude)
                    }
                    BDMapSetting.onResume()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    BDMapSetting.onPause()
                }

                Lifecycle.Event.ON_DESTROY -> {
                    BDMapSetting.onDestroy()
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            mapView.onDestroy()
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}

private fun moveMapToLocation(
    latLng: LatLng,
    mapView: MapView
) {
    mapView.map.animateMapStatus(
        MapStatusUpdateFactory.newLatLngZoom(
            latLng,
            18f
        )
    )
}