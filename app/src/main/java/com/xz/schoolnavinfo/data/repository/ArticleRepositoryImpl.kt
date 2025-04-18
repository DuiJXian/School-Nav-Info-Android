package com.xz.schoolnavinfo.data.repository

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.common.model.PageResponse
import com.xz.schoolnavinfo.data.dao.remote.request.ArticleRequest
import com.xz.schoolnavinfo.data.dao.remote.ArticleApi
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.repository.ArticleRepository
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val articleApi: ArticleApi
) : ArticleRepository {
    override suspend fun getDiscussArticleList(articleRequest: ArticleRequest): BaseResponse<PageResponse<ArticleDTO>> {
        return articleApi.getDiscussArticleList(articleRequest)
    }

    override suspend fun createDiscussArticle(articleDTO: ArticleDTO): BaseResponse<Unit> {
        return articleApi.createDiscussArticle(articleDTO)
    }

    override suspend fun deleteDiscussArticle(articleId: String): BaseResponse<Unit> {
        return articleApi.deleteDiscussArticle(articleId)
    }

    override suspend fun getActivityBanner(): BaseResponse<List<ArticleDTO>> {
        return articleApi.getActivityBanner()
    }

    override suspend fun getActivityArticleList(articleRequest: ArticleRequest): BaseResponse<PageResponse<ArticleDTO>> {
        return articleApi.getActivityArticleList(articleRequest)
    }


    override suspend fun createActivityArticle(articleDTO: ArticleDTO): BaseResponse<Unit> {
        return articleApi.createActivityArticle(articleDTO)
    }

    override suspend fun deleteActivityArticle(articleId: String): BaseResponse<Unit> {
        return articleApi.deleteActivityArticle(articleId)
    }

    override suspend fun searchActivityArticleList(text: String): BaseResponse<List<ArticleDTO>> {
        return articleApi.searchActivityArticleList(text)
    }
}