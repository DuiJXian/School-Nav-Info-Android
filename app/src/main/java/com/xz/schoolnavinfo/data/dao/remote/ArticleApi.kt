package com.xz.schoolnavinfo.data.dao.remote

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.common.model.PageResponse
import com.xz.schoolnavinfo.common.request.ArticleRequest
import com.xz.schoolnavinfo.domain.model.dto.ArticleDTO
import retrofit2.http.Body
import retrofit2.http.POST


interface ArticleApi {

    @POST("/api/article/getArticles")
    suspend fun getArticles(@Body articleRequest: ArticleRequest) : BaseResponse<PageResponse<ArticleDTO>>

//    @GET("/article/getArticleById")
//    suspend fun getArticleById() : ApiResponse
//
//    @GET("/article/createArticle")
//    suspend fun createArticle() : ApiResponse
//
//    @GET("/article/deleteArticle")
//    suspend fun deleteArticle(articleId: String) : ApiResponse
}