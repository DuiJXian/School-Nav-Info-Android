package com.xz.schoolnavinfo.data.dao.remote

import com.xz.schoolnavinfo.domain.data.resp.BaseResponse
import com.xz.schoolnavinfo.domain.data.resp.PageResponse
import com.xz.schoolnavinfo.data.dao.remote.request.ArticleRequest
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface ArticleApi {

    @POST("/api/article/discuss/get")
    suspend fun getDiscussArticleList(@Body articleRequest: ArticleRequest): BaseResponse<PageResponse<ArticleDTO>>

    @POST("/api/article/discuss/insert")
    suspend fun createDiscussArticle(@Body articleDTO: ArticleDTO): BaseResponse<Unit>

    @POST("/api/article/discuss/del")
    suspend fun deleteDiscussArticle(@Body articleId: String): BaseResponse<Unit>


    @GET("/api/article/activity/getBanner")
    suspend fun getActivityBanner(): BaseResponse<List<ArticleDTO>>

    @POST("/api/article/activity/get")
    suspend fun getActivityArticleList(@Body articleRequest: ArticleRequest): BaseResponse<PageResponse<ArticleDTO>>

    @POST("/api/article/activity/insert")
    suspend fun createActivityArticle(@Body articleDTO: ArticleDTO): BaseResponse<Unit>

    @POST("/api/article/activity/del")
    suspend fun deleteActivityArticle(@Body articleId: String): BaseResponse<Unit>

    @POST("/api/article/discuss/search")
    suspend fun searchActivityArticleList(@Body text: String): BaseResponse<List<ArticleDTO>>

//    @GET("/article/getArticleById")
//    suspend fun getArticleById() : ApiResponse
//

//

}