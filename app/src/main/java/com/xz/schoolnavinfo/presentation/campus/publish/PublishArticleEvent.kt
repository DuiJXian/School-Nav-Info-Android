package com.xz.schoolnavinfo.presentation.campus.publish

import com.esafirm.imagepicker.model.Image
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.baidu.select.LocationState

sealed class PublishArticleEvent {
    data class TitleChange(val title: String) : PublishArticleEvent()
    data class ContentChange(val content: String) : PublishArticleEvent()
    data class ActivityImagesAdd(val images: List<Image>) : PublishArticleEvent()
    data class DiscussImagesAdd(val images: List<Image>) : PublishArticleEvent()
    data class ImageBanner(val image: Image) : PublishArticleEvent()
    data class ImagesRemove(val image: Image, val type: String) : PublishArticleEvent()
    data class PublishArticle(val articleType: ArticleType, val isBanner: Boolean) :
        PublishArticleEvent()

    data class Clear(val type: CampusMenu) : PublishArticleEvent()
    data class LocationChange(val locationState: LocationState?): PublishArticleEvent()
    data class AddBanner(val select: Boolean): PublishArticleEvent()
}