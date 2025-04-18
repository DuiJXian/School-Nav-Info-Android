package com.xz.schoolnavinfo.domain.use_case

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.data.dao.remote.request.LoginRequest
import com.xz.schoolnavinfo.domain.data.entity.UserInfo
import com.xz.schoolnavinfo.domain.repository.UserRepository

class UserUseCases(private val userRepository: UserRepository) {

    suspend fun login(request: LoginRequest): BaseResponse<String> {
        return userRepository.login(request)
    }

    suspend fun getUserInfo(): BaseResponse<UserInfo> {
        return userRepository.getUserInfo()
    }

    suspend fun register(body: MutableMap<String, Any>): BaseResponse<Unit> {
        return userRepository.register(body)
    }

    suspend fun changePassword(body: MutableMap<String, Any>): BaseResponse<Unit> {
        return userRepository.changePassword(body);
    }

    suspend fun changeNicknameAndAvatar(body: MutableMap<String, Any>): BaseResponse<Unit> {
        return userRepository.changeNicknameAndAvatar(body)
    }
}