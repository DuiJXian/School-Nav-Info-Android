package com.xz.schoolnavinfo.common.event

sealed class NetExceptionEvent {
    abstract val msg: String

    data object Code401 : NetExceptionEvent() {
        override val msg = "未登录或token已过期"
    }

    data object Code403 : NetExceptionEvent() {
        override val msg = "没有权限访问该资源"
    }

    data class CodeOther(val code: Int, val detail: String) : NetExceptionEvent() {
        override val msg = "其他错误（$code）: $detail"
    }

    data class IOException(val e: java.io.IOException) : NetExceptionEvent() {
        override val msg = "网络连接失败: ${e.message}"
    }

    data class OtherException(val e: Throwable) : NetExceptionEvent() {
        override val msg = "未知异常: ${e.message}"
    }
}