package com.xz.schoolnavinfo.presentation.user

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.common.event.GlobalFlow
import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.common.net.AuthInterceptor
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.data.dao.remote.request.LoginRequest
import com.xz.schoolnavinfo.domain.use_case.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val authInterceptor: AuthInterceptor,
    private val authUseCase: UserUseCases,
    private val netErrManager: NetExceptionManager,
) : ViewModel() {

    private val _loginOrRegisterState =
        mutableStateOf(LoginOrRegisterState(username = "admin", password = "admin1"))
    val loginOrRegisterState: State<LoginOrRegisterState> = _loginOrRegisterState

    private var _loginOrRegister = mutableIntStateOf(0) //0登录 1注册
    var loginOrRegister = _loginOrRegister

    private var _loginRes = mutableStateOf(BaseResponse("fail", "null", "null"))
    var loginRes = _loginRes


    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.ChangeLoginRegister -> {
                if (_loginOrRegister.intValue == 0) {
                    _loginOrRegister.intValue = 1
                } else {
                    _loginOrRegister.intValue = 0
                }
            }

            is UserEvent.ChangeUsername -> {
                _loginOrRegisterState.value = loginOrRegisterState.value.copy(
                    username = event.username
                )
            }

            is UserEvent.ChangePassword -> {
                _loginOrRegisterState.value = loginOrRegisterState.value.copy(
                    password = event.password
                )
            }

            is UserEvent.Login -> {
                viewModelScope.launch {
                    netErrManager.safeApiCall {
                        val resp = authUseCase.login(
                            LoginRequest(
                                loginOrRegisterState.value.username,
                                loginOrRegisterState.value.password
                            )
                        )
                        _loginRes.value = BaseResponse(resp.code, resp.message, resp.data)
                        if (resp.code == "success") {
                            Log.e("TAG", "onEvent: success")
                            val data = resp.data
                            authInterceptor.setToken(data)
                        }
                    }
                }
            }

        }
    }

}
