package com.xz.schoolnavinfo.domain.repository

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.common.model.PageResponse
import com.xz.schoolnavinfo.common.request.ArticleRequest
import com.xz.schoolnavinfo.domain.model.dto.ArticleDTO

interface ArticleRepository {
    suspend fun getArticles(articleRequest: ArticleRequest) : BaseResponse<PageResponse<ArticleDTO>>
}