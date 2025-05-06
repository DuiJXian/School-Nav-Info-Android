package com.xz.schoolnavinfo.domain.data.resp

// 最外层响应包装
data class BaseResponse<T>(
    val code: String,
    val message: String,
    val data: T
){
    companion object {
        fun <T> fail(code: String = "fail", message: String = "请求失败"): BaseResponse<Nothing?> {
            return BaseResponse(code, message, null)
        }
    }
}

// 中间的 data 对象，包含 list、pageNum 等分页信息
data class PageResponse<T>(
    val list: List<T>,
    val pageNum: Int,
    val pageSize: Int,
    val total: Int
)
