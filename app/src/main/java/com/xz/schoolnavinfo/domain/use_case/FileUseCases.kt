package com.xz.schoolnavinfo.domain.use_case

import android.content.Context
import com.xz.schoolnavinfo.domain.data.resp.BaseResponse
import com.xz.schoolnavinfo.domain.repository.FileRepository

class FileUseCases(private val fileRepository: FileRepository, private val context: Context) {
    suspend fun uploadImage(path: String, type: String): BaseResponse<String> {
        return fileRepository.uploadImage(path, type, context)
    }
}