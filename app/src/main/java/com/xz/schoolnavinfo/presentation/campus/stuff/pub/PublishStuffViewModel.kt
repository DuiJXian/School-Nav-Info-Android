package com.xz.schoolnavinfo.presentation.campus.stuff.pub

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esafirm.imagepicker.model.Image
import com.xz.schoolnavinfo.common.flow.GlobalFlow
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.common.utils.JsonUtils
import com.xz.schoolnavinfo.domain.data.entity.Stuff
import com.xz.schoolnavinfo.domain.use_case.FileUseCases
import com.xz.schoolnavinfo.domain.use_case.StuffUseCases
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.baidu.select.LocationInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


data class PublishStuffUiState(
    val desc: String = "",
    val image: Image? = null,
    val location: String = "",
    val address: String = "",
    val happenTime: String = "",
    val type: Boolean = false,
    val status: Boolean = false,
    val loading: Boolean = false,
)

@HiltViewModel
class PublishStuffViewModel @Inject constructor(
    private val stuffUseCases: StuffUseCases,
    private val fileUseCases: FileUseCases,
    private val netExceptionManager: NetExceptionManager,
    private val globalFlow: GlobalFlow
) : ViewModel() {

    private val _uiState = MutableStateFlow(PublishStuffUiState())
    val uiState: StateFlow<PublishStuffUiState> = _uiState.asStateFlow()

    private val _reqOver = MutableSharedFlow<Unit>()
    val netOver: SharedFlow<Unit> get() = _reqOver.asSharedFlow()

    fun setDesc(desc: String) {
        _uiState.update { it.copy(desc = desc) }
    }

    fun setImage(image: Image?) {
        _uiState.update { it.copy(image = image) }
    }

    fun setLocation(locationInfo: LocationInfo?) {
        if (locationInfo == null) return
        _uiState.update {
            it.copy(
                location = JsonUtils.toJson(locationInfo.location),
                address = "${locationInfo.name}-${locationInfo.address}"
            )
        }
    }

    fun setHappenTime(happenTime: String) {
        _uiState.update { it.copy(happenTime = happenTime) }
    }

    fun setType(type: Boolean) {
        _uiState.update { it.copy(type = type) }
    }

    fun setStatus(status: Boolean) {
        _uiState.update { it.copy(status = status) }
    }

    fun publishStuff() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true) }
            netExceptionManager.safeApiCall {
                val fileResp = fileUseCases.uploadImage(_uiState.value.image!!.path, "normal")
                val stuff = Stuff(
                    desc = uiState.value.desc,
                    location = uiState.value.location,
                    address = uiState.value.address,
                    imageUrl = fileResp.data,
                    happenTime = uiState.value.happenTime,
                    type = uiState.value.type,
                    status = false
                )
                val resp = stuffUseCases.createStuff(stuff)
                if (resp.code == "success") {
                    _uiState.update { PublishStuffUiState() }
                    _reqOver.emit(Unit)
                    globalFlow.onRefreshDataEvent(CampusMenu.Stuff)
                }
            }
            _uiState.update { it.copy(loading = false) }
        }
    }

    fun isContentEmpty(): Boolean {
        val stateValue = uiState.value
        return stateValue.desc.isBlank() || stateValue.image == null || stateValue.location.isBlank() || stateValue.happenTime.isBlank()
    }
}