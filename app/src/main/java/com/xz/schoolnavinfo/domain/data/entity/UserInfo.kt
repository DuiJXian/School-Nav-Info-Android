package com.xz.schoolnavinfo.domain.data.entity

data class UserInfo(
    val id: String,
    val username: String,
    val nickname: String,
    val role: String,
    val avatarUrl: String
){
    companion object{
        fun default() = UserInfo(
            id = "",
            username = "",
            nickname = "",
            role = "",
            avatarUrl = ""
        )
    }
}
