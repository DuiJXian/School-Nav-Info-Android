package com.xz.schoolnavinfo.domain.repository

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.domain.data.dto.CommentDTO
import com.xz.schoolnavinfo.domain.data.entity.Comment

interface CommentRepository {
    suspend fun getCommentById(articleId: String): BaseResponse<List<CommentDTO>>

    suspend fun createComment(comment: Comment): BaseResponse<String>
}