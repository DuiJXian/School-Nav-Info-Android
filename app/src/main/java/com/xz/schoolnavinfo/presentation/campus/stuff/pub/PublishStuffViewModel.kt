package com.xz.schoolnavinfo.presentation.campus.stuff.pub

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esafirm.imagepicker.model.Image
import com.google.gson.Gson
import com.xz.schoolnavinfo.common.event.GlobalFlow
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.domain.data.dto.StuffDTO
import com.xz.schoolnavinfo.domain.data.entity.Stuff
import com.xz.schoolnavinfo.domain.use_case.FileUseCases
import com.xz.schoolnavinfo.domain.use_case.StuffUseCases
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PublishStuffViewModel @Inject constructor(
    private val stuffUseCases: StuffUseCases,
    private val fileUseCases: FileUseCases,
    private val netExceptionManager: NetExceptionManager,
    private val globalFlow: GlobalFlow
) : ViewModel() {
    private val gson = Gson()

    private val _pubStuff = mutableStateOf(
        Stuff(
            desc = "",
            location = "",
            address = "",
            imageUrl = "",
            happenTime = "",
            type = false,
            id = null,
            createTime = null,
            publisherId = null,
            finishTime = null,
            status = false
        )
    )
    val pubStuff get() = _pubStuff.value

    private var _selectImage = mutableStateOf<Image?>(null)
    val selectImage get() = _selectImage.value

    private val _isShowLoading = mutableStateOf(false)
    val isShowLoading = _isShowLoading

    private val _netOver = MutableSharedFlow<Unit>()
    val netOver: SharedFlow<Unit> get() = _netOver.asSharedFlow()

    fun onEvent(event: PublishStuffEvent) {
        when (event) {
            is PublishStuffEvent.DateTimeChange -> {
                _pubStuff.value = pubStuff.copy(
                    happenTime = event.text
                )
            }

            is PublishStuffEvent.DescChange -> {
                _pubStuff.value = pubStuff.copy(
                    desc = event.text
                )
            }

            is PublishStuffEvent.TypeChange -> {
                _pubStuff.value = pubStuff.copy(
                    type = event.type
                )
            }

            is PublishStuffEvent.LocationChange -> {
                if (event.locationState == null) return
                _pubStuff.value = pubStuff.copy(
                    address = event.locationState.name + "-" + event.locationState.address,
                    location = gson.toJson(event.locationState.location)
                )
            }

            is PublishStuffEvent.PubStuff -> {
                viewModelScope.launch {
                    _isShowLoading.value = true
                    netExceptionManager.safeApiCall {
                        Log.e("TAG", "onEvent: ${gson.toJson(_pubStuff.value)}")
                        val fileResp = fileUseCases.uploadImage(_selectImage.value!!.path, "normal")
                        _pubStuff.value = pubStuff.copy(
                            imageUrl = fileResp.data
                        )
                        val resp = stuffUseCases.createStuff(pubStuff)
                        if (resp.code == "success") {
                            _netOver.emit(Unit)
                            globalFlow.onRefreshDataEvent(CampusMenu.Stuff)
                        }
                    }
                    _isShowLoading.value = false
                }
            }

            is PublishStuffEvent.ImageChange -> {
                _selectImage.value = event.image
            }
        }
    }

}