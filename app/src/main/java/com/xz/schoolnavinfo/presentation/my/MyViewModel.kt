package com.xz.schoolnavinfo.presentation.my

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.domain.data.resp.BaseResponse
import com.xz.schoolnavinfo.common.net.AuthInterceptor
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.domain.use_case.FileUseCases
import com.xz.schoolnavinfo.domain.use_case.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MyViewModel @Inject constructor(
    private val userUseCases: UserUseCases,
    private val authInterceptor: AuthInterceptor,
    private val fileUseCases: FileUseCases,
    private val netErrManager: NetExceptionManager,
) : ViewModel() {

    private val _changePasswordFlow = MutableSharedFlow<BaseResponse<Unit>>()
    val changePasswordFlow: SharedFlow<BaseResponse<Unit>> = _changePasswordFlow

    private val _changeNicknameAndAvatarFlow = MutableSharedFlow<BaseResponse<Unit>>()
    val changeNicknameAndAvatarFlow: SharedFlow<BaseResponse<Unit>> = _changeNicknameAndAvatarFlow

    fun onLogOut() {
        viewModelScope.launch {
            authInterceptor.setToken("")
        }
    }

    fun onUpdateNickname(nickname: String) {
        viewModelScope.launch {
            netErrManager.safeApiCall {
                val resp =
                    userUseCases.changeNicknameAndAvatar(mutableMapOf("nickname" to nickname))
                _changeNicknameAndAvatarFlow.emit(resp)
            }
        }
    }

    fun onUpdateAvatar(path: String) {
        viewModelScope.launch {
            netErrManager.safeApiCall {
                val resp = fileUseCases.uploadImage(path, "normal")
                if (resp.code == "success") {
                    val changeResp =
                        userUseCases.changeNicknameAndAvatar(mutableMapOf("avatar" to resp.data))
                    _changeNicknameAndAvatarFlow.emit(changeResp)
                }
            }
        }
    }

    fun onUpdatePassword(oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            netErrManager.safeApiCall {
                val resp = userUseCases.changePassword(
                    mutableMapOf(
                        "oldPassword" to oldPassword,
                        "newPassword" to newPassword
                    )
                )
                _changePasswordFlow.emit(resp)
            }
        }
    }
}