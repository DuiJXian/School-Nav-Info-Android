package com.xz.schoolnavinfo.domain.repository

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.common.model.PageResponse
import com.xz.schoolnavinfo.data.dao.remote.request.ArticleRequest
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO

interface ArticleRepository {

    suspend fun getDiscussArticleList(articleRequest: ArticleRequest) : BaseResponse<PageResponse<ArticleDTO>>

    suspend fun createDiscussArticle(articleDTO: ArticleDTO): BaseResponse<Unit>

    suspend fun deleteDiscussArticle(articleId: String) : BaseResponse<Unit>

    suspend fun getActivityBanner(): BaseResponse<List<ArticleDTO>>

    suspend fun getActivityArticleList(articleRequest: ArticleRequest) : BaseResponse<PageResponse<ArticleDTO>>

    suspend fun createActivityArticle(articleDTO: ArticleDTO): BaseResponse<Unit>

    suspend fun deleteActivityArticle(articleId: String) : BaseResponse<Unit>

    suspend fun searchActivityArticleList(text: String): BaseResponse<List<ArticleDTO>>

}