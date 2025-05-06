package com.xz.schoolnavinfo.domain.repository

import com.xz.schoolnavinfo.domain.data.resp.BaseResponse
import com.xz.schoolnavinfo.data.dao.remote.request.LoginRequest
import com.xz.schoolnavinfo.domain.data.entity.UserInfo

interface UserRepository {
    suspend fun login(request: LoginRequest): BaseResponse<String>

    suspend fun getUserInfo() : BaseResponse<UserInfo>

    suspend fun register(body: MutableMap<String, Any>): BaseResponse<Unit>

    suspend fun changePassword(body: MutableMap<String, Any>): BaseResponse<Unit>

    suspend fun changeNicknameAndAvatar(body: MutableMap<String, Any>): BaseResponse<Unit>
}