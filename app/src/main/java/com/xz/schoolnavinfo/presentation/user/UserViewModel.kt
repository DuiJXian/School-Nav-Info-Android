package com.xz.schoolnavinfo.presentation.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.common.net.AuthInterceptor
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.data.dao.remote.request.LoginRequest
import com.xz.schoolnavinfo.domain.use_case.UserUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class LoginOrRegisterUiSate(
    val username: String = "",
    val password: String = "",
    val againPassword: String = "",
    val errMessage: String = "",
    val isSuccess: Boolean = false,
    val currentTitleIndex: Int = 0,
)

@HiltViewModel
class UserViewModel @Inject constructor(
    private val authInterceptor: AuthInterceptor,
    private val userUseCases: UserUseCases,
    private val netErrManager: NetExceptionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginOrRegisterUiSate())
    val uiSate = _uiState.asStateFlow()

    private val _errMsgEvent = MutableSharedFlow<String>()
    val errMsgEvent = _errMsgEvent.asSharedFlow()

    fun setUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun setPassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun setAgainPassword(confirmPassword: String) {
        _uiState.update { it.copy(againPassword = confirmPassword) }
    }

    fun setCurrentTitleIndex(index: Int) {
        _uiState.update { it.copy(currentTitleIndex = index) }
    }

    fun login() {
        if (_uiState.value.username.isBlank() || _uiState.value.password.isBlank()) {
            _uiState.update { it.copy(errMessage = "账号和密码不能为空") }
            return
        } else {
            _uiState.update { it.copy(errMessage = "") }
        }
        viewModelScope.launch {
            netErrManager.safeApiCall {
                val resp = userUseCases.login(
                    LoginRequest(
                        _uiState.value.username,
                        _uiState.value.password
                    )
                )
                if (resp.code == "success") {
                    authInterceptor.setToken(resp.data)
                    _uiState.update { it.copy(isSuccess = true) }
                }
                _errMsgEvent.emit(resp.message)
            }
        }
    }

    fun register() {
        if (_uiState.value.username.isBlank() || _uiState.value.password.isBlank()) {
            _uiState.update { it.copy(errMessage = "账号和密码不能为空") }
            return
        } else if (_uiState.value.password != _uiState.value.againPassword) {
            _uiState.update { it.copy(errMessage = "2次密码不一致") }
            return
        } else {
            _uiState.update { it.copy(errMessage = "") }
        }
        viewModelScope.launch {
            netErrManager.safeApiCall {
                val resp = userUseCases.register(
                    mutableMapOf(
                        "username" to _uiState.value.username,
                        "password" to _uiState.value.password
                    )
                )
                if (resp.code == "success") {
                    _uiState.update { it.copy(currentTitleIndex = 0) }
                }
                _errMsgEvent.emit(resp.message)
            }
        }
    }

}
