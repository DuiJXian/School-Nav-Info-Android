package com.xz.schoolnavinfo.presentation.common.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidu.mapapi.model.LatLng
import com.google.gson.Gson
import com.xz.schoolnavinfo.common.event.GlobalFlow
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.common.utils.DataStoreUtils
import com.xz.schoolnavinfo.common.utils.parseJwtPayload
import com.xz.schoolnavinfo.domain.data.entity.UserInfo
import com.xz.schoolnavinfo.domain.use_case.UserUseCases
import com.xz.schoolnavinfo.presentation.common.baidu.select.LocationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    val globalFlow: GlobalFlow,
    private val userUseCases: UserUseCases,
    private val netExceptionManager: NetExceptionManager,
) : ViewModel() {

    private val gson = Gson()

    private val _navEventFlow = MutableSharedFlow<NavEvent>(replay = 1)
    val navEventFlow: SharedFlow<NavEvent> = _navEventFlow

    private var _imageUrlState = mutableStateOf(ImageUrlListState())
    val imageUrlState get() = _imageUrlState.value

    private val _userInfo = MutableStateFlow(UserInfo("", "", "", "", ""))
    val userInfo: StateFlow<UserInfo> = _userInfo

    private val _selectLocationFlow = MutableSharedFlow<LocationState?>(replay = 1)
    val selectLocationFlow: SharedFlow<LocationState?> = _selectLocationFlow

    private val _routePlanFlow = MutableSharedFlow<LatLng>(replay = 1)
    val routePlanFlow: SharedFlow<LatLng> = _routePlanFlow

    private val _homePageFlow = MutableSharedFlow<Int>()
    val homePageFlow: SharedFlow<Int> = _homePageFlow

    init {
        viewModelScope.launch {
            getUserInfo()
        }
    }

    fun onHomePage(page: Int) {
        viewModelScope.launch {
            _homePageFlow.emit(page)
        }
    }

    fun onRoutePlan(json: String) {
        val location = gson.fromJson(json, LatLng::class.java)
        viewModelScope.launch {
            _routePlanFlow.emit(location)
        }
    }

    fun onLocationSelectEvent(locationState: LocationState?) {
        viewModelScope.launch {
            _selectLocationFlow.emit(locationState)
        }
    }


    suspend fun getUserInfo() {
//        val token = DataStoreUtils.getData(application, DataStoreUtils.Keys.TOKEN, "")
//        val json = parseJwtPayload(token)
//        try {
//            _userInfo.value = gson.fromJson(json.toString(), UserInfo::class.java)
//        } catch (e: Exception) {
//            onNavEvent(NavEvent.LoginOrRegister)
//        }
        netExceptionManager.safeApiCall {
            val resp = userUseCases.getUserInfo()
            if (resp.code == "success") {
                _userInfo.value = resp.data
            }
        }
    }

    fun onGetUserInfoEvent() {
        viewModelScope.launch {
            getUserInfo()
        }
    }

    fun onLoadImageUrlEvent(list: List<String>, startIndex: Int, height: Dp) {
        _imageUrlState.value = imageUrlState.copy(
            list = list,
            startIndex = startIndex,
            displayHeight = height
        )
        onNavEvent(NavEvent.ImagePreview)
    }


    fun onNavEvent(event: NavEvent) {
        viewModelScope.launch {
            when (event) {
                is NavEvent.LoginOrRegister -> {
                    _navEventFlow.emit(NavEvent.LoginOrRegister)
                }

                is NavEvent.PublishArticle -> {
                    _navEventFlow.emit(NavEvent.PublishArticle(event.campusMenu))
                }

                is NavEvent.BackPage -> {
                    _navEventFlow.emit(NavEvent.BackPage)
                }

                is NavEvent.ImagePreview -> {
                    _navEventFlow.emit(NavEvent.ImagePreview)
                }

                is NavEvent.ArticleDetail -> {
                    _navEventFlow.emit(NavEvent.ArticleDetail(event.articleDTO, event.articleType))
                }

                NavEvent.MapLocationSelect -> {
                    _navEventFlow.emit(NavEvent.MapLocationSelect)
                }

                NavEvent.PublishStuff -> {
                    _navEventFlow.emit(NavEvent.PublishStuff)
                }

                is NavEvent.StuffDetail -> {
                    _navEventFlow.emit(NavEvent.StuffDetail(event.id))
                }
            }
        }

    }
}