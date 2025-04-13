package com.xz.schoolnavinfo.data.dao.remote

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.common.model.PageResponse
import com.xz.schoolnavinfo.data.dao.remote.request.ArticleRequest
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ArticleApi {

    @POST("/api/article/discuss/get")
    suspend fun getDiscussArticleList(@Body articleRequest: ArticleRequest): BaseResponse<PageResponse<ArticleDTO>>

    @POST("/api/article/discuss/insert")
    suspend fun createDiscussArticle(@Body articleDTO: ArticleDTO): BaseResponse<String>

    @GET("/api/article/discuss/delete")
    suspend fun deleteDiscussArticle(@Body articleId: String): BaseResponse<String>


    @GET("/api/article/activity/getBanner")
    suspend fun getActivityBanner(): BaseResponse<List<ArticleDTO>>

    @POST("/api/article/activity/get")
    suspend fun getActivityArticleList(@Body articleRequest: ArticleRequest): BaseResponse<PageResponse<ArticleDTO>>

    @POST("/api/article/activity/insert")
    suspend fun createActivityArticle(@Body articleDTO: ArticleDTO): BaseResponse<String>

    @GET("/api/article/activity/delete")
    suspend fun deleteActivityArticle(@Body articleId: String): BaseResponse<String>

//    @GET("/article/getArticleById")
//    suspend fun getArticleById() : ApiResponse
//

//

}