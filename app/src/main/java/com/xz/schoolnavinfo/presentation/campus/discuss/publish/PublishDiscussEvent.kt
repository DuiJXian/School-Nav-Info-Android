package com.xz.schoolnavinfo.presentation.campus.discuss.publish

import com.esafirm.imagepicker.model.Image

sealed class PublishDiscussEvent {
    data class TitleChange(val title: String): PublishDiscussEvent()
    data class ContentChange(val content: String): PublishDiscussEvent()
    data class ImagesAdd(val images:List<Image>): PublishDiscussEvent()
    data class ImagesRemove(val image:Image): PublishDiscussEvent()
}