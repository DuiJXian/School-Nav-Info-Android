package com.xz.schoolnavinfo.presentation.map

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baidu.mapapi.bikenavi.BikeNavigateHelper
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam
import com.baidu.mapapi.bikenavi.params.BikeRouteNodeInfo
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener
import com.baidu.mapapi.map.BaiduMapOptions
import com.baidu.mapapi.map.MapPoi
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.map.MyLocationConfiguration
import com.baidu.mapapi.map.MyLocationData
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.overlayutil.BikingRouteOverlay
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay
import com.baidu.mapapi.overlayutil.OverlayManager
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay
import com.baidu.mapapi.search.core.PoiDetailInfo
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener
import com.baidu.mapapi.search.poi.PoiDetailResult
import com.baidu.mapapi.search.poi.PoiDetailSearchOption
import com.baidu.mapapi.search.poi.PoiDetailSearchResult
import com.baidu.mapapi.search.poi.PoiIndoorResult
import com.baidu.mapapi.search.poi.PoiNearbySearchOption
import com.baidu.mapapi.search.poi.PoiResult
import com.baidu.mapapi.search.poi.PoiSearch
import com.baidu.mapapi.search.route.BikingRoutePlanOption
import com.baidu.mapapi.search.route.BikingRouteResult
import com.baidu.mapapi.search.route.DrivingRoutePlanOption
import com.baidu.mapapi.search.route.DrivingRouteResult
import com.baidu.mapapi.search.route.IndoorRouteResult
import com.baidu.mapapi.search.route.IntegralRouteResult
import com.baidu.mapapi.search.route.MassTransitRouteResult
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener
import com.baidu.mapapi.search.route.PlanNode
import com.baidu.mapapi.search.route.RoutePlanSearch
import com.baidu.mapapi.search.route.TransitRouteResult
import com.baidu.mapapi.search.route.WalkingRoutePlanOption
import com.baidu.mapapi.search.route.WalkingRouteResult
import com.baidu.mapapi.utils.DistanceUtil
import com.baidu.mapapi.walknavi.WalkNavigateHelper
import com.baidu.mapapi.walknavi.adapter.IWEngineInitListener
import com.baidu.mapapi.walknavi.adapter.IWRoutePlanListener
import com.baidu.mapapi.walknavi.model.WalkRoutePlanError
import com.baidu.mapapi.walknavi.params.WalkNaviLaunchParam
import com.baidu.mapapi.walknavi.params.WalkRouteNodeInfo
import com.baidu.navisdk.adapter.BNRoutePlanNode
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory
import com.baidu.navisdk.adapter.IBNRoutePlanManager
import com.xz.schoolnavinfo.common.utils.LocationUtils
import com.xz.schoolnavinfo.common.utils.defaultLocation
import com.xz.schoolnavinfo.domain.data.entity.LocalPoiInfo
import com.xz.schoolnavinfo.domain.use_case.LocalPoiInfoUseCases
import com.xz.schoolnavinfo.presentation.common.baidu.map.adjustMapZoom
import com.xz.schoolnavinfo.presentation.common.baidu.map.scrollMapView
import com.xz.schoolnavinfo.presentation.common.baidu.map.setStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


enum class RouteType(val title: String) {
    Walking("步行"), Biking("骑行"), Driving("驾车")
}

data class MapViewDataUiState(
    val searchPoiInfos: List<PoiInfo> = emptyList(),
    val poiDetailInfo: PoiDetailInfo = PoiDetailInfo(),
    val localPoiInfos: List<LocalPoiInfo> = emptyList(),
    val localPoiInfo: LocalPoiInfo? = null,
    val routeType: RouteType = RouteType.Walking,
    val searchText: String = "",
    val routeDistance: String = "0",
    val routeDuration: String = "0",
    val currentPoiUid: String = "0",
)

data class MapViewShowUiState(
    val showPoiSearchData: Boolean = false,
    val showRoutePlan: Boolean = false,
    val showPoiDetail: Boolean = false,
    val showQuickEdit: Boolean = false
)

sealed class MapViewUiEvent {
    data class CalculateMsg(val msg: String) : MapViewUiEvent()
    data class EnterNav(val routeType: RouteType) : MapViewUiEvent()
}

@HiltViewModel
class MapViewModel @Inject constructor(
    private val localPoiInfoUseCases: LocalPoiInfoUseCases,
    private val application: Application
) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val _mapView = MapView(application, BaiduMapOptions().apply {
        zoomControlsEnabled(false)
    })
    val mapView get() = _mapView
    private val baiduMap = mapView.map

    private var getLocalPoiInfoJob: Job? = null

    private val _dataState = MutableStateFlow(MapViewDataUiState())
    val dataState: StateFlow<MapViewDataUiState> = _dataState.asStateFlow()

    private val _showState = MutableStateFlow(MapViewShowUiState())
    val showState: StateFlow<MapViewShowUiState> = _showState

    private val _uiEvent = MutableSharedFlow<MapViewUiEvent>()
    val uiEvent: SharedFlow<MapViewUiEvent> = _uiEvent.asSharedFlow()

    private var overlayManager: OverlayManager? = null
    private var centerLocation: LatLng = defaultLocation

    init {
        getLocalPoiInfos()
        initMap()
    }

    private fun initMap() {
        baiduMap.isMyLocationEnabled = true
        baiduMap.setMyLocationConfiguration(
            MyLocationConfiguration.Builder(MyLocationConfiguration.LocationMode.NORMAL, true)
                .build()
        )

        baiduMap.setOnMapClickListener(object : OnMapClickListener {
            override fun onMapClick(point: LatLng?) {}
            override fun onMapPoiClick(poi: MapPoi) {
                searchPoiDetail(poi.uid)
                _dataState.update {
                    it.copy(currentPoiUid = poi.uid)
                }
            }
        })
    }

    fun compositionOver(isDark: Boolean, location: LatLng) {
        mapView.setStyle(application, isDark)
        scrollMap(location, false)
        setCenterLocation(location)
    }


    private fun setCenterLocation(location: LatLng) {
        centerLocation = location
    }

    fun searchTextChange(text: String, location: LatLng) {
        _dataState.update { it.copy(searchText = text) }
        _showState.update { it.copy(showPoiSearchData = text.isNotBlank()) }
        poiSearch.searchNearby(
            PoiNearbySearchOption().location(location).radius(15000).keyword(text)
        )
    }

    fun clearSearchText() {
        _dataState.update {
            it.copy(searchPoiInfos = emptyList(), searchText = "")
        }
    }

    fun searchTextFocusChange(isFocus: Boolean) {
        _showState.update { it.copy(showPoiSearchData = isFocus) }
    }

    fun clickSearchItem(location: LatLng, uid: String) {
        setCenterLocation(location)
        searchPoiDetail(uid)
    }

    fun clickQuickViaItem(location: LatLng, uid: String) {
        searchPoiDetail(uid)
        setCenterLocation(location)
    }

    fun deleteLocalPoiInfo() {
        viewModelScope.launch {
            _dataState.value.localPoiInfo?.let { localPoiInfoUseCases.deleteMPoiInfo(it) }
            setShowQuickEdit(false)
        }
    }

    fun longClickQuickViaItem(uid: String) {
        _dataState.update {
            it.copy(
                currentPoiUid = uid,
                localPoiInfo = _dataState.value.localPoiInfos.find { res -> res.uid == uid }
            )
        }
        _showState.update { it.copy(showQuickEdit = true) }
    }

    fun setShowQuickEdit(value: Boolean) {
        _showState.update { it.copy(showQuickEdit = value) }
    }

    fun scrollMap(location: LatLng, animate: Boolean = false) {
        mapView.map.scrollMapView(location, animate = animate)
    }

    fun updateMapViewLocation(location: LatLng, direction: Float) {
        viewModelScope.launch { LocationUtils.saveLocation(application, location) }
        mapView.map.setMyLocationData(
            MyLocationData.Builder().latitude(location.latitude)
                .longitude(location.longitude).direction(direction)
                .build()
        )
    }

    fun searchPoiDetail(poiUid: String) {
        poiSearch.searchPoiDetail(PoiDetailSearchOption().poiUids(poiUid))
    }

    fun setShowPoiDetail(show: Boolean) {
        _showState.update { it.copy(showPoiDetail = show) }
    }

    fun closeRoutePlan() {
        _showState.update { it.copy(showRoutePlan = false) }
        overlayManager?.removeFromMap()
        _dataState.update { it.copy(routeType = RouteType.Walking) }
    }

    //导航事件
    fun onNavEvent(stPoint: LatLng, endPoint: LatLng) {
        when (_dataState.value.routeType) {
            RouteType.Walking -> {
                if (BikeNavigateHelper.getInstance().isInitEngine) {
                    BikeNavigateHelper.getInstance().unInitNaviEngine()
                }
                val param = WalkNaviLaunchParam()
                    .startNodeInfo(
                        WalkRouteNodeInfo().apply {
                            location = stPoint
                        }
                    ).endNodeInfo(
                        WalkRouteNodeInfo().apply {
                            location = endPoint
                        }
                    )
                WalkNavigateHelper.getInstance()
                    .initNaviEngine(application, object : IWEngineInitListener {
                        override fun engineInitSuccess() {
                            //引擎初始化成功的回调
                            WalkNavigateHelper.getInstance()
                                .routePlanWithRouteNode(param, object : IWRoutePlanListener {
                                    override fun onRoutePlanStart() {}

                                    override fun onRoutePlanSuccess() {
                                        viewModelScope.launch {
                                            _uiEvent.emit(MapViewUiEvent.EnterNav(RouteType.Walking))
                                        }
                                    }

                                    override fun onRoutePlanFail(walkRoutePlanError: WalkRoutePlanError) {}
                                })
                        }

                        override fun engineInitFail() {
                            //引擎初始化失败的回调
                        }
                    })
            }

            RouteType.Biking -> {
                if (WalkNavigateHelper.getInstance().isInitEngine) {
                    WalkNavigateHelper.getInstance().unInitNaviEngine()
                }
                val param = BikeNaviLaunchParam()
                    .startNodeInfo(
                        BikeRouteNodeInfo().apply {
                            location = stPoint
                        }
                    ).endNodeInfo(
                        BikeRouteNodeInfo().apply {
                            location = endPoint
                        }
                    )
                BikeNavigateHelper.getInstance()
                    .initNaviEngine(application, object : IBEngineInitListener {
                        override fun engineInitSuccess() {
                            //引擎初始化成功的回调
                            BikeNavigateHelper.getInstance()
                                .routePlanWithRouteNode(param, object : IBRoutePlanListener {
                                    override fun onRoutePlanStart() {

                                    }

                                    override fun onRoutePlanSuccess() {
                                        viewModelScope.launch {
                                            _uiEvent.emit(MapViewUiEvent.EnterNav(RouteType.Biking))
                                        }
                                    }

                                    override fun onRoutePlanFail(p0: BikeRoutePlanError?) {
                                    }
                                })
                        }

                        override fun engineInitFail() {
                            //引擎初始化失败的回调
                        }
                    })

            }

            RouteType.Driving -> {
                val list: MutableList<BNRoutePlanNode> = mutableListOf(
                    BNRoutePlanNode.Builder()
                        .longitude(stPoint.longitude)
                        .latitude(stPoint.latitude)
                        .build(),
                    BNRoutePlanNode.Builder()
                        .longitude(endPoint.longitude)
                        .latitude(endPoint.latitude)
                        .build()
                )
                BaiduNaviManagerFactory.getRoutePlanManager().routePlan(
                    list,
                    IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                    null,
                    handler
                )
            }
        }
    }

    //获取本地兴趣点
    private fun getLocalPoiInfos() {
        getLocalPoiInfoJob?.cancel()
        getLocalPoiInfoJob = localPoiInfoUseCases.getMPoiInfos()
            .onEach { items ->
                _dataState.update {
                    it.copy(localPoiInfos = items)
                }
            }.launchIn(viewModelScope)
    }

    fun updateLocalPoiInfo(localPoiInfo: LocalPoiInfo) {
        viewModelScope.launch {
            localPoiInfoUseCases.updateLocalPoiInfo(localPoiInfo)
            setShowQuickEdit(false)
        }
    }

    fun addOrRemoveQuickViaItem() {
        val localPoiInfo = _dataState.value.localPoiInfo
        if (_dataState.value.localPoiInfos.find { it.uid == localPoiInfo?.uid } == null) {
            viewModelScope.launch {
                val poiInfo = LocalPoiInfo(
                    uid = _dataState.value.poiDetailInfo.uid,
                    name = _dataState.value.poiDetailInfo.name,
                    order = _dataState.value.localPoiInfos.size + 1,
                    address = _dataState.value.poiDetailInfo.address,
                    telephone = _dataState.value.poiDetailInfo.telephone
                )
                localPoiInfoUseCases.insertMPoiInfo(poiInfo)
                _dataState.update { it.copy(localPoiInfo = poiInfo) }
            }
        } else if (localPoiInfo != null) {
            deleteLocalPoiInfo()
            _dataState.update { it.copy(localPoiInfo = null) }
        }
    }

    fun startRoutePlan(routeType: RouteType, startLocation: LatLng, endLocation: LatLng) {
        _dataState.update { it.copy(routeType = routeType) }
        val routeStartNode: PlanNode = PlanNode.withLocation(startLocation)
        val routeEndNode: PlanNode = PlanNode.withLocation(endLocation)
        when (routeType) {
            RouteType.Walking -> {
                routePlanSearch.walkingSearch(
                    WalkingRoutePlanOption()
                        .from(routeStartNode)
                        .to(routeEndNode)
                )
            }

            RouteType.Biking -> {
                routePlanSearch.bikingSearch(
                    BikingRoutePlanOption()
                        .from(routeStartNode)
                        .to(routeEndNode)
                )
            }

            RouteType.Driving -> {
                routePlanSearch.drivingSearch(
                    DrivingRoutePlanOption()
                        .from(routeStartNode)
                        .to(routeEndNode)
                )
            }
        }
        setShowPoiDetail(false)
    }

    private fun dealRouteRes(
        routeType: RouteType,
        walkingRouteResult: WalkingRouteResult? = null,
        bikingRouteResult: BikingRouteResult? = null,
        drivingRouteResult: DrivingRouteResult? = null
    ) {
        var distance = "0"
        var duration = "0"
        overlayManager?.removeFromMap()
        when (routeType) {
            RouteType.Walking -> {
                val overlay = WalkingRouteOverlay(mapView.map)
                overlayManager = overlay
                if (walkingRouteResult != null && walkingRouteResult.routeLines.size > 0) {
                    val routeLine = walkingRouteResult.routeLines[0]
                    overlay.setData(routeLine)
                    distance = routeLine.distance.toString()
                    duration = routeLine.duration.toString()
                }
            }

            RouteType.Biking -> {
                val overlay = BikingRouteOverlay(mapView.map)
                overlayManager = overlay
                if (bikingRouteResult != null && bikingRouteResult.routeLines.size > 0) {
                    val routeLine = bikingRouteResult.routeLines[0]
                    overlay.setData(routeLine)
                    distance = routeLine.distance.toString()
                    duration = routeLine.duration.toString()
                }
            }

            RouteType.Driving -> {
                val overlay = DrivingRouteOverlay(mapView.map)
                overlayManager = overlay
                if (drivingRouteResult != null && drivingRouteResult.routeLines.size > 0) {
                    val routeLine = drivingRouteResult.routeLines[0]
                    overlay.setData(routeLine)
                    distance = routeLine.distance.toString()
                    duration = routeLine.duration.toString()
                }
            }
        }
        overlayManager?.addToMap()
        overlayManager?.zoomToSpan()
        baiduMap.adjustMapZoom(-0.5f)
        _dataState.update {
            it.copy(
                routeDistance = distance,
                routeDuration = duration,
            )
        }
        _showState.update { it.copy(showRoutePlan = true) }
    }

    private val routePlanListener = object : OnGetRoutePlanResultListener {
        override fun onGetWalkingRouteResult(res: WalkingRouteResult) {
            dealRouteRes(routeType = RouteType.Walking, walkingRouteResult = res)
        }

        override fun onGetTransitRouteResult(p0: TransitRouteResult?) {}
        override fun onGetMassTransitRouteResult(p0: MassTransitRouteResult?) {}
        override fun onGetDrivingRouteResult(res: DrivingRouteResult) {
            dealRouteRes(routeType = RouteType.Driving, drivingRouteResult = res)
        }

        override fun onGetIndoorRouteResult(p0: IndoorRouteResult?) {}
        override fun onGetBikingRouteResult(res: BikingRouteResult) {
            dealRouteRes(routeType = RouteType.Biking, bikingRouteResult = res)
        }

        override fun onGetIntegralRouteResult(p0: IntegralRouteResult?) {}
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            viewModelScope.launch {
                when (msg.what) {
                    IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START -> {
                        _uiEvent.emit(MapViewUiEvent.CalculateMsg("算路开始"))
                    }

                    IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS -> {
                        _uiEvent.emit(MapViewUiEvent.CalculateMsg("算路成功"))
                    }

                    IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED -> {
                        _uiEvent.emit(MapViewUiEvent.CalculateMsg("算路失败"))
                    }

                    IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI -> {
                        _uiEvent.emit(MapViewUiEvent.EnterNav(RouteType.Driving))
                    }

                    else -> {}
                }
            }
        }
    }

    private val routePlanSearch: RoutePlanSearch = RoutePlanSearch.newInstance().apply {
        setOnGetRoutePlanResultListener(routePlanListener)
    }

    private val poiSearchListener = object : OnGetPoiSearchResultListener {
        override fun onGetPoiResult(result: PoiResult) {
            if (result.allPoi != null) {
                _dataState.update { it.copy(searchPoiInfos = result.allPoi) }
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onGetPoiDetailResult(p0: PoiDetailResult?) {
        }

        override fun onGetPoiDetailResult(result: PoiDetailSearchResult?) {
            if (result != null) {
                for (item in result.poiDetailInfoList) {
                    val localPoiInfo = _dataState.value.localPoiInfos.find { it.uid == item.uid }
                    item.image = localPoiInfo?.iconPic
                    item.distance = DistanceUtil.getDistance(centerLocation, item.location).toInt()

                    _dataState.update { it.copy(poiDetailInfo = item, localPoiInfo = localPoiInfo) }
                    setShowPoiDetail(true)
                }
            }
        }

        override fun onGetPoiIndoorResult(result: PoiIndoorResult) {}
    }

    private val poiSearch = PoiSearch.newInstance().apply {
        setOnGetPoiSearchResultListener(poiSearchListener)
    }


    override fun onCleared() {
        super.onCleared()
        mapView.onDestroy()
    }
}