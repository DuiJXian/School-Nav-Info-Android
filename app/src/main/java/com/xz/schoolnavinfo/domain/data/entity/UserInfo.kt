package com.xz.schoolnavinfo.domain.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val id: String = "",
    val username: String = "",
    val nickname: String = "",
    val role: String = "",
    val avatarUrl: String = ""
)
