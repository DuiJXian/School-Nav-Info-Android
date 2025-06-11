package com.xz.schoolnavinfo.presentation.common.baidu.select

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Point
import android.util.Log
import android.view.MotionEvent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.BaiduMapOptions
import com.baidu.mapapi.map.MapStatus
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
import com.baidu.mapapi.utils.DistanceUtil
import com.xz.schoolnavinfo.common.utils.LocationUtils
import com.xz.schoolnavinfo.presentation.common.baidu.map.scrollMapView
import com.xz.schoolnavinfo.presentation.common.baidu.map.setStyle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class LocationSelectUiState(
    val centerLocation: LatLng? = null,
    val searchText: String = "",
    val selectedPoiInfo: PoiInfo? = null,
    val poiInfos: List<PoiInfo> = emptyList(),
)

@HiltViewModel
class LocationSelectViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val _mapView = MapView(application, BaiduMapOptions().apply {
        zoomControlsEnabled(false)
    })
    val mapView get() = _mapView

    private val _uiState = MutableStateFlow(LocationSelectUiState())
    val uiState: StateFlow<LocationSelectUiState> = _uiState.asStateFlow()

    private var isMapScrollFlag = false

    init {
        mapView.setStyle(application, true)
        viewModelScope.launch {
            _uiState.update {
                it.copy(centerLocation = LocationUtils.loadLocation(application))
            }
            poiSearchByNear(uiState.value.centerLocation!!)
        }
        setMapListener()
    }

    fun setMapStyle(isDark: Boolean) {
        val context = getApplication<Application>()
        mapView.setStyle(context, isDark)
    }

    fun destroyMap() {
        _mapView.onDestroy()
    }

    private fun setMapListener() {
        _mapView.map.setOnMapGestureListener(object : BaiduMap.onMapGestureListener {
            override fun onMapScroll(p0: Point, p1: Point, p2: MapStatus?): Boolean {
                isMapScrollFlag = true
                return false
            }

            override fun onMapDoubleTouch(p0: Point?, p1: MapStatus?): Boolean {
                return false
            }

            override fun onMapTwoClick(p0: Point?, p1: Point?, p2: MapStatus?): Boolean {
                return false
            }

            override fun onMapKneading(p0: Point?, p1: Point?, p2: MapStatus?): Boolean {
                return false
            }

            override fun onMapOverLooking(p0: Point?, p1: Point?, p2: MapStatus?): Boolean {
                return false
            }

            override fun onMapFling(
                p0: MotionEvent?,
                p1: Float,
                p2: Float,
                p3: MapStatus?
            ): Boolean {
                return false
            }

            override fun onMapStatusChangeFinish(p0: MapStatus?) {
                if (isMapScrollFlag && p0?.target != null) {
                    poiSearchByNear(p0.target)
                    isMapScrollFlag = false
                }
            }
        })
    }

    fun setSearchText(text: String) {
        _uiState.update { it.copy(searchText = text) }
        if (text.isNotBlank()) {
            poiSearchByKeyword(text)

        } else {
            poiSearchByNear(uiState.value.centerLocation!!)
        }
    }

    fun scrollMapViewToCenter() {
        if (_uiState.value.centerLocation != null) {
            isMapScrollFlag = true
            _mapView.map.scrollMapView(_uiState.value.centerLocation!!)
        }
    }


    fun setSelectedPoiInfo(poiInfo: PoiInfo) {
        _mapView.map.scrollMapView(poiInfo.location)
        _uiState.update { it.copy(selectedPoiInfo = poiInfo) }
    }

    private val poiSearchListener = object : OnGetPoiSearchResultListener {
        override fun onGetPoiResult(result: PoiResult) {
            val newPoiInfos: MutableList<PoiInfo> = mutableListOf()
            if (result.allPoi != null) {
                for (poiInfo in result.allPoi) {
                    poiInfo.distance = DistanceUtil
                        .getDistance(poiInfo.location, _uiState.value.centerLocation!!)
                        .toInt()
                    newPoiInfos.add(poiInfo)
                }
                _uiState.update {
                    it.copy(
                        poiInfos = newPoiInfos,
                        selectedPoiInfo = newPoiInfos.first()
                    )
                }
            }
        }

        @Deprecated("Deprecated in Java")
        override fun onGetPoiDetailResult(result: PoiDetailResult) {
        }

        override fun onGetPoiDetailResult(result: PoiDetailSearchResult) {}
        override fun onGetPoiIndoorResult(result: PoiIndoorResult) {}
    }

    private val poiSearch = PoiSearch.newInstance().apply {
        setOnGetPoiSearchResultListener(poiSearchListener)
    }

    private fun poiSearchByKeyword(keyword: String) {
        poiSearch.searchNearby(
            PoiNearbySearchOption().location(_uiState.value.centerLocation).radius(15000)
                .keyword(keyword)
        )
    }

    private fun poiSearchByNear(location: LatLng) {
        viewModelScope.launch {
            poiSearch.searchNearby(
                PoiNearbySearchOption().pageCapacity(50).location(location)
                    .keyword("美食\$楼房\$道路")
                    .radius(1000)
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.e("TAG", "onCleared: ")
        mapView.onDestroy()
    }
}