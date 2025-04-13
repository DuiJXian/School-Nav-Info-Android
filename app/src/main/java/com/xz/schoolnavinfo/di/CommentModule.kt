package com.xz.schoolnavinfo.di

import com.xz.schoolnavinfo.data.dao.remote.CommentAPi
import com.xz.schoolnavinfo.data.repository.CommentRepositoryImpl
import com.xz.schoolnavinfo.domain.repository.CommentRepository
import com.xz.schoolnavinfo.domain.use_case.CommentUserCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommentModule {
    @Provides
    @Singleton
    fun provideCommentApi(retrofit: Retrofit): CommentAPi {
        return retrofit.create(CommentAPi::class.java)
    }

    @Provides
    @Singleton
    fun provideCommentRepository(commentAPi: CommentAPi): CommentRepository {
        return CommentRepositoryImpl(commentAPi);
    }

    @Provides
    @Singleton
    fun provideCommentUseCases(commentRepository: CommentRepository): CommentUserCases {
        return CommentUserCases(commentRepository);
    }
}