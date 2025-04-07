package com.xz.schoolnavinfo.presentation.common.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esafirm.imagepicker.model.Image
import com.xz.schoolnavinfo.common.net.NetExceptionFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor(
    private val _netExceptionFlow: NetExceptionFlow
) : ViewModel() {

    var aaa = 1
    private val _navEvent = MutableSharedFlow<NavEvent>()
    val navEvent: SharedFlow<NavEvent> = _navEvent

//    private val _imagePickerResult = MutableStateFlow<List<Image>>(emptyList())
//    val imagePickerResult = _imagePickerResult.asStateFlow()
//
//    private val _launchImagePicker = MutableSharedFlow<Unit>()
//    val launchImagePicker = _launchImagePicker.asSharedFlow()

    val netExceptionFlow = _netExceptionFlow.netErrFlow


//    fun onImagePicker(event: ImagePickerEvent) {
//        viewModelScope.launch {
//            when (event) {
//                is ImagePickerEvent.Launch -> {
//                    Log.e("TAG", "CommonViewModel: emit")
//                    _launchImagePicker.emit(Unit)
//                }
//
//                is ImagePickerEvent.LaunchDone -> {
//                    _imagePickerResult.emit(event.list)
//                }
//            }
//        }
//    }

    fun navEvent(event: NavEvent) {
        viewModelScope.launch {
            when (event) {
                NavEvent.ArticleDetail -> {
                    _navEvent.emit(NavEvent.ArticleDetail)
                }

                NavEvent.LoginOrRegister -> {
                    _navEvent.emit(NavEvent.LoginOrRegister)
                }

                NavEvent.PublishDiscuss -> {
                    _navEvent.emit(NavEvent.PublishDiscuss)
                }
            }
        }

    }
}