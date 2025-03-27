package com.xz.schoolnavinfo.presentation.map

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener
import com.baidu.mapapi.map.MapPoi
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.overlayutil.BikingRouteOverlay
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay
import com.baidu.mapapi.search.route.BikingRouteResult
import com.baidu.mapapi.search.route.DrivingRouteResult
import com.baidu.mapapi.search.route.IndoorRouteResult
import com.baidu.mapapi.search.route.IntegralRouteResult
import com.baidu.mapapi.search.route.MassTransitRouteResult
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener
import com.baidu.mapapi.search.route.TransitRouteResult
import com.baidu.mapapi.search.route.WalkingRouteResult
import com.xz.schoolnavinfo.domain.model.MPoiInfo
import com.xz.schoolnavinfo.presentation.common.baidu.BDMapScreen
import com.xz.schoolnavinfo.presentation.common.baidu.BDMapSetting
import com.xz.schoolnavinfo.presentation.common.baidu.LocateEvent
import com.xz.schoolnavinfo.presentation.common.baidu.LocationMapViewModel
import com.xz.schoolnavinfo.presentation.common.baidu.MapUiEvent
import com.xz.schoolnavinfo.presentation.common.baidu.RoutePlanType
import com.xz.schoolnavinfo.presentation.common.components.CheckGps
import com.xz.schoolnavinfo.presentation.common.components.CheckPermission
import com.xz.schoolnavinfo.presentation.common.utils.DataStoreUtils
import com.xz.schoolnavinfo.presentation.common.utils.LocateUtils
import com.xz.schoolnavinfo.presentation.common.utils.TimeUtils
import com.xz.schoolnavinfo.presentation.map.components.LocateNow
import com.xz.schoolnavinfo.presentation.map.components.PoiDetailCard
import com.xz.schoolnavinfo.presentation.map.components.PoiSearch
import com.xz.schoolnavinfo.presentation.map.components.QuickViaItem
import com.xz.schoolnavinfo.presentation.map.components.RoutePlan
import kotlinx.coroutines.launch


val TAG = "MapScreen"

@Composable
fun MapScreen(
    locationMapViewModel: LocationMapViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),
) {
    val deviceState by locationMapViewModel.deviceState.collectAsState()
    val poiState by mapViewModel.poiState.collectAsState()
    var routePlanType: RoutePlanType by remember { mutableStateOf(RoutePlanType.Walking) }
    var isShowQuickVia by remember { mutableStateOf(true) }
    val mPoiInfos by mapViewModel.mPoiInfos.collectAsState()
    val routeState by mapViewModel.routeState
    val context = LocalContext.current
    var scope = rememberCoroutineScope()


    LocateCheck()
    LaunchedEffect(true) {
        locationMapViewModel.startLocation()
        BDMapSetting.setOnMapClickListener(object : OnMapClickListener {
            override fun onMapClick(point: LatLng?) {}
            override fun onMapPoiClick(poi: MapPoi?) {
                if (poi != null) {
                    mapViewModel.onPoiEvent(PoiEvent.GetPoiDetailInfo(poi.uid))
                    locationMapViewModel.uiEvent(MapUiEvent.MoveToLocation(poi.position))
                    mapViewModel.onMPoiInfoEvent(MPoiInfoEvent.GetMPoiInfoByUid(poi.uid))
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
        mapViewModel.onPoiEvent(PoiEvent.CenterPointChange(deviceState.locationPoint))
    }

    Box {
        //地图
        BDMapScreen()

        //搜索
        if (poiState.isShowSearch) {
            PoiSearch(
                onTextChange = {
                    mapViewModel.onPoiEvent(PoiEvent.SearchTextChange(it))
                }, onClose = {
                    mapViewModel.onPoiEvent(PoiEvent.ClearSearchText)
                }, onClickItem = {
                    mapViewModel.onPoiEvent(PoiEvent.GetPoiDetailInfo(it.uid))
                    locationMapViewModel.uiEvent(MapUiEvent.MoveToLocation(it.location))
                    mapViewModel.onMPoiInfoEvent(MPoiInfoEvent.GetMPoiInfoByUid(it.uid))
                }
            )
        }
        //定位
        LocateNow(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 100.dp, end = 10.dp)
                .clickable {
                    BDMapSetting.moveMapToLocation(deviceState.locationPoint)
                }
        )
        //收藏
        if (isShowQuickVia) {
            QuickViaItem(
                modifier = Modifier.align(Alignment.BottomStart),
                mPoiInfos = mPoiInfos,
                onClickItem = {
                    mapViewModel.onPoiEvent(PoiEvent.GetPoiDetailInfo(it.uid))
                    mapViewModel.onMPoiInfoEvent(MPoiInfoEvent.GetMPoiInfoByUid(it.uid))
                }
            )
        }
        //poi详情
        if (poiState.isShowDetailCard) {
            isShowQuickVia = false
            poiState.poiDetailInfo?.let {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)) // 半透明背景
                        .clickable(enabled = false) { }
                        .padding(10.dp)
                        .align(Alignment.Center)
                ) {
                    PoiDetailCard(
                        modifier = Modifier.align(Alignment.Center),
                        poiDetailInfo = it,
                        onCancel = {
                            isShowQuickVia = true
                            mapViewModel.onPoiEvent(PoiEvent.CloseDetailCard)
                        },
                        onRoute = {
                            mapViewModel.onPoiEvent(PoiEvent.IsShowSearchPoi(false))
                            onRoutePlan(
                                mapViewModel = mapViewModel,
                                endLocation = it.location
                            )
                            BDMapSetting.startRoutePlan(RoutePlanType.Walking)
                            BDMapSetting.moveMapToLocation(it.location, 14f)
                            mapViewModel.onPoiEvent(PoiEvent.CloseDetailCard)
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
                        },
                        onFavorite = {
                            val item = MPoiInfo(
                                uid = it.uid,
                                name = it.name,
                                order = mPoiInfos.size + 1,
                                iconPic = "",
                                address = it.address,
                                telephone = it.telephone
                            )
                            scope.launch {
                                mapViewModel.onMPoiInfoEvent(MPoiInfoEvent.InsertMPoiInfo(item))
                            }
                        }
                    )
                }
            }
        }
        //路线
        if (routeState.isShowRoutePlan) {
            RoutePlan(
                onCancel = {
                    isShowQuickVia = true
                    mapViewModel.onRouteEvent(RouteEvent.IsShowChange(false))
                    mapViewModel.onPoiEvent(PoiEvent.IsShowSearchPoi(true))
                    BDMapSetting.removeOverlay()
                },
                onRoutePlanType = {
                    mapViewModel.onPoiEvent(PoiEvent.IsShowSearchPoi(false))
                    routePlanType = it
                    BDMapSetting.startRoutePlan(it)
                },
                onNavi = {

                },
                distance = routeState.routeDistance,
                duration = routeState.routeDuration,
                routePlanType = routePlanType
            )
        }

    }
}

private fun onRoutePlan(
    mapViewModel: MapViewModel,
    endLocation: LatLng
) {
    val onGetPlanListener = object : OnGetRoutePlanResultListener {
        override fun onGetWalkingRouteResult(walkingRes: WalkingRouteResult) {
            val overlay = WalkingRouteOverlay(BDMapSetting.baiduMap)
            BDMapSetting.setOverlayManager(overlay)
            if (walkingRes.routeLines.size > 0) {
                val routeLine = walkingRes.routeLines[0]
                overlay.setData(routeLine)
                overlay.addToMap()
                val distance = LocateUtils.metersToKilometers(routeLine.distance)
                val duration = TimeUtils.formatTime(routeLine.duration)
                mapViewModel.onRouteEvent(RouteEvent.DisAndDurChange(distance, duration))
                mapViewModel.onRouteEvent(RouteEvent.IsShowChange(true))
            }

        }

        override fun onGetTransitRouteResult(p0: TransitRouteResult?) {}
        override fun onGetMassTransitRouteResult(p0: MassTransitRouteResult?) {}
        override fun onGetDrivingRouteResult(drivingRes: DrivingRouteResult) {
            val overlay = DrivingRouteOverlay(BDMapSetting.baiduMap)
            BDMapSetting.setOverlayManager(overlay)
            if (drivingRes.routeLines.size > 0) {
                val routeLine = drivingRes.routeLines[0]
                overlay.setData(routeLine)
                overlay.addToMap()
                val distance = LocateUtils.metersToKilometers(routeLine.distance)
                val duration = TimeUtils.formatTime(routeLine.duration)
                mapViewModel.onRouteEvent(RouteEvent.DisAndDurChange(distance, duration))
                mapViewModel.onRouteEvent(RouteEvent.IsShowChange(true))
            }
        }

        override fun onGetIndoorRouteResult(p0: IndoorRouteResult?) {}
        override fun onGetBikingRouteResult(bikingRes: BikingRouteResult) {
            val overlay = BikingRouteOverlay(BDMapSetting.baiduMap)
            BDMapSetting.setOverlayManager(overlay)
            if (bikingRes.routeLines.size > 0) {
                val routeLine = bikingRes.routeLines[0]
                overlay.setData(routeLine)
                overlay.addToMap()
                val distance = LocateUtils.metersToKilometers(routeLine.distance)
                val duration = TimeUtils.formatTime(routeLine.duration)
                mapViewModel.onRouteEvent(RouteEvent.DisAndDurChange(distance, duration))
                mapViewModel.onRouteEvent(RouteEvent.IsShowChange(true))
            }
        }

        override fun onGetIntegralRouteResult(p0: IntegralRouteResult?) {}
    }
    BDMapSetting.setRoutePlanListener(
        listener = onGetPlanListener,
        endPoint = endLocation
    )
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
