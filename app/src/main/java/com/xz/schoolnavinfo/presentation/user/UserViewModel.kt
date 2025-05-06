package com.xz.schoolnavinfo.presentation.user

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.domain.data.resp.BaseResponse
import com.xz.schoolnavinfo.common.net.AuthInterceptor
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.data.dao.remote.request.LoginRequest
import com.xz.schoolnavinfo.domain.use_case.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class UserViewModel @Inject constructor(
    private val authInterceptor: AuthInterceptor,
    private val userUseCases: UserUseCases,
    private val netErrManager: NetExceptionManager,
) : ViewModel() {

    private val _loginOrRegisterState =
        mutableStateOf(LoginOrRegisterState(username = "", password = ""))
    val loginOrRegisterState get() = _loginOrRegisterState.value

    private var _loginOrRegister = mutableIntStateOf(0) //0登录 1注册
    var loginOrRegister = _loginOrRegister

    private var _loginRes = mutableStateOf(BaseResponse("fail", "null", "null"))
    var loginRes = _loginRes

    private val _errMessage = mutableStateOf("")
    val errMessage get() = _errMessage.value


    fun onErrMessage(txt: String) {
        _errMessage.value = txt
    }

    fun onEvent(event: UserEvent) {
        when (event) {
            is UserEvent.ChangeLoginRegister -> {
                _loginOrRegister.intValue = abs(loginOrRegister.intValue - 1)
            }

            is UserEvent.ChangeUsername -> {
                _loginOrRegisterState.value = loginOrRegisterState.copy(
                    username = event.username
                )
            }

            is UserEvent.ChangePassword -> {
                _loginOrRegisterState.value = loginOrRegisterState.copy(
                    password = event.password
                )
            }

            is UserEvent.Login -> {
                viewModelScope.launch {
                    netErrManager.safeApiCall {
                        val resp = userUseCases.login(
                            LoginRequest(
                                loginOrRegisterState.username,
                                loginOrRegisterState.password
                            )
                        )
                        _loginRes.value = BaseResponse(resp.code, resp.message, resp.data)
                        if (resp.code == "success") {
                            val data = resp.data
                            authInterceptor.setToken(data)
                        } else {
                            _errMessage.value = resp.message
                        }
                    }
                }
            }

            UserEvent.Register -> {
                viewModelScope.launch {
                    netErrManager.safeApiCall {
                        val resp = userUseCases.register(
                            mutableMapOf(
                                "username" to loginOrRegisterState.username,
                                "password" to loginOrRegisterState.password
                            )
                        )
                        if (resp.code == "success") {
                            _loginOrRegister.intValue = abs(loginOrRegister.intValue - 1)
                        } else {
                            _errMessage.value = resp.message
                        }
                    }
                }
            }
        }
    }

}
