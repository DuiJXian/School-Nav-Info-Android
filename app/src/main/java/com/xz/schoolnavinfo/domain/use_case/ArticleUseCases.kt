package com.xz.schoolnavinfo.domain.use_case

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.common.model.PageResponse
import com.xz.schoolnavinfo.common.request.ArticleRequest
import com.xz.schoolnavinfo.domain.model.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.repository.ArticleRepository

class ArticleUseCases(private val articleRepository: ArticleRepository) {
    suspend fun getArticles(articleRequest: ArticleRequest): BaseResponse<PageResponse<ArticleDTO>> {
        return articleRepository.getArticles(articleRequest)
    }
}