package com.xz.schoolnavinfo.di

import com.xz.schoolnavinfo.data.dao.remote.StuffApi
import com.xz.schoolnavinfo.data.repository.StuffRepositoryImpl
import com.xz.schoolnavinfo.domain.repository.StuffRepository
import com.xz.schoolnavinfo.domain.use_case.StuffUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object StuffModule {

    @Provides
    @Singleton
    fun provideStuffDao(retrofit: Retrofit): StuffApi {
        return retrofit.create(StuffApi::class.java)
    }

    @Provides
    @Singleton
    fun provideStuffRepository(stuffApi: StuffApi): StuffRepository {
        return StuffRepositoryImpl(stuffApi)
    }

    @Provides
    @Singleton
    fun provideStuffUseCases(stuffRepository: StuffRepository): StuffUseCases {
        return StuffUseCases(stuffRepository)
    }
}