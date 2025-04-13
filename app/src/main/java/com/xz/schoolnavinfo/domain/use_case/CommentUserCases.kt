package com.xz.schoolnavinfo.domain.use_case

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.domain.data.dto.CommentDTO
import com.xz.schoolnavinfo.domain.data.entity.Comment
import com.xz.schoolnavinfo.domain.repository.CommentRepository

class CommentUserCases(
    private val commentRepository: CommentRepository
) {
    suspend fun getCommentById(articleId: String): BaseResponse<List<CommentDTO>> {
        return commentRepository.getCommentById(articleId)
    }

    suspend fun createComment(comment: Comment): BaseResponse<String> {
        return commentRepository.createComment(comment)
    }
}