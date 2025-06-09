package com.xz.schoolnavinfo.presentation.campus.publish

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baidu.mapapi.model.LatLng
import com.esafirm.imagepicker.model.Image
import com.xz.schoolnavinfo.common.flow.GlobalFlow
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.common.utils.JsonUtils
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.data.entity.Article
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.domain.use_case.ArticleUseCases
import com.xz.schoolnavinfo.domain.use_case.FileUseCases
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.baidu.select.LocationInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface PublishArticleUiState {
    val title: String
    val content: String
    val address: String
    val location: LatLng?
    val images: List<Image>
    val isLoading: Boolean

    data class Discuss(
        override val title: String = "",
        override val content: String = "",
        override val address: String = "",
        override val location: LatLng?,
        override val images: List<Image> = emptyList(),
        override val isLoading: Boolean = false
    ) : PublishArticleUiState

    data class Activity(
        val banner: Image?,
        val isAddBanner: Boolean = false,
        override val title: String = "",
        override val content: String = "",
        override val address: String = "",
        override val location: LatLng?,
        override val images: List<Image> = emptyList(),
        override val isLoading: Boolean = false
    ) : PublishArticleUiState
}

inline fun <T> PublishArticleUiState.ifActivity(block: (PublishArticleUiState.Activity) -> T): T? {
    return if (this is PublishArticleUiState.Activity) block(this) else null
}

fun PublishArticleUiState.isAddBanner(): Boolean {
    return if (this !is PublishArticleUiState.Activity) false else this.isAddBanner
}

fun PublishArticleUiState.isSelectedBanner(): Boolean {
    return if (this !is PublishArticleUiState.Activity) false else this.banner != null
}


@HiltViewModel
class PublishArticleViewModel @Inject constructor(
    private val fileUseCases: FileUseCases,
    private val articleUseCases: ArticleUseCases,
    private val netExceptionManager: NetExceptionManager,
    private val globalFlow: GlobalFlow
) : ViewModel() {

    private val activityUiState = MutableStateFlow(
        PublishArticleUiState.Activity(banner = null, location = null)
    )
    private val discussUiState = MutableStateFlow(
        PublishArticleUiState.Discuss(location = null)
    )

    private val articleType: MutableStateFlow<ArticleType> = MutableStateFlow(ArticleType.DISCUSS)

    private val _publishOver = MutableSharedFlow<Unit>()
    val publishOver = _publishOver.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<PublishArticleUiState> = articleType
        .flatMapLatest {
            when (it) {
                ArticleType.ACTIVITY -> activityUiState
                ArticleType.DISCUSS -> discussUiState
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            discussUiState.value
        )

    fun switchArticleType(newType: ArticleType) {
        articleType.update { newType }
    }

    private fun updateCurrentUiState(transform: (PublishArticleUiState) -> PublishArticleUiState) {
        when (articleType.value) {
            ArticleType.DISCUSS -> discussUiState.update {
                transform(it) as PublishArticleUiState.Discuss
            }

            ArticleType.ACTIVITY -> activityUiState.update {
                transform(it) as PublishArticleUiState.Activity
            }
        }
    }

    fun setTitle(title: String) {
        updateCurrentUiState { state ->
            when (state) {
                is PublishArticleUiState.Discuss -> state.copy(title = title)
                is PublishArticleUiState.Activity -> state.copy(title = title)
            }
        }
    }

    fun setContent(content: String) {
        updateCurrentUiState { state ->
            when (state) {
                is PublishArticleUiState.Discuss -> state.copy(content = content)
                is PublishArticleUiState.Activity -> state.copy(content = content)
            }
        }
    }

    fun setLocation(locationInfo: LocationInfo?) {
        if (locationInfo == null) return
        updateCurrentUiState { state ->
            when (state) {
                is PublishArticleUiState.Discuss -> state.copy(
                    location = locationInfo.location,
                    address = "${locationInfo.name}-${locationInfo.address}"
                )

                is PublishArticleUiState.Activity -> state.copy(
                    location = locationInfo.location,
                    address = "${locationInfo.name}-${locationInfo.address}"
                )
            }
        }
    }

    fun setImages(images: List<Image>) {
        if (uiState.value.images.size + images.size > 9) return
        updateCurrentUiState { state ->
            when (state) {
                is PublishArticleUiState.Discuss -> state.copy(images = state.images + images)
                is PublishArticleUiState.Activity -> state.copy(images = state.images + images)
            }
        }
    }

    fun removeImage(image: Image) {
        updateCurrentUiState { state ->
            when (state) {
                is PublishArticleUiState.Discuss -> state.copy(images = uiState.value.images.filterNot { it == image })
                is PublishArticleUiState.Activity -> state.copy(images = uiState.value.images.filterNot { it == image })
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        updateCurrentUiState { state ->
            when (state) {
                is PublishArticleUiState.Discuss -> state.copy(isLoading = isLoading)
                is PublishArticleUiState.Activity -> state.copy(isLoading = isLoading)
            }
        }
    }

    fun setBanner(banner: Image) {
        updateCurrentUiState { state ->
            if (state is PublishArticleUiState.Activity) {
                state.copy(banner = banner)
            } else state
        }
    }

    fun setAddBanner(isAdd: Boolean) {
        updateCurrentUiState { state ->
            if (state is PublishArticleUiState.Activity) {
                state.copy(isAddBanner = isAdd)
            } else state
        }
    }

    fun clearUiState() {
        if (articleType.value == ArticleType.ACTIVITY) {
            activityUiState.update {
                PublishArticleUiState.Activity(banner = null, location = null)
            }
        } else {
            discussUiState.update { PublishArticleUiState.Discuss(location = null) }
        }
    }

    fun publishArticle() {
        setLoading(true)
        viewModelScope.launch {
            val imageUrls = mutableListOf<String>()
            uiState.value.ifActivity {
                if (it.banner != null && it.isAddBanner) {
                    netExceptionManager.safeApiCall {
                        val resp = fileUseCases.uploadImage(it.banner.path, "banner")
                        imageUrls.add(resp.data)
                    }
                }
            }
            for (image in uiState.value.images) {
                netExceptionManager.safeApiCall {
                    val resp = fileUseCases.uploadImage(image.path, "normal")
                    imageUrls.add(resp.data)
                }
            }

            val article = Article(
                title = uiState.value.title,
                content = uiState.value.content,
                address = uiState.value.address,
                location = JsonUtils.toJson(uiState.value.location),
                banner = (uiState.value.isAddBanner())
            )

            val articleDTO = ArticleDTO(
                article = article,
                imageList = imageUrls.map { it }
            )

            when (articleType.value) {
                ArticleType.DISCUSS -> {
                    netExceptionManager.safeApiCall {
                        val resp = articleUseCases.createDiscussArticle(articleDTO)
                        if (resp.code == "success") {
                            clearUiState()
                            globalFlow.onRefreshDataEvent(CampusMenu.Discuss)
                        }
                    }
                }

                ArticleType.ACTIVITY -> {
                    netExceptionManager.safeApiCall {
                        val resp = articleUseCases.createActivityArticle(articleDTO)
                        if (resp.code == "success") {
                            clearUiState()
                            globalFlow.onRefreshDataEvent(CampusMenu.Activity)
                        }
                    }
                }
            }
            setLoading(false)
            _publishOver.emit(Unit)
        }
    }

}