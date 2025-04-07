package com.xz.schoolnavinfo.data.repository

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.common.model.PageResponse
import com.xz.schoolnavinfo.common.request.ArticleRequest
import com.xz.schoolnavinfo.data.dao.remote.ArticleApi
import com.xz.schoolnavinfo.domain.model.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.repository.ArticleRepository
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val articleApi: ArticleApi
) : ArticleRepository {
    override suspend fun getArticles(articleRequest: ArticleRequest): BaseResponse<PageResponse<ArticleDTO>> {
        return articleApi.getArticles(articleRequest)
    }
}