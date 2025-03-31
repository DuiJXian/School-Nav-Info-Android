package com.xz.schoolnavinfo.presentation.map

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baidu.mapapi.bikenavi.BikeNavigateHelper
import com.baidu.mapapi.bikenavi.adapter.IBEngineInitListener
import com.baidu.mapapi.bikenavi.adapter.IBRoutePlanListener
import com.baidu.mapapi.bikenavi.model.BikeRoutePlanError
import com.baidu.mapapi.bikenavi.params.BikeNaviLaunchParam
import com.baidu.mapapi.bikenavi.params.BikeRouteNodeInfo
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener
import com.baidu.mapapi.search.poi.PoiDetailResult
import com.baidu.mapapi.search.poi.PoiDetailSearchOption
import com.baidu.mapapi.search.poi.PoiDetailSearchResult
import com.baidu.mapapi.search.poi.PoiIndoorResult
import com.baidu.mapapi.search.poi.PoiNearbySearchOption
import com.baidu.mapapi.search.poi.PoiResult
import com.baidu.mapapi.search.poi.PoiSearch
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
import com.xz.schoolnavinfo.domain.model.MPoiInfo
import com.xz.schoolnavinfo.domain.use_case.MPoiInfoUseCases
import com.xz.schoolnavinfo.presentation.common.baidu.RoutePlanType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MapViewModel @Inject constructor(
    private val mPoiInfoUseCases: MPoiInfoUseCases,
    private val application: Application
) : AndroidViewModel(application) {
    private val _poiState = MutableStateFlow(PoiState())
    val poiState: StateFlow<PoiState> = _poiState

    private val _routeState = mutableStateOf(RouteState())
    val routeState = _routeState

    private var getMPoiInfoJob: Job? = null
    private val _mPoiInfos = MutableStateFlow<List<MPoiInfo>>(emptyList())
    val mPoiInfos: StateFlow<List<MPoiInfo>> = _mPoiInfos

    private val _isFavoritePoi = mutableStateOf(false)
    val isFavoritePoi = _isFavoritePoi

    private val _favoritePoi = mutableStateOf<MPoiInfo?>(null)
    var favoritePoi: MutableState<MPoiInfo?> = _favoritePoi

    private val _navMsgEvent = MutableSharedFlow<NavMsgEvent>()
    val navMsgEvent: SharedFlow<NavMsgEvent> = _navMsgEvent.asSharedFlow()


    init {
        getMPoiInfos()
        viewModelScope.launch {
            _poiState.collectLatest {
                if (it.searchText.isBlank()) {
                    clearPoiInfoList()
                }
            }
        }
    }


    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            viewModelScope.launch {
                when (msg.what) {
                    IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START -> {
                        _navMsgEvent.emit(NavMsgEvent.CalculateMsg("算路开始"))
                    }

                    IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS -> {
                        _navMsgEvent.emit(NavMsgEvent.CalculateMsg("算路成功"))
                    }

                    IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED -> {
                        _navMsgEvent.emit(NavMsgEvent.CalculateMsg("算路失败"))
                    }

                    IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI -> {
                        _navMsgEvent.emit(NavMsgEvent.EnterNav(RoutePlanType.Driving))
                    }

                    else -> {}
                }
            }
        }
    }

    fun onGoNavEvent(stPoint: LatLng, endPoint: LatLng, routePlanType: RoutePlanType) {
        when (routePlanType) {
            is RoutePlanType.Walking -> {
                if(BikeNavigateHelper.getInstance().isInitEngine){
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
                                            _navMsgEvent.emit(NavMsgEvent.EnterNav(RoutePlanType.Walking))
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

            is RoutePlanType.Biking -> {
                if(WalkNavigateHelper.getInstance().isInitEngine){
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
                                            _navMsgEvent.emit(NavMsgEvent.EnterNav(RoutePlanType.Biking))
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

            is RoutePlanType.Driving -> {
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


    private fun getMPoiInfos() {
        getMPoiInfoJob?.cancel()
        getMPoiInfoJob = mPoiInfoUseCases.getMPoiInfos()
            .onEach { mPoiInfos ->
                _mPoiInfos.value = mPoiInfos
            }.launchIn(viewModelScope)
    }

    fun onMPoiInfoEvent(event: MPoiInfoEvent) {
        when (event) {
            is MPoiInfoEvent.DeleteMPoiInfo -> {
                viewModelScope.launch {
                    mPoiInfoUseCases.deleteMPoiInfo(event.mPoiInfo)
                }
            }

            is MPoiInfoEvent.InsertMPoiInfo -> {
                viewModelScope.launch {
                    mPoiInfoUseCases.insertMPoiInfo(event.mPoiInfo)
                    _isFavoritePoi.value = true
                }
            }

            is MPoiInfoEvent.GetMPoiInfoByUid -> {
                getMPoiInfoByUid(event.uid)
            }

            is MPoiInfoEvent.UpdateMPoiInfo -> {
                viewModelScope.launch {
                    mPoiInfoUseCases.updateMPoiInfo(event.mPoiInfo)
                }
            }
        }
    }

    private fun getMPoiInfoByUid(uid: String) {
        viewModelScope.launch {
            favoritePoi.value = mPoiInfoUseCases.getMPoiInfoByUid(uid)
        }
        _isFavoritePoi.value = _mPoiInfos.value.any {
            it.uid == uid
        }
    }

    fun onPoiEvent(event: PoiEvent) {
        when (event) {
            is PoiEvent.SearchTextChange -> {
                _poiState.value = poiState.value.copy(
                    searchText = event.text
                )
                getPoiInfoList(event.text)
            }

            is PoiEvent.ClearSearchText -> {
                _poiState.value = poiState.value.copy(
                    searchText = ""
                )
            }

            is PoiEvent.ClearInfoList -> {
                clearPoiInfoList()
            }

            is PoiEvent.DestroyPoiSearch -> {
                poiSearch.destroy()
            }

            is PoiEvent.GetPoiDetailInfo -> {
                getMPoiInfoByUid(event.poiUid)
                getPoiDetail(event.poiUid)
            }

            is PoiEvent.CloseDetailCard -> {
                _poiState.value = poiState.value.copy(
                    isShowDetailCard = false
                )
            }

            is PoiEvent.CenterPointChange -> {
                _poiState.value = poiState.value.copy(
                    centerPoint = event.centerPoint
                )
            }

            is PoiEvent.FocusedChange -> {
                _poiState.value = poiState.value.copy(
                    isFocused = event.isFocused
                )
            }

            is PoiEvent.IsShowSearchPoi -> {
                _poiState.value = poiState.value.copy(
                    isShowSearch = event.isShow
                )
            }
        }
    }

    fun onRouteEvent(event: RouteEvent) {
        when (event) {
            is RouteEvent.DisAndDurChange -> {
                _routeState.value = routeState.value.copy(
                    routeDistance = event.distance,
                    routeDuration = event.duration
                )
            }

            is RouteEvent.IsShowChange -> {
                _routeState.value = routeState.value.copy(
                    isShowRoutePlan = event.isShow
                )
            }
        }
    }

    private val poiSearchListener = object : OnGetPoiSearchResultListener {
        override fun onGetPoiResult(result: PoiResult) {
            var poiList: MutableList<PoiInfo> = mutableListOf()
            if (result?.allPoi != null) {
                for (poi in result.allPoi) {
                    poi.distance = DistanceUtil
                        .getDistance(_poiState.value.centerPoint, poi.location)
                        .toInt()
                    poiList.add(poi)
                }
                _poiState.value = poiState.value.copy(
                    poiInfoList = poiList
                )
            }
        }

        override fun onGetPoiDetailResult(result: PoiDetailResult) {}
        override fun onGetPoiDetailResult(result: PoiDetailSearchResult) {
            for (item in result.poiDetailInfoList) {
                item.distance = DistanceUtil
                    .getDistance(_poiState.value.centerPoint, item.location)
                    .toInt()
                _poiState.value = poiState.value.copy(
                    poiDetailInfo = item,
                    isShowDetailCard = true
                )
            }
        }

        override fun onGetPoiIndoorResult(result: PoiIndoorResult) {}
    }
    private val poiSearch = PoiSearch.newInstance().apply {
        setOnGetPoiSearchResultListener(poiSearchListener)
    }

    private fun getPoiInfoList(keyword: String) {
        poiSearch.searchNearby(
            PoiNearbySearchOption().location(_poiState.value.centerPoint).radius(15000)
                .keyword(keyword)
        )
    }

    private fun getPoiDetail(poiUid: String) {
        poiSearch.searchPoiDetail(PoiDetailSearchOption().poiUids(poiUid))
    }

    private fun clearPoiInfoList() {
        _poiState.value = poiState.value.copy(
            poiInfoList = emptyList()
        )
    }

}