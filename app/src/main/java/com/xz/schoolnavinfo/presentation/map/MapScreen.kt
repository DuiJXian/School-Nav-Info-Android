package com.xz.schoolnavinfo.presentation.map

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.xz.schoolnavinfo.presentation.common.baidu.BDMapScreen
import com.xz.schoolnavinfo.presentation.common.baidu.LocateEvent
import com.xz.schoolnavinfo.presentation.common.baidu.LocationMapViewModel
import com.xz.schoolnavinfo.presentation.common.baidu.MapUiEvent
import com.xz.schoolnavinfo.presentation.common.components.CheckGps
import com.xz.schoolnavinfo.presentation.common.components.CheckPermission
import com.xz.schoolnavinfo.presentation.common.utils.DataStoreUtils
import com.xz.schoolnavinfo.presentation.common.utils.LocateUtils
import com.xz.schoolnavinfo.presentation.map.components.LocateNow
import com.xz.schoolnavinfo.presentation.map.components.PoiDetailCard
import com.xz.schoolnavinfo.presentation.map.components.PoiSearch
import com.xz.schoolnavinfo.presentation.map.components.QuickViaItem
import kotlinx.coroutines.delay


@Composable
fun MapScreen(
    locationMapViewModel: LocationMapViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel()
) {
    val deviceState by locationMapViewModel.deviceState.collectAsState()
    val searchTextFieldState by mapViewModel.searchTextFiledState.collectAsState()
    val context = LocalContext.current

    LocateCheck()
    LaunchedEffect(true) {
        locationMapViewModel.startLocation()
    }

    //将最新的位置信息保存到本地
    LaunchedEffect(deviceState.locationPoint) {
        DataStoreUtils.saveData(context, DataStoreUtils.Keys.LONGITUDE, deviceState.locationPoint.longitude)
        DataStoreUtils.saveData(context, DataStoreUtils.Keys.LATITUDE, deviceState.locationPoint.latitude)
        delay(5000)
    }

    Box {
        BDMapScreen()
        PoiSearch(
            onTextChange = {
                mapViewModel.onEvent(MapEvent.SearchTextChange(it, deviceState.locationPoint))
            }
            , onClose = {
                mapViewModel.onEvent(MapEvent.ClearSearchText)
            }
            , onClickItem = {
                mapViewModel.onEvent(MapEvent.GetPoiDetailInfo(it))
            }
        )
        if (searchTextFieldState.isShowDetailCard) {
            searchTextFieldState.poiDetailInfo?.let {
                Box(
                    modifier = Modifier
                        .padding(10.dp)
                        .align(Alignment.Center)
                ) {
                    PoiDetailCard(
                        modifier = Modifier.align(Alignment.Center),
                        poiDetailInfo = it,
                        onCancel = {
                            mapViewModel.onEvent(MapEvent.CloseDetailCard)
                        },
                        onGo = {

                        }
                    )
                }
            }
        }
        LocateNow(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 10.dp)
                .clickable {
                    locationMapViewModel.uiEvent(MapUiEvent.MapMove)
                }
        )
        QuickViaItem(
            Modifier.align(Alignment.BottomStart)
        )
    }
}

@Composable
private fun LocateCheck(viewModel: LocationMapViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    CheckGps()
    CheckPermission()
    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    val isOpenGps = LocateUtils.isGpsEnabled(context)
                    val isGrantedPermission = LocateUtils.isGrantedLocationPermission(context)
                    viewModel.locateEvent(LocateEvent.GpsChange(isOpenGps))
                    viewModel.locateEvent(LocateEvent.PermissionChange(isGrantedPermission))
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
