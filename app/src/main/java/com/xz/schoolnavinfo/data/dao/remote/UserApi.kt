package com.xz.schoolnavinfo.data.dao.remote

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.data.dao.remote.request.LoginRequest
import com.xz.schoolnavinfo.domain.data.entity.UserInfo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST



interface UserApi {
    @POST("/user/login")
    suspend fun login(@Body request: LoginRequest): BaseResponse<String>

    @GET("/api/user/getUserInfo")
    suspend fun getUserInfo() : BaseResponse<UserInfo>

    @POST("/user/register")
    suspend fun register(@Body body: MutableMap<String, Any>): BaseResponse<Unit>

    @POST("/api/user/changePassword")
    suspend fun changePassword(@Body body: MutableMap<String, Any>): BaseResponse<Unit>

    @POST("/api/user/changeNicknameAndAvatar")
    suspend fun changeNicknameAndAvatar(@Body body: MutableMap<String, Any>): BaseResponse<Unit>
}
