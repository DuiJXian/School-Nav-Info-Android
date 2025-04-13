package com.xz.schoolnavinfo.domain.data.dto

import com.xz.schoolnavinfo.domain.data.entity.Comment
import com.xz.schoolnavinfo.domain.data.entity.UserInfo

data class CommentDTO(
    val comment: Comment,
    val userInfo: UserInfo
)