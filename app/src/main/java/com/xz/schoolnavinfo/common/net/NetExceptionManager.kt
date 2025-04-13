package com.xz.schoolnavinfo.common.net

import android.util.Log
import com.xz.schoolnavinfo.common.event.GlobalFlow
import com.xz.schoolnavinfo.common.event.NetExceptionEvent
import retrofit2.HttpException
import java.io.IOException

class NetExceptionManager(
    private val globalFlow: GlobalFlow
) {

    // 在这里捕获网络异常并发送错误消息
    suspend fun <T> safeApiCall(apiCall: suspend () -> T) : T?{
        return try {
            apiCall()
        } catch (e: HttpException) {
            Log.e("TAG", "safeApiCall: ${e.message}")
            when (e.code()) {
                401 -> {
                    globalFlow.onNetEvent(NetExceptionEvent.Code401)
                }

                403 -> {
                    globalFlow.onNetEvent(NetExceptionEvent.Code403)
                }

                else -> {
                    globalFlow.onNetEvent(NetExceptionEvent.CodeOther(e.code(), e.message ?: ""))
                }
            }
            null
        } catch (e: IOException) {
            Log.e("TAG", "safeApiCall: ${e.message}")
            globalFlow.onNetEvent(NetExceptionEvent.IOException(e))
            null
        } catch (e: Exception) {
            Log.e("TAG", "safeApiCall: ${e.message}")
            globalFlow.onNetEvent(NetExceptionEvent.OtherException(e))
            null
        }
    }
}
