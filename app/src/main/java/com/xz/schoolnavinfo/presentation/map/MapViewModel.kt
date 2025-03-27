package com.xz.schoolnavinfo.presentation.map

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.xz.schoolnavinfo.domain.model.MPoiInfo
import com.xz.schoolnavinfo.domain.use_case.MPoiInfoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val mPoiInfoUseCases: MPoiInfoUseCases
) : ViewModel() {
    private val _poiState = MutableStateFlow(PoiState())
    val poiState: StateFlow<PoiState> = _poiState

    private val _routeState = mutableStateOf(RouteState())
    val routeState = _routeState

    private var getMPoiInfoJob: Job? = null
    private val _mPoiInfos = MutableStateFlow<List<MPoiInfo>>(emptyList())
    val mPoiInfos: StateFlow<List<MPoiInfo>> = _mPoiInfos

    private val _isFavoritePoi = mutableStateOf(false)
    var isFavoritePoi = _isFavoritePoi

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
                _isFavoritePoi.value = _mPoiInfos.value.any {
                    it.uid == event.uid
                }
            }
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