package com.xz.schoolnavinfo.domain.repository

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.common.request.LoginRequest
import com.xz.schoolnavinfo.domain.model.entity.UserInfo

interface UserRepository {
    suspend fun login(request: LoginRequest): BaseResponse<String>

    suspend fun getUserInfo() : BaseResponse<UserInfo>
}