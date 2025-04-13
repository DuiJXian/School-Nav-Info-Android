package com.xz.schoolnavinfo.presentation.common.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.xz.schoolnavinfo.common.event.GlobalFlow
import com.xz.schoolnavinfo.common.net.AuthInterceptor
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.domain.data.entity.UserInfo
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.domain.use_case.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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


    init {
        viewModelScope.launch {
            getUserInfo()
        }
    }

    private suspend fun getUserInfo() {
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

    fun onGetUserInfoEvent(){
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
                    _navEventFlow.emit(NavEvent.PublishArticle(event.type))
                }

                is NavEvent.BackPage -> {
                    _navEventFlow.emit(NavEvent.BackPage)
                }

                is NavEvent.ImagePreview -> {
                    _navEventFlow.emit(NavEvent.ImagePreview)
                }

                is NavEvent.ArticleDetail -> {
                    _navEventFlow.emit(NavEvent.ArticleDetail(event.articleDTO, event.type))
                }
            }
        }

    }
}