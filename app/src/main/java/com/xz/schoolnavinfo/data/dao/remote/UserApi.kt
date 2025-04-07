package com.xz.schoolnavinfo.data.dao.remote

import androidx.room.Dao
import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.common.request.LoginRequest
import com.xz.schoolnavinfo.domain.model.entity.UserInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST



interface UserApi {
    @POST("/user/login")
    suspend fun login(@Body request: LoginRequest): BaseResponse<String>

    @GET("/user/getUserInfo")
    suspend fun getUserInfo() : BaseResponse<UserInfo>
}
