package com.xz.schoolnavinfo.presentation.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.Gravity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.baidu.mapapi.model.LatLng
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.xz.schoolnavinfo.common.utils.LocationUtils
import com.xz.schoolnavinfo.presentation.common.baidu.map.LocateViewModel
import com.xz.schoolnavinfo.presentation.common.baidu.map.MapViewScreen
import com.xz.schoolnavinfo.presentation.common.baidu.map.scrollMapView
import com.xz.schoolnavinfo.presentation.common.baidu.nav.activity.BNaviGuideActivity
import com.xz.schoolnavinfo.presentation.common.baidu.nav.activity.DrivingActivity
import com.xz.schoolnavinfo.presentation.common.baidu.nav.activity.WNaviGuideActivity
import com.xz.schoolnavinfo.presentation.common.components.OpeGpsDialog
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.map.composable.PoiDetailSection
import com.xz.schoolnavinfo.presentation.map.composable.PoiSearchSection
import com.xz.schoolnavinfo.presentation.map.composable.QuickViaSection
import com.xz.schoolnavinfo.presentation.map.composable.RoutePlan
import com.xz.schoolnavinfo.presentation.map.composable.ScrollMapIcon
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.flow.collectLatest


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapScreen(
    locateViewModel: LocateViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel,
    mapViewModel: MapViewModel = hiltViewModel(),
) {

    val deviceState by locateViewModel.deviceState.collectAsStateWithLifecycle()
    val dataState by mapViewModel.dataState.collectAsStateWithLifecycle()
    val showState by mapViewModel.showState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarHostState)
        },
    ) {
        MapViewContent(
            mapViewModel = mapViewModel,
            deviceLocation = deviceState.locationPoint,
            dataUiState = dataState,
            showUiState = showState
        )
    }

    ManageLifeCycle()
    RunCoroutine(mapViewModel, commonViewModel, locateViewModel, snackBarHostState)
}

@Composable
private fun RunCoroutine(
    mapViewModel: MapViewModel,
    commonViewModel: CommonViewModel,
    locateViewModel: LocateViewModel,
    snackBarHostState: SnackbarHostState
) {
    val mapView = mapViewModel.mapView
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val deviceState by locateViewModel.deviceState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        commonViewModel.routePlanFlow.collectLatest {
            mapViewModel.startRoutePlan(
                RouteType.Walking,
                deviceState.locationPoint,
                it
            )
        }
    }
    LaunchedEffect(Unit) {
        locateViewModel.startLocation()
        mapViewModel.compositionOver(isDark,LocationUtils.loadLocation(context))
        locateViewModel.firstScrollMap.collectLatest {
            mapView.map.scrollMapView(deviceState.locationPoint)
        }
    }
    LaunchedEffect(Unit) {
        mapViewModel.uiEvent.collectLatest { event ->
            when (event) {
                is MapViewUiEvent.CalculateMsg -> {
                    snackBarHostState.showSnackbar(
                        message = event.msg, duration = SnackbarDuration.Short
                    )
                }

                is MapViewUiEvent.EnterNav -> {
                    val intent = when (event.routeType) {
                        RouteType.Walking -> Intent(context, WNaviGuideActivity::class.java)
                        RouteType.Biking -> Intent(context, BNaviGuideActivity::class.java)
                        RouteType.Driving -> {
                            BaiduNaviManagerFactory.getBaiduNaviManager().startLocationMonitor()
                            Intent(context, DrivingActivity::class.java)
                        }
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
    LaunchedEffect(deviceState) {
        mapViewModel.updateMapViewLocation(deviceState.locationPoint, deviceState.direction)
    }
}

@Composable
private fun MapViewContent(
    mapViewModel: MapViewModel,
    deviceLocation: LatLng,
    dataUiState: MapViewDataUiState,
    showUiState: MapViewShowUiState,
) {

    val context = LocalContext.current
    Box {
        MapViewScreen(mapView = mapViewModel.mapView)


        PoiSearchSection(
            onTextChange = { text, location -> mapViewModel.searchTextChange(text, location) },
            onClearSearchText = { mapViewModel.clearSearchText() },
            onFocusChange = { mapViewModel.searchTextFocusChange(it) },
            onClickItem = { location, uid -> mapViewModel.clickSearchItem(location, uid) },
            searchText = dataUiState.searchText,
            showTextField = !showUiState.showRoutePlan,
            showSearchRes = showUiState.showPoiSearchData,
            deviceLocation = deviceLocation,
            searchPoiInfos = dataUiState.searchPoiInfos
        )

        ScrollMapIcon(Modifier
            .align(Alignment.BottomEnd)
            .padding(bottom = 100.dp, end = 10.dp)
            .clip(CircleShape)
            .clickable { mapViewModel.scrollMap(deviceLocation, true) })

        QuickViaSection(
            showQuickBar = !showUiState.showRoutePlan && dataUiState.localPoiInfos.isNotEmpty(),
            showQuickEdit = showUiState.showQuickEdit,
            localPoiInfos = dataUiState.localPoiInfos,
            localPoiInfo = dataUiState.localPoiInfo,
            onClickItem = { mapViewModel.clickQuickViaItem(deviceLocation, it) },
            onLongClickItem = { mapViewModel.longClickQuickViaItem(it) },
            onDelete = { mapViewModel.deleteLocalPoiInfo() },
            onConfirm = { mapViewModel.updateLocalPoiInfo(it) },
            onCancel = { mapViewModel.setShowQuickEdit(false) }
        )

        PoiDetailSection(
            showPoiDetailSection = showUiState.showPoiDetail,
            isFavorite = dataUiState.localPoiInfo != null,
            poiDetailInfo = dataUiState.poiDetailInfo,
            onCancel = { mapViewModel.setShowPoiDetail(false) },
            onRoute = {
                mapViewModel.startRoutePlan(
                    RouteType.Walking,
                    deviceLocation,
                    dataUiState.poiDetailInfo.location
                )
            },
            onCall = { onCall(it, context) },
            onFavorite = { mapViewModel.addOrRemoveQuickViaItem() }
        )

        RoutePlan(
            modifier = Modifier.align(Alignment.BottomCenter),
            visible = showUiState.showRoutePlan,
            distance = dataUiState.routeDistance,
            duration = dataUiState.routeDuration,
            routeType = dataUiState.routeType,
            onCancel = { mapViewModel.closeRoutePlan() },
            onRouteTypeChange = {
                mapViewModel.startRoutePlan(
                    it, deviceLocation, dataUiState.poiDetailInfo.location
                )
            },
            onNavi = {
                mapViewModel.onNavEvent(deviceLocation, dataUiState.poiDetailInfo.location)
            },
        )
    }
}

private fun onCall(it: String, context: Context) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$it")
    }
    val hasTelephony = context.packageManager.hasSystemFeature(
        PackageManager.FEATURE_TELEPHONY
    )
    if (hasTelephony) {
        context.startActivity(intent)
    } else {
        StyleableToast.Builder(context).text("当前设备不支持拨号功能")
            .textColor(Color.White.toArgb())
            .backgroundColor(Color(0xFF0091EA).toArgb()).cornerRadius(36)
            .gravity(Gravity.TOP).show()
    }
}

@Composable
private fun ManageLifeCycle(locateViewModel: LocateViewModel = hiltViewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current
    OpeGpsDialog()
    CheckPermission()
    DisposableEffect(lifecycleOwner) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    locateViewModel.stopLocation()
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

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermission() {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    when{
        //已授予
        permissionsState.allPermissionsGranted -> {

        }
        //被拒绝一次
        permissionsState.shouldShowRationale -> {

        }
        //拒绝多次
        else -> {

        }
    }
    if (!permissionsState.allPermissionsGranted){
        LaunchedEffect(permissionsState) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
}
