package com.xz.schoolnavinfo.common.net

import com.xz.schoolnavinfo.common.model.BaseResponse
import retrofit2.HttpException
import java.io.IOException

class NetExceptionManager(
    private val netExceptionFlow: NetExceptionFlow
) {

    // 在这里捕获网络异常并发送错误消息
    suspend fun <T> safeApiCall(apiCall: suspend () -> T) : T?{
        return try {
            apiCall()
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> {
                    netExceptionFlow.onEvent(NetException.Code401)
                }

                403 -> {
                    netExceptionFlow.onEvent(NetException.Code403)
                }

                else -> {
                    netExceptionFlow.onEvent(NetException.CodeOther(e.code(), e.message ?: ""))
                }
            }
            null
        } catch (e: IOException) {
            netExceptionFlow.onEvent(NetException.IOException(e))
            null
        } catch (e: Exception) {
            netExceptionFlow.onEvent(NetException.OtherException(e))
            null
        }
    }
}
