package com.xz.schoolnavinfo.presentation.common.baidu.select

import android.annotation.SuppressLint
import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baidu.mapapi.map.BaiduMapOptions
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener
import com.baidu.mapapi.search.poi.PoiDetailResult
import com.baidu.mapapi.search.poi.PoiDetailSearchResult
import com.baidu.mapapi.search.poi.PoiIndoorResult
import com.baidu.mapapi.search.poi.PoiNearbySearchOption
import com.baidu.mapapi.search.poi.PoiResult
import com.baidu.mapapi.search.poi.PoiSearch
import com.xz.schoolnavinfo.presentation.common.baidu.map.MapControl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapSelectViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val _mapView = MapView(application, BaiduMapOptions().apply {
        zoomControlsEnabled(false)
    })
    val mapView get() = _mapView

    private val _mapSelectState = mutableStateOf<LocationState?>(null)
    val mapSelectState get() = _mapSelectState

    private var _poiInfoList = MutableStateFlow<List<PoiInfo>>(emptyList())
    val poiInfoList: StateFlow<List<PoiInfo>> get() = _poiInfoList


    fun onStateChangeEvent(state: LocationState?) {
        _mapSelectState.value = state
    }


    private val poiSearchListener = object : OnGetPoiSearchResultListener {
        override fun onGetPoiResult(result: PoiResult) {
            if (result.allPoi != null) {
                _poiInfoList.value = result.allPoi
            }
        }

        override fun onGetPoiDetailResult(result: PoiDetailResult) {}
        override fun onGetPoiDetailResult(result: PoiDetailSearchResult) {
        }

        override fun onGetPoiIndoorResult(result: PoiIndoorResult) {}
    }
    private val poiSearch = PoiSearch.newInstance().apply {
        setOnGetPoiSearchResultListener(poiSearchListener)
    }

    fun getPoiInfoListEvent(location: LatLng) {
        viewModelScope.launch {
            delay(100)
            poiSearch.searchNearby(
                PoiNearbySearchOption().pageCapacity(20).location(location)
                    .keyword("美食\$购物\$生活服务\$丽人\$休闲娱乐\$运动健身\$医疗\$公司企业\$村\$教育培训\$学校")
                    .radius(1000)
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        MapControl.onDestroy(mapView)
    }
}