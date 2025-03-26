package com.xz.schoolnavinfo.presentation.map

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener
import com.baidu.mapapi.map.MapPoi
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay
import com.baidu.mapapi.search.route.BikingRouteResult
import com.baidu.mapapi.search.route.DrivingRouteResult
import com.baidu.mapapi.search.route.IndoorRouteResult
import com.baidu.mapapi.search.route.IntegralRouteResult
import com.baidu.mapapi.search.route.MassTransitRouteResult
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener
import com.baidu.mapapi.search.route.TransitRouteResult
import com.baidu.mapapi.search.route.WalkingRouteResult
import com.xz.schoolnavinfo.presentation.common.baidu.BDMapScreen
import com.xz.schoolnavinfo.presentation.common.baidu.BDMapSetting
import com.xz.schoolnavinfo.presentation.common.baidu.LocateEvent
import com.xz.schoolnavinfo.presentation.common.baidu.LocationMapViewModel
import com.xz.schoolnavinfo.presentation.common.baidu.MapUiEvent
import com.xz.schoolnavinfo.presentation.common.components.CheckGps
import com.xz.schoolnavinfo.presentation.common.components.CheckPermission
import com.xz.schoolnavinfo.presentation.common.utils.DataStoreUtils
import com.xz.schoolnavinfo.presentation.common.utils.LocateUtils
import com.xz.schoolnavinfo.presentation.common.utils.TimeUtils
import com.xz.schoolnavinfo.presentation.map.components.LocateNow
import com.xz.schoolnavinfo.presentation.map.components.PoiDetailCard
import com.xz.schoolnavinfo.presentation.map.components.PoiSearch
import com.xz.schoolnavinfo.presentation.map.components.QuickViaItem
import com.xz.schoolnavinfo.presentation.map.components.RoutePlanChange
import kotlinx.coroutines.delay


val TAG = "MapScreen"

@Composable
fun MapScreen(
    locationMapViewModel: LocationMapViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel()
) {
    val deviceState by locationMapViewModel.deviceState.collectAsState()
    val searchTextFieldState by mapViewModel.searchTextFiledState.collectAsState()
    val context = LocalContext.current

    var isShowRoutePlanChange by remember { mutableStateOf(false) }
    var routeInfoDistance by remember { mutableStateOf("") }
    var routeInfoDuration by remember { mutableStateOf("") }

    LocateCheck()
    LaunchedEffect(true) {
        locationMapViewModel.startLocation()
        BDMapSetting.setOnMapClickListener(object : OnMapClickListener {
            override fun onMapClick(point: LatLng?) {

            }

            override fun onMapPoiClick(poi: MapPoi?) {
                if (poi != null) {
                    mapViewModel.onEvent(MapEvent.GetPoiDetailInfo(poi.uid))
                    locationMapViewModel.uiEvent(MapUiEvent.MoveToLocation(poi.position))
                }

            }
        })
    }

    //将最新的位置信息保存到本地
    LaunchedEffect(deviceState.locationPoint) {
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
        delay(10000)
    }

    Box {
        BDMapScreen()
        PoiSearch(
            onTextChange = {
                mapViewModel.onEvent(MapEvent.SearchTextChange(it, deviceState.locationPoint))
            }, onClose = {
                mapViewModel.onEvent(MapEvent.ClearSearchText)
            }, onClickItem = {
                mapViewModel.onEvent(MapEvent.GetPoiDetailInfo(it.uid))
                locationMapViewModel.uiEvent(MapUiEvent.MoveToLocation(it.location))
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
                        onRoute = {
                            BDMapSetting.routePlanDestroy()
                            val onGetPlanListener = object : OnGetRoutePlanResultListener {
                                override fun onGetWalkingRouteResult(res: WalkingRouteResult) {
                                    val overlay = WalkingRouteOverlay(BDMapSetting.baiduMap)
                                    if (res.routeLines.size > 0) {
                                        val routeLine = res.routeLines[0]
                                        overlay.setData(routeLine)
                                        overlay.addToMap()
                                        routeInfoDistance = LocateUtils.metersToKilometers(routeLine.distance)
                                        routeInfoDuration = TimeUtils.formatTime(routeLine.duration)
                                        isShowRoutePlanChange = true
                                    }
                                }

                                override fun onGetTransitRouteResult(p0: TransitRouteResult?) {}
                                override fun onGetMassTransitRouteResult(p0: MassTransitRouteResult?) {}
                                override fun onGetDrivingRouteResult(p0: DrivingRouteResult?) {}
                                override fun onGetIndoorRouteResult(p0: IndoorRouteResult?) {}
                                override fun onGetBikingRouteResult(p0: BikingRouteResult?) {}
                                override fun onGetIntegralRouteResult(p0: IntegralRouteResult?) {}
                            }
                            BDMapSetting.routePlan(
                                onGetPlanListener,
                                deviceState.locationPoint,
                                it.location
                            )
                            mapViewModel.onEvent(MapEvent.CloseDetailCard)
                        },
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$it")
                            }
                            // 检查是否有 Activity 处理此 Intent
                            if (intent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(
                                    context,
                                    "当前设备不支持拨号功能",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
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
                    locationMapViewModel.uiEvent(MapUiEvent.MoveNowLocation)
                }
        )
        QuickViaItem(
            Modifier.align(Alignment.BottomStart)
        )

        if(isShowRoutePlanChange){
            RoutePlanChange(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp),
                onCancel = {
                    isShowRoutePlanChange = false
                    BDMapSetting.routePlanDestroy()
                },
                onNavi = {

                },
                routeInfoDistance,
                routeInfoDuration
            )
        }

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
