package com.xz.schoolnavinfo.presentation.campus.publish

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esafirm.imagepicker.model.Image
import com.google.gson.Gson
import com.xz.schoolnavinfo.common.flow.GlobalFlow
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.data.entity.Article
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.domain.use_case.ArticleUseCases
import com.xz.schoolnavinfo.domain.use_case.FileUseCases
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.baidu.select.LocationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PublishArticleViewModel @Inject constructor(
    private val fileUseCases: FileUseCases,
    private val articleUseCases: ArticleUseCases,
    private val netExceptionManager: NetExceptionManager,
    private val globalFlow: GlobalFlow
) : ViewModel() {
    private val _articleInfo = mutableStateOf(ArticleState(location = null))
    val articleInfo get() = _articleInfo.value

    private val _discussImages = mutableStateOf<List<Image>>(emptyList())
    val discussImages = _discussImages

    private val _activityImages = mutableStateOf<List<Image>>(emptyList())
    val activityImages = _activityImages


    private val _imageBanner = mutableStateOf<Image?>(null)
    val imageBanner get() = _imageBanner.value

    private val _addBanner = mutableStateOf(false)
    val addBanner get() = _addBanner.value

    private val _isShowLoading = mutableStateOf(false)
    val isShowLoading = _isShowLoading

    private val _netOver = MutableSharedFlow<Unit>()
    val netOver: SharedFlow<Unit> get() = _netOver.asSharedFlow()

    private val gson = Gson()

    fun onEvent(event: PublishArticleEvent) {
        when (event) {
            is PublishArticleEvent.ContentChange -> {
                _articleInfo.value = articleInfo.copy(
                    content = event.content
                )
            }

            is PublishArticleEvent.ActivityImagesAdd -> {
                _activityImages.value = activityImages.value + event.images
            }

            is PublishArticleEvent.ImagesRemove -> {
                if (event.type == "活动") {
                    _activityImages.value = _activityImages.value.filterNot { it == event.image }
                } else {
                    _discussImages.value = _discussImages.value.filterNot { it == event.image }
                }

            }

            is PublishArticleEvent.TitleChange -> {
                _articleInfo.value = articleInfo.copy(
                    title = event.title
                )
            }

            is PublishArticleEvent.PublishArticle -> {
                _isShowLoading.value = true
                viewModelScope.launch {
                    val imageUrls = mutableListOf<String>()
                    val uploadImageList =
                        if (event.articleType == ArticleType.Activity) _activityImages else _discussImages

                    if (event.isBanner && imageBanner != null) {
                        netExceptionManager.safeApiCall {
                            val resp = fileUseCases.uploadImage(imageBanner!!.path, "banner")
                            imageUrls.add(resp.data)
                        }
                    }
                    for (image in uploadImageList.value) {
                        netExceptionManager.safeApiCall {
                            val resp = fileUseCases.uploadImage(image.path, "normal")
                            imageUrls.add(resp.data)
                        }
                    }

                    val article = Article(
                        title = articleInfo.title,
                        content = articleInfo.content,
                        address = articleInfo.address,
                        location = gson.toJson(articleInfo.location),
                        banner = event.isBanner
                    )

                    val articleDTO = ArticleDTO(
                        article = article,
                        imageList = imageUrls.map { it }
                    )

                    when (event.articleType) {
                        is ArticleType.Discuss -> {
                            netExceptionManager.safeApiCall {
                                val resp = articleUseCases.createDiscussArticle(articleDTO)
                                if (resp.code == "success") {
                                    clearPublicArticleData(CampusMenu.Discuss)
                                    globalFlow.onRefreshDataEvent(CampusMenu.Discuss)
                                }
                            }
                        }

                        ArticleType.Activity -> {
                            netExceptionManager.safeApiCall {
                                val resp = articleUseCases.createActivityArticle(articleDTO)
                                if (resp.code == "success") {
                                    _addBanner.value = false
                                    _imageBanner.value = null
                                    clearPublicArticleData(CampusMenu.Activity)
                                    globalFlow.onRefreshDataEvent(CampusMenu.Activity)
                                }
                            }
                        }
                    }
                    _isShowLoading.value = false
                    _netOver.emit(Unit)
                }
            }

            is PublishArticleEvent.Clear -> {
                clearPublicArticleData(event.type)
            }

            is PublishArticleEvent.ImageBanner -> {
                _imageBanner.value = event.image
            }

            is PublishArticleEvent.DiscussImagesAdd -> {
                _discussImages.value = discussImages.value + event.images
            }

            is PublishArticleEvent.LocationChange -> {
                updateLocation(event.locationState)
            }

            is PublishArticleEvent.AddBanner -> {
                _addBanner.value = event.select
            }

        }
    }

    private fun updateLocation(
        locationState: LocationState?
    ) {
        if (locationState == null) return
        _articleInfo.value = articleInfo.copy(
            address = locationState.name + "-" + locationState.address,
            location = locationState.location
        )
    }

    private fun clearPublicArticleData(type: CampusMenu) {
        Log.e("TAG", "clearPublicArticleData", )
        _articleInfo.value = articleInfo.copy(
            title = "",
            content = "",
            address = "",
            location = null
        )

        if (type == CampusMenu.Activity) {
            _imageBanner.value = null
            _activityImages.value = emptyList()
        }else{
            _discussImages.value = emptyList()
        }
    }
}