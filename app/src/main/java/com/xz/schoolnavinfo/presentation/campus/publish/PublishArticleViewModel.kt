package com.xz.schoolnavinfo.presentation.campus.publish

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.esafirm.imagepicker.model.Image
import com.xz.schoolnavinfo.common.event.GlobalFlow
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.data.entity.Article
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.domain.use_case.ArticleUseCases
import com.xz.schoolnavinfo.domain.use_case.FileUseCases
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
    private val _articleTitleAndContent = mutableStateOf(ArticleState())
    val articleState = _articleTitleAndContent

    private val _discussImages = mutableStateOf<List<Image>>(emptyList())
    val discussImages = _discussImages

    private val _activityImages = mutableStateOf<List<Image>>(emptyList())
    val activityImages = _activityImages


    private val _imageBanner = mutableStateOf(Image(0, "", ""))
    val imageBanner = _imageBanner


    private val _isShowLoading = mutableStateOf(false)
    val isShowLoading = _isShowLoading

    private val _netOver = MutableSharedFlow<Unit>()
    val netOver: SharedFlow<Unit> get() = _netOver.asSharedFlow()

    fun onEvent(event: PublishArticleEvent) {
        when (event) {
            is PublishArticleEvent.ContentChange -> {
                _articleTitleAndContent.value = articleState.value.copy(
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
                _articleTitleAndContent.value = articleState.value.copy(
                    title = event.title
                )
            }

            is PublishArticleEvent.PublishArticle -> {
                _isShowLoading.value = true
                viewModelScope.launch {
                    val imageUrls = mutableListOf<String>()
                    val uploadImageList =
                        if (event.articleType == ArticleType.Activity) _activityImages else _discussImages

                    if (event.isBanner) {
                        netExceptionManager.safeApiCall {
                            val resp = fileUseCases.uploadImage(imageBanner.value.path, "banner")
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
                        title = articleState.value.title,
                        content = articleState.value.content,
                        banner = event.isBanner
                    )

                    Log.e("TAG", "onEvent: $article")
                    val articleDTO = ArticleDTO(
                        article = article,
                        imageList = imageUrls.map { it }
                    )

                    when (event.articleType) {
                        is ArticleType.Discuss -> {
                            netExceptionManager.safeApiCall {
                                val resp = articleUseCases.createDiscussArticle(articleDTO)
                                if (resp.code == "success") {
                                    clearPublicArticleData(PublishArticleEvent.Clear("讨论"))
                                    globalFlow.onRefreshDataEvent(ArticleType.Discuss)
                                }
                            }
                        }

                        ArticleType.Activity -> {
                            netExceptionManager.safeApiCall {
                                val resp = articleUseCases.createActivityArticle(articleDTO)
                                if (resp.code == "success") {
                                    clearPublicArticleData(PublishArticleEvent.Clear("活动"))
                                    globalFlow.onRefreshDataEvent(ArticleType.Activity)
                                }
                            }
                        }
                    }
                    _isShowLoading.value = false
                    _netOver.emit(Unit)
                }
            }

            is PublishArticleEvent.Clear -> {
                clearPublicArticleData(event)
            }

            is PublishArticleEvent.ImageBanner -> {
                _imageBanner.value = event.image
            }

            is PublishArticleEvent.DiscussImagesAdd -> {
                _discussImages.value = discussImages.value + event.images
            }
        }
    }

    private fun clearPublicArticleData(event: PublishArticleEvent.Clear) {
        if (event.type == "活动") {
            _articleTitleAndContent.value = articleState.value.copy(
                title = "",
                content = "",
            )
            activityImages.value = emptyList()
            _imageBanner.value = Image(0, "", "")
        } else {
            _articleTitleAndContent.value = articleState.value.copy(
                title = "",
                content = "",
            )
            _discussImages.value = emptyList()
        }
    }


}