package com.xz.schoolnavinfo.di

import com.xz.schoolnavinfo.common.net.NetExceptionManager
import com.xz.schoolnavinfo.data.dao.remote.UserApi
import com.xz.schoolnavinfo.data.repository.UserRepositoryImpl
import com.xz.schoolnavinfo.domain.repository.UserRepository
import com.xz.schoolnavinfo.domain.use_case.UserUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {

    @Provides
    @Singleton
    fun provideUserDao(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(userApi: UserApi): UserRepository{
        return UserRepositoryImpl(userApi)
    }

    @Provides
    @Singleton
    fun provideUserCases(authRepository: UserRepository): UserUseCases{
        return UserUseCases(authRepository)
    }
}