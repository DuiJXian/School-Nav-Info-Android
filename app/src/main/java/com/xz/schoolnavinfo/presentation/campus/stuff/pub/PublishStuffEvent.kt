package com.xz.schoolnavinfo.presentation.campus.stuff.pub

import com.esafirm.imagepicker.model.Image
import com.xz.schoolnavinfo.presentation.common.baidu.select.LocationInfo

sealed class PublishStuffEvent {
    data class TypeChange(val type: Boolean): PublishStuffEvent()
    data class DescChange(val text: String): PublishStuffEvent()
    data class DateTimeChange(val text: String): PublishStuffEvent()
    data object PubStuff: PublishStuffEvent()
    data class LocationChange(val locationInfo: LocationInfo?): PublishStuffEvent()
    data class ImageChange(val image: Image): PublishStuffEvent()
}