package com.xz.schoolnavinfo.common.net

import android.app.Application
import com.xz.schoolnavinfo.common.utils.DataStoreUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val context: Application
) : Interceptor {
    private var token: String? = null


    suspend fun loadToken() {
        token = DataStoreUtils.getData(context, DataStoreUtils.Keys.TOKEN, "")
    }


    suspend fun setToken(newToken: String) {
        token = newToken
        DataStoreUtils.saveData(context, DataStoreUtils.Keys.TOKEN, newToken)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val newRequest = if (!token.isNullOrBlank()) {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            request
        }
        return chain.proceed(newRequest)
    }
}