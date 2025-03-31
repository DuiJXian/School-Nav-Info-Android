package com.xz.schoolnavinfo.presentation.map

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory
import com.xz.schoolnavinfo.domain.model.LocalPoiInfo
import com.xz.schoolnavinfo.presentation.common.baidu.BDMapScreen
import com.xz.schoolnavinfo.presentation.common.baidu.BDMapSetting
import com.xz.schoolnavinfo.presentation.common.baidu.LocateEvent
import com.xz.schoolnavinfo.presentation.common.baidu.LocateViewModel
import com.xz.schoolnavinfo.presentation.common.baidu.RoutePlanType
import com.xz.schoolnavinfo.presentation.common.baidu.nav.activity.BNaviGuideActivity
import com.xz.schoolnavinfo.presentation.common.baidu.nav.activity.DemoGuideActivity
import com.xz.schoolnavinfo.presentation.common.baidu.nav.activity.WNaviGuideActivity
import com.xz.schoolnavinfo.presentation.common.components.CheckGps
import com.xz.schoolnavinfo.presentation.common.components.CheckPermission
import com.xz.schoolnavinfo.presentation.common.utils.DataStoreUtils
import com.xz.schoolnavinfo.presentation.common.utils.LocateUtils
import com.xz.schoolnavinfo.presentation.common.utils.TimeUtils
import com.xz.schoolnavinfo.presentation.map.components.FavoriteItemEdit
import com.xz.schoolnavinfo.presentation.map.components.LocateNow
import com.xz.schoolnavinfo.presentation.map.components.PoiDetailCard
import com.xz.schoolnavinfo.presentation.map.components.PoiSearch
import com.xz.schoolnavinfo.presentation.map.components.QuickViaItem
import com.xz.schoolnavinfo.presentation.map.components.RoutePlan
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


val TAG = "MapScreen"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapScreen(
    locationMapViewModel: LocateViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel(),
) {
    //设备状态
    val deviceState by locationMapViewModel.deviceState.collectAsState()
    //poi状态
    val poiState by mapViewModel.poiState.collectAsState()
    //路线规划类型
    var routePlanType: RoutePlanType by remember { mutableStateOf(RoutePlanType.Walking) }
    //是否显示快速访问
    var isShowQuickVia by remember { mutableStateOf(true) }
    //快速访问数据
    val mPoiInfos by mapViewModel.localPoiState.collectAsState()
    //路线状态
    val routeState by mapViewModel.routeState
    //收藏编辑
    var isShowEditFavorite by remember { mutableStateOf(false) }
    var mPoiInfo by remember { mutableStateOf<LocalPoiInfo?>(null) }
    val localPoiState by mapViewModel.localPoiState.collectAsState()


    val context = LocalContext.current
    var scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }


    //检查定位和权限
    ManageLifeCycle()

    LaunchedEffect(true) {
        //开始定位
        locationMapViewModel.startLocation()
        //点击事件
        BDMapSetting.setOnMapClickListener(object : OnMapClickListener {
            override fun onMapClick(point: LatLng?) {}
            override fun onMapPoiClick(poi: MapPoi?) {
                if (poi != null) {
                    mapViewModel.onPoiEvent(PoiEvent.GetPoiDetailInfo(poi.uid))
                    mapViewModel.onMPoiInfoEvent(LocalInfoEvent.GetMPoiInfoByUid(poi.uid))
                }

            }
        })
        //导航消息
        mapViewModel.navEvent.collectLatest { event ->
            when (event) {
                is NavMsgEvent.CalculateMsg -> {
                    snackbarHostState.showSnackbar(
                        message = event.msg,
                        duration = SnackbarDuration.Short
                    )
                }

                is NavMsgEvent.EnterNav -> {
                    val intent = when(event.routePlanType){
                        is RoutePlanType.Walking -> Intent(context, WNaviGuideActivity::class.java)
                        is RoutePlanType.Biking -> Intent(context, BNaviGuideActivity::class.java)
                        is RoutePlanType.Driving -> {
                            //开启导航的定位
                            BaiduNaviManagerFactory.getBaiduNaviManager().stopLocationMonitor()
                            Intent(context, DemoGuideActivity::class.java)
                        }
                    }
                    context.startActivity(intent)
                }
            }
        }
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

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) {
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
                        mapViewModel.onMPoiInfoEvent(LocalInfoEvent.GetMPoiInfoByUid(it.uid))
                    }
                )
            }
            //定位
            LocateNow(
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 100.dp, end = 10.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        BDMapSetting.moveMapToLocation(deviceState.locationPoint)
                    }
            )
            //快速访问
            if (isShowQuickVia) {
                QuickViaItem(
                    modifier = Modifier.align(Alignment.BottomStart),
                    localPoiInfos = localPoiState.localPoiInfos,
                    onClickItem = {
                        mapViewModel.onPoiEvent(PoiEvent.GetPoiDetailInfo(it.uid))
                        mapViewModel.onMPoiInfoEvent(LocalInfoEvent.GetMPoiInfoByUid(it.uid))
                    },
                    onLongClickItem = {
                        isShowEditFavorite = true
                        mPoiInfo = it
                    }
                )
            }
            //poi详情
            if (poiState.isShowDetailCard) {
                isShowQuickVia = false
                val poiDetailInfo = poiState.poiDetailInfo
                poiDetailInfo.image = localPoiState.favoritePoi?.iconPic
                BDMapSetting.moveMapToLocation(poiDetailInfo.location)
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
                        poiDetailInfo = poiDetailInfo,
                        isFavorite = localPoiState.isFavoritePoi,
                        onCancel = {
                            isShowQuickVia = true
                            mapViewModel.onPoiEvent(PoiEvent.CloseDetailCard)
                        },
                        onRoute = {
                            mapViewModel.onPoiEvent(PoiEvent.IsShowSearchPoi(false))
                            onRoutePlan(
                                mapViewModel = mapViewModel,
                                endLocation = poiDetailInfo.location
                            )
                            BDMapSetting.startRoutePlan(RoutePlanType.Walking)
                            BDMapSetting.moveMapToLocation(poiDetailInfo.location, 14f)
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
                            val item = LocalPoiInfo(
                                uid = poiDetailInfo.uid,
                                name = poiDetailInfo.name,
                                order = localPoiState.localPoiInfos.size + 1,
                                iconPic = "",
                                address = poiDetailInfo.address,
                                telephone = poiDetailInfo.telephone
                            )
                            scope.launch {
                                mapViewModel.onMPoiInfoEvent(LocalInfoEvent.InsertMPoiInfo(item))
                            }
                        }
                    )
                }
            }
            //路线规划
            if (routeState.isShowRoutePlan) {
                RoutePlan(
                    onCancel = {
                        isShowQuickVia = true
                        mapViewModel.onRouteEvent(RouteEvent.IsShowChange(false))
                        mapViewModel.onPoiEvent(PoiEvent.IsShowSearchPoi(true))
                        BDMapSetting.removeOverlay()
                        routePlanType = RoutePlanType.Walking
                    },
                    onRoutePlanType = {
                        mapViewModel.onPoiEvent(PoiEvent.IsShowSearchPoi(false))
                        routePlanType = it
                        BDMapSetting.startRoutePlan(it)
                    },
                    onNavi = {
                        mapViewModel.onGoNavEvent(
                            stPoint = deviceState.locationPoint,
                            endPoint = poiState.poiDetailInfo.location,
                            routePlanType = routePlanType
                        )

                    },
                    distance = routeState.routeDistance,
                    duration = routeState.routeDuration,
                    routePlanType = routePlanType
                )
            }
            //编辑收藏
            if (isShowEditFavorite) {
                mPoiInfo?.let { info ->
                    FavoriteItemEdit(
                        modifier = Modifier.align(Alignment.Center),
                        mPoiInfo = info,
                        onDelete = {
                            isShowEditFavorite = false
                            mapViewModel.onMPoiInfoEvent(LocalInfoEvent.DeleteMPoiInfo(info))
                        },
                        onCancel = {
                            isShowEditFavorite = false
                        },
                        onConfirm = {
                            isShowEditFavorite = false
                            mapViewModel.onMPoiInfoEvent(LocalInfoEvent.UpdateMPoiInfo(it))
                        }
                    )
                }
            }
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
private fun ManageLifeCycle(viewModel: LocateViewModel = hiltViewModel()) {
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
                Lifecycle.Event.ON_DESTROY -> {
                    viewModel.stopLocation()
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

