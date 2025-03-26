package com.xz.schoolnavinfo.presentation.map

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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {
    private val _searchTextFiledState = MutableStateFlow(SearchTextFieldState())
    val searchTextFiledState: StateFlow<SearchTextFieldState> = _searchTextFiledState

    init {
        viewModelScope.launch {
            _searchTextFiledState.collectLatest {
                if (it.searchText.isBlank()) {
                    clearPoiInfoList()
                }
            }
        }
    }

    fun onEvent(event: MapEvent) {
        when (event) {
            is MapEvent.SearchTextChange -> {
                _searchTextFiledState.value = searchTextFiledState.value.copy(
                    searchText = event.text,
                    centerPoint = event.centerPoint
                )
                getPoiInfoList(event.text)
            }

            is MapEvent.ClearSearchText -> {
                _searchTextFiledState.value = searchTextFiledState.value.copy(
                    searchText = "",
                )
            }

            is MapEvent.ClearInfoList -> {
                clearPoiInfoList()
            }

            is MapEvent.DestroyPoiSearch -> {
                poiSearch.destroy()
            }

            is MapEvent.GetPoiDetailInfo -> {
                getPoiDetail(event.poiUid)
            }

            is MapEvent.CloseDetailCard -> {
                _searchTextFiledState.value = searchTextFiledState.value.copy(
                    isShowDetailCard = false,
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
                        .getDistance(_searchTextFiledState.value.centerPoint, poi.location)
                        .toInt()
                    poiList.add(poi)
                }
                _searchTextFiledState.value = searchTextFiledState.value.copy(
                    poiInfoList = poiList
                )
            }
        }

        override fun onGetPoiDetailResult(result: PoiDetailResult) {}
        override fun onGetPoiDetailResult(result: PoiDetailSearchResult) {
            for (item in result.poiDetailInfoList) {
                item.distance = DistanceUtil
                    .getDistance(_searchTextFiledState.value.centerPoint, item.location)
                    .toInt()
                _searchTextFiledState.value = searchTextFiledState.value.copy(
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
            PoiNearbySearchOption().location(_searchTextFiledState.value.centerPoint).radius(15000)
                .keyword(keyword)
        )
    }

    private fun getPoiDetail(poiUid: String) {
        poiSearch.searchPoiDetail(PoiDetailSearchOption().poiUids(poiUid))
    }

    private fun clearPoiInfoList() {
        _searchTextFiledState.value = searchTextFiledState.value.copy(
            poiInfoList = emptyList()
        )
    }

}