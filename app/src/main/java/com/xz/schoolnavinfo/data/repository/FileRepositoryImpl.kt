package com.xz.schoolnavinfo.data.repository

import android.content.Context
import android.webkit.MimeTypeMap
import com.xz.schoolnavinfo.domain.data.resp.BaseResponse
import com.xz.schoolnavinfo.data.dao.remote.FileApi
import com.xz.schoolnavinfo.domain.repository.FileRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FileRepositoryImpl @Inject constructor(
    private val fileApi: FileApi
) : FileRepository {
    override suspend fun uploadImage(
        path: String,
        type: String,
        context: Context
    ): BaseResponse<String> {
        val originalFile = File(path)
        if (!originalFile.exists()) {
            return BaseResponse("fail", "文件不存在", "文件不存在")
        }

        // 1. suspend 等待压缩结果
        val compressedFile = compressImageWithLuban(context, originalFile)
            ?: return BaseResponse("fail", "压缩失败", "压缩失败")

        // 2. 转为 Multipart
        val mimeType = getMimeType(compressedFile) ?: "image/*"
        val requestFile = compressedFile.asRequestBody(mimeType.toMediaTypeOrNull())
        val multipart = MultipartBody.Part.createFormData("file", compressedFile.name, requestFile)

        return fileApi.uploadImage(multipart, type.toRequestBody("text/plain".toMediaType()))
    }
}

private suspend fun compressImageWithLuban(context: Context, file: File): File? =
    suspendCoroutine { continuation ->
        Luban.with(context)
            .load(file)
            .ignoreBy(200)
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {}

                override fun onSuccess(file: File) {
                    continuation.resume(file) // 返回压缩后的文件
                }

                override fun onError(e: Throwable?) {
                    continuation.resume(null) // 返回 null 表示失败
                }
            }).launch()
    }


private fun getMimeType(file: File): String? {
    val extension = MimeTypeMap.getFileExtensionFromUrl(file.absolutePath)
    return MimeTypeMap.getSingleton()
        .getMimeTypeFromExtension(extension.lowercase(Locale.getDefault()))
}
