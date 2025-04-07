package com.xz.schoolnavinfo.presentation.campus.discuss.publish

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.esafirm.imagepicker.model.Image
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PublishDiscussViewModel @Inject constructor() : ViewModel() {
    private val _title = mutableStateOf("")
    val title = _title

    private val _content = mutableStateOf("")
    val content = _content

    private val _images = mutableStateOf<List<Image>>(emptyList())
    val images = _images

    fun onEvent(event: PublishDiscussEvent) {
        when (event) {
            is PublishDiscussEvent.ContentChange -> {
                _content.value = event.content
            }

            is PublishDiscussEvent.ImagesAdd -> {
                _images.value = images.value + event.images
            }

            is PublishDiscussEvent.ImagesRemove -> {
                _images.value = _images.value.filterNot { it == event.image }
            }

            is PublishDiscussEvent.TitleChange -> {
                _title.value = event.title
            }


        }
    }
}