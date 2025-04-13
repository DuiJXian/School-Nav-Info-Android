package com.xz.schoolnavinfo.di

import android.content.Context
import com.xz.schoolnavinfo.data.dao.remote.FileApi
import com.xz.schoolnavinfo.data.repository.FileRepositoryImpl
import com.xz.schoolnavinfo.domain.repository.FileRepository
import com.xz.schoolnavinfo.domain.use_case.FileUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object FileModule {
    @Provides
    @Singleton
    fun provideFileApi(retrofit: Retrofit): FileApi {
        return retrofit.create(FileApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFileRepository(fileApi: FileApi): FileRepository {
        return FileRepositoryImpl(fileApi)
    }

    @Provides
    @Singleton
    fun provideFileCases(
        fileRepository: FileRepository,
        @ApplicationContext context: Context
    ): FileUseCases {
        return FileUseCases(fileRepository, context)
    }
}