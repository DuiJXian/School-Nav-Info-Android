package com.xz.schoolnavinfo.presentation.map

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory
import com.xz.schoolnavinfo.common.utils.LocateUtils
import com.xz.schoolnavinfo.domain.data.entity.LocalPoiInfo
import com.xz.schoolnavinfo.presentation.common.baidu.map.LocateEvent
import com.xz.schoolnavinfo.presentation.common.baidu.map.LocateViewModel
import com.xz.schoolnavinfo.presentation.common.baidu.map.MapControl
import com.xz.schoolnavinfo.presentation.common.baidu.map.MapViewScreen
import com.xz.schoolnavinfo.presentation.common.baidu.map.RoutePlanType
import com.xz.schoolnavinfo.presentation.common.baidu.nav.activity.BNaviGuideActivity
import com.xz.schoolnavinfo.presentation.common.baidu.nav.activity.DemoGuideActivity
import com.xz.schoolnavinfo.presentation.common.baidu.nav.activity.WNaviGuideActivity
import com.xz.schoolnavinfo.presentation.common.compose.CheckGps
import com.xz.schoolnavinfo.presentation.common.compose.CheckPermission
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
    //路线状态
    val routeState by mapViewModel.routeState
    //收藏编辑
    var isShowEditFavorite by remember { mutableStateOf(false) }
    var mPoiInfo by remember { mutableStateOf<LocalPoiInfo?>(null) }
    val localPoiState by mapViewModel.localPoiState.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val isDark = isSystemInDarkTheme()


    val mapView = mapViewModel.map

    //检查定位和权限
    ManageLifeCycle()

    LaunchedEffect(deviceState) {
        mapViewModel.onPoiEvent(PoiEvent.CenterPointChange(deviceState.locationPoint))
        MapControl.saveLocation(context, deviceState.locationPoint)
        MapControl.setMyLocationData(
            myLocationData = MyLocationData
                .Builder()
                .latitude(deviceState.locationPoint.latitude)
                .longitude(deviceState.locationPoint.longitude)
                .direction(deviceState.direction)
                .build(),
            mapView = mapView
        )
    }


    LaunchedEffect(true) {
        MapControl.setConfig(mapView)
        if (!mapViewModel.isSignalMarker) {
            mapViewModel.onMarkerChange(true)
        }
        //先加载本地保存的位置
        MapControl.setMapWindow(mapView, MapControl.loadLocation(context), type = 0)
        MapControl.setStyle(mapView, context, isDark)
        //开始定位
        locationMapViewModel.startLocation()
        //点击事件
        MapControl.setOnMapClickListener(object : OnMapClickListener {
            override fun onMapClick(point: LatLng?) {}
            override fun onMapPoiClick(poi: MapPoi?) {
                if (poi != null) {
                    mapViewModel.onPoiEvent(PoiEvent.GetPoiDetailInfo(poi.uid))
                    mapViewModel.onLocalPoiInfoEvent(LocalInfoEvent.GetMPoiInfoByUid(poi.uid))
                }

            }
        }, mapView)
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
                    val intent = when (event.routePlanType) {
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

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) {
        Box {
            //地图
            MapViewScreen(mapView = mapView)

            //搜索
            if (poiState.isShowSearch) {
                PoiSearch(
                    onTextChange = {
                        mapViewModel.onPoiEvent(PoiEvent.SearchTextChange(it))
                    }, onClose = {
                        mapViewModel.onPoiEvent(PoiEvent.ClearSearchText)
                    }, onClickItem = {
                        mapViewModel.onPoiEvent(PoiEvent.GetPoiDetailInfo(it.uid))
                        mapViewModel.onLocalPoiInfoEvent(LocalInfoEvent.GetMPoiInfoByUid(it.uid))
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
                        MapControl.setMapWindow(mapView, deviceState.locationPoint)
                    }
            )
            //快速访问
            if (isShowQuickVia) {
                QuickViaItem(
                    modifier = Modifier.align(Alignment.BottomStart),
                    localPoiInfos = localPoiState.localPoiInfos,
                    onClickItem = {
                        mapViewModel.onPoiEvent(PoiEvent.GetPoiDetailInfo(it.uid))
                        mapViewModel.onLocalPoiInfoEvent(LocalInfoEvent.GetMPoiInfoByUid(it.uid))
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
                MapControl.setMapWindow(mapView, poiDetailInfo.location)
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
                            MapControl.preRoutePlan(
                                startLocation = deviceState.locationPoint,
                                endLocation = poiDetailInfo.location,
                                mapView = mapView
                            ) { distance, duration ->
                                mapViewModel.onRouteEvent(
                                    RouteEvent.DisAndDurChange(
                                        distance,
                                        duration
                                    )
                                )
                                mapViewModel.onRouteEvent(RouteEvent.IsShowChange(true))
                            }
                            MapControl.startRoutePlan(RoutePlanType.Walking)
                            MapControl.setMapWindow(mapView, poiDetailInfo.location, 14f)
                            mapViewModel.onPoiEvent(PoiEvent.CloseDetailCard)
                        },
                        onCall = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:$it")
                            }
                            val hasTelephony = context.packageManager.hasSystemFeature(
                                PackageManager.FEATURE_TELEPHONY
                            )
                            if (hasTelephony) {
                                Log.e(TAG, "MapScreen: $it")
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
                                mapViewModel.onLocalPoiInfoEvent(LocalInfoEvent.InsertMPoiInfo(item))
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
                        MapControl.removeOverlay()
                        routePlanType = RoutePlanType.Walking
                    },
                    onRoutePlanType = {
                        mapViewModel.onPoiEvent(PoiEvent.IsShowSearchPoi(false))
                        routePlanType = it
                        MapControl.startRoutePlan(it)
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
                            mapViewModel.onLocalPoiInfoEvent(LocalInfoEvent.DeleteMPoiInfo(info))
                        },
                        onCancel = {
                            isShowEditFavorite = false
                        },
                        onConfirm = {
                            isShowEditFavorite = false
                            mapViewModel.onLocalPoiInfoEvent(LocalInfoEvent.UpdateMPoiInfo(it))
                        }
                    )
                }
            }
        }
    }
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




