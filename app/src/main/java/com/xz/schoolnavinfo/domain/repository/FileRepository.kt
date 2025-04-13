package com.xz.schoolnavinfo.domain.repository

import android.content.Context
import com.xz.schoolnavinfo.common.model.BaseResponse

interface FileRepository {
    suspend fun uploadImage(path: String, type: String, context: Context): BaseResponse<String>
}