package com.xz.schoolnavinfo.common.net

import android.app.Application
import android.util.Log
import com.xz.schoolnavinfo.common.utils.DataStoreUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val context: Application
) : Interceptor {
    private var token: String = ""

    suspend fun setToken(newToken: String) {
        token = newToken
        DataStoreUtils.saveData(context, DataStoreUtils.Keys.TOKEN, newToken)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        if (token.isBlank()) {
            runBlocking {
                token = DataStoreUtils.getData(context, DataStoreUtils.Keys.TOKEN, "")
            }
        }
        val request = chain.request()

        Log.e("AuthInterceptor", "intercept: ${request.url}")

        val newRequest = request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
        return chain.proceed(newRequest)
    }
}