package com.xz.schoolnavinfo.di

import com.xz.schoolnavinfo.data.dao.remote.ArticleApi
import com.xz.schoolnavinfo.data.repository.ArticleRepositoryImpl
import com.xz.schoolnavinfo.domain.repository.ArticleRepository
import com.xz.schoolnavinfo.domain.use_case.ArticleUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ArticleModule {

    @Provides
    @Singleton
    fun provideArticleApi(retrofit: Retrofit): ArticleApi {
        return retrofit.create(ArticleApi::class.java)
    }

    @Provides
    @Singleton
    fun provideArticleRepository(articleApi: ArticleApi): ArticleRepository{
        return ArticleRepositoryImpl(articleApi)
    }

    @Provides
    @Singleton
    fun provideArticleCases(articleRepository: ArticleRepository): ArticleUseCases{
        return ArticleUseCases(articleRepository)
    }
}