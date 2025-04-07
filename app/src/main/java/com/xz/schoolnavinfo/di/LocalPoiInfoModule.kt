package com.xz.schoolnavinfo.di

import android.app.Application
import androidx.room.Room
import com.xz.schoolnavinfo.data.dao.local.SchoolNavInfoDataBase
import com.xz.schoolnavinfo.data.repository.LocalPoiInfoRepositoryImp
import com.xz.schoolnavinfo.domain.repository.LocalPoiInfoRepository
import com.xz.schoolnavinfo.domain.use_case.LocalPoiInfoUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalPoiInfoModule {

    @Provides
    @Singleton
    fun provideLocalPoiInfoDataBase(app: Application): SchoolNavInfoDataBase {
        return Room.databaseBuilder(
            app,
            SchoolNavInfoDataBase::class.java,
            SchoolNavInfoDataBase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideLocalPoiInfoRepository(db: SchoolNavInfoDataBase): LocalPoiInfoRepository{
        return LocalPoiInfoRepositoryImp(db.localPoiInfoDao)
    }

    @Provides
    fun provideMPoiInfoUseCases(repository: LocalPoiInfoRepository): LocalPoiInfoUseCases{
        return LocalPoiInfoUseCases(repository)
    }

}