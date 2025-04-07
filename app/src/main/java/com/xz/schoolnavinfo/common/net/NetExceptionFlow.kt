package com.xz.schoolnavinfo.common.net

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class NetExceptionFlow{

    private val _netErrFlow = MutableSharedFlow<NetException>()
    val netErrFlow:SharedFlow<NetException> = _netErrFlow.asSharedFlow()

    suspend fun onEvent(event: NetException){
        _netErrFlow.emit(event)
    }
}

sealed class NetException {
    abstract val msg: String

    data object Code401 : NetException() {
        override val msg = "未登录或 token 已过期"
    }

    data object Code403 : NetException() {
        override val msg = "没有权限访问该资源"
    }

    data class CodeOther(val code: Int, val detail: String) : NetException() {
        override val msg = "其他错误（$code）: $detail"
    }

    data class IOException(val e: java.io.IOException) : NetException() {
        override val msg = "网络连接失败: ${e.message}"
    }

    data class OtherException(val e: Throwable) : NetException() {
        override val msg = "未知异常: ${e.message}"
    }
}
