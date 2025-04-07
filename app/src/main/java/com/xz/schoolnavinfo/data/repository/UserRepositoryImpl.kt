package com.xz.schoolnavinfo.data.repository

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.common.request.LoginRequest
import com.xz.schoolnavinfo.data.dao.remote.UserApi
import com.xz.schoolnavinfo.domain.model.entity.UserInfo
import com.xz.schoolnavinfo.domain.repository.UserRepository

class UserRepositoryImpl(
    private val authApi: UserApi,
) :
    UserRepository {

    override suspend fun login(request: LoginRequest): BaseResponse<String> {
        return authApi.login(request)
    }

    override suspend fun getUserInfo(): BaseResponse<UserInfo> {
        return authApi.getUserInfo()
    }
}