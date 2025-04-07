package com.xz.schoolnavinfo.presentation.common.viewmodel

import com.esafirm.imagepicker.model.Image

sealed class ImagePickerEvent {
    data class LaunchDone(val list: List<Image>): ImagePickerEvent()
    data object Launch: ImagePickerEvent()
}