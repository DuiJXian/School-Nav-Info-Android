package com.xz.schoolnavinfo.data.dao.remote

import com.xz.schoolnavinfo.common.model.BaseResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileApi {
    @Multipart
    @POST("/api/files/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part, @Part("type") type: RequestBody): BaseResponse<String>
}