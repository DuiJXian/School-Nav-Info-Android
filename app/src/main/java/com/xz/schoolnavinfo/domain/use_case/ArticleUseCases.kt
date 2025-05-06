package com.xz.schoolnavinfo.domain.use_case

import com.xz.schoolnavinfo.domain.data.resp.BaseResponse
import com.xz.schoolnavinfo.domain.data.resp.PageResponse
import com.xz.schoolnavinfo.data.dao.remote.request.ArticleRequest
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.repository.ArticleRepository

class ArticleUseCases(private val articleRepository: ArticleRepository) {
    suspend fun getDiscussArticleList(articleRequest: ArticleRequest): BaseResponse<PageResponse<ArticleDTO>> {
        return articleRepository.getDiscussArticleList(articleRequest)
    }

    suspend fun createDiscussArticle(articleDTO: ArticleDTO): BaseResponse<Unit> {
        return articleRepository.createDiscussArticle(articleDTO)
    }

    suspend fun deleteDiscussArticle(articleId: String): BaseResponse<Unit> {
        return articleRepository.deleteDiscussArticle(articleId)
    }


    suspend fun getActivityArticleList(articleRequest: ArticleRequest): BaseResponse<PageResponse<ArticleDTO>> {
        return articleRepository.getActivityArticleList(articleRequest)
    }

    suspend fun getActivityBanner(): BaseResponse<List<ArticleDTO>> {
        return articleRepository.getActivityBanner()
    }

    suspend fun createActivityArticle(articleDTO: ArticleDTO): BaseResponse<Unit> {
        return articleRepository.createActivityArticle(articleDTO)
    }

    suspend fun deleteActivityArticle(articleId: String): BaseResponse<Unit> {
        return articleRepository.deleteActivityArticle(articleId)
    }

    suspend fun searchActivityArticleList(text: String): BaseResponse<List<ArticleDTO>> {
        return articleRepository.searchActivityArticleList(text)
    }
}