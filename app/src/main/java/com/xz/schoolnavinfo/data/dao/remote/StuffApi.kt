package com.xz.schoolnavinfo.data.dao.remote

import com.xz.schoolnavinfo.domain.data.resp.BaseResponse
import com.xz.schoolnavinfo.domain.data.dto.StuffDTO
import com.xz.schoolnavinfo.domain.data.entity.Stuff
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface StuffApi {

    @POST("/api/stuff/createStuff")
    suspend fun createStuff(@Body stuff: Stuff?): BaseResponse<Unit>

    @POST("/api/stuff/deleteById")
    suspend fun deleteById(@Body id: String?): BaseResponse<Unit>

    @POST("/api/stuff/updateStatus")
    suspend fun updateStatus(@Body id: String?): BaseResponse<Unit>

    @GET("/api/stuff/get")
    suspend fun getStuffList(): BaseResponse<List<StuffDTO>>

    @POST("/api/stuff/getById")
    suspend fun getStuffById(@Body id: String): BaseResponse<StuffDTO>

    @POST("/api/stuff/search")
    suspend fun searchStuffList(@Body text: String): BaseResponse<List<StuffDTO>>
}