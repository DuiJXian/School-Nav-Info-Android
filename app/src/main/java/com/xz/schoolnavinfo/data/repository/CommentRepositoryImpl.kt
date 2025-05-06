package com.xz.schoolnavinfo.data.repository

import com.xz.schoolnavinfo.domain.data.resp.BaseResponse
import com.xz.schoolnavinfo.data.dao.remote.CommentAPi
import com.xz.schoolnavinfo.domain.data.dto.CommentDTO
import com.xz.schoolnavinfo.domain.data.entity.Comment
import com.xz.schoolnavinfo.domain.repository.CommentRepository
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val commentAPi: CommentAPi
) : CommentRepository {
    override suspend fun getCommentById(articleId: String): BaseResponse<List<CommentDTO>> {
        return commentAPi.getCommentById(map = mapOf("articleId" to articleId))
    }

    override suspend fun createComment(comment: Comment): BaseResponse<String> {
        return commentAPi.createComment(comment)
    }
}