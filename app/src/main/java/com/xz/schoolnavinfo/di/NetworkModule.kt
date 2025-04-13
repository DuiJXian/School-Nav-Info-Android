package com.xz.schoolnavinfo.di

import android.app.Application
import com.xz.schoolnavinfo.common.event.GlobalFlow
import com.xz.schoolnavinfo.common.net.AuthInterceptor
import com.xz.schoolnavinfo.common.net.BASE_URL
import com.xz.schoolnavinfo.common.net.NetExceptionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideCoroutineScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.IO) // 使用 IO 调度器
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(app: Application, scope: CoroutineScope): AuthInterceptor {
        return AuthInterceptor(app)
    }


    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideGlobalFlow(): GlobalFlow {
        return GlobalFlow()
    }

    @Provides
    @Singleton
    fun provideNetErrManager(netExceptionFlow: GlobalFlow): NetExceptionManager {
        return NetExceptionManager(netExceptionFlow)
    }
}
