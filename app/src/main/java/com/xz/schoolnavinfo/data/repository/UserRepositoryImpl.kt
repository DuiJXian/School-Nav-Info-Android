package com.xz.schoolnavinfo.data.repository

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.data.dao.remote.request.LoginRequest
import com.xz.schoolnavinfo.data.dao.remote.UserApi
import com.xz.schoolnavinfo.domain.data.entity.UserInfo
import com.xz.schoolnavinfo.domain.repository.UserRepository

class UserRepositoryImpl(
    private val userApi: UserApi,
) :
    UserRepository {

    override suspend fun login(request: LoginRequest): BaseResponse<String> {
        return userApi.login(request)
    }

    override suspend fun getUserInfo(): BaseResponse<UserInfo> {
        return userApi.getUserInfo()
    }

    override suspend fun register(body: MutableMap<String, Any>): BaseResponse<Unit> {
        return userApi.register(body)
    }

    override suspend fun changePassword(body: MutableMap<String, Any>): BaseResponse<Unit> {
        return userApi.changePassword(body)
    }

    override suspend fun changeNicknameAndAvatar(body: MutableMap<String, Any>): BaseResponse<Unit> {
        return userApi.changeNicknameAndAvatar(body)
    }
}