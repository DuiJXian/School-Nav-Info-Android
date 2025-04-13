package com.xz.schoolnavinfo.domain.use_case

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.data.dao.remote.request.LoginRequest
import com.xz.schoolnavinfo.domain.data.entity.UserInfo
import com.xz.schoolnavinfo.domain.repository.UserRepository

class UserUseCases(private val authRepository: UserRepository) {

    suspend fun login(request: LoginRequest): BaseResponse<String> {
        return authRepository.login(request)
    }

    suspend fun getUserInfo(): BaseResponse<UserInfo> {
        return authRepository.getUserInfo()
    }
}