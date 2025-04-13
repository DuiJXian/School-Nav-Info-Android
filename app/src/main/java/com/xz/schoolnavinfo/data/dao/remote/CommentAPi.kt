package com.xz.schoolnavinfo.data.dao.remote

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.domain.data.dto.CommentDTO
import com.xz.schoolnavinfo.domain.data.entity.Comment
import retrofit2.http.Body
import retrofit2.http.POST

interface CommentAPi {
    @POST("/api/comment/getComments")
    suspend fun getCommentById(@Body map: Map<String, String>): BaseResponse<List<CommentDTO>>

    @POST("/api/comment/createComment")
    suspend fun createComment(@Body comment: Comment): BaseResponse<String>
}