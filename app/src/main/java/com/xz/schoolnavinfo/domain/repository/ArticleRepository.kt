package com.xz.schoolnavinfo.domain.repository

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.common.model.PageResponse
import com.xz.schoolnavinfo.data.dao.remote.request.ArticleRequest
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import retrofit2.http.Body

interface ArticleRepository {
    suspend fun getDiscussArticleList(articleRequest: ArticleRequest) : BaseResponse<PageResponse<ArticleDTO>>

    suspend fun createDiscussArticle(articleDTO: ArticleDTO): BaseResponse<String>

    suspend fun deleteDiscussArticle(articleId: String) : BaseResponse<String>

    suspend fun getActivityBanner(): BaseResponse<List<ArticleDTO>>
    suspend fun getActivityArticleList(articleRequest: ArticleRequest) : BaseResponse<PageResponse<ArticleDTO>>
    suspend fun createActivityArticle(articleDTO: ArticleDTO): BaseResponse<String>
    suspend fun deleteActivityArticle(articleId: String) : BaseResponse<String>
}