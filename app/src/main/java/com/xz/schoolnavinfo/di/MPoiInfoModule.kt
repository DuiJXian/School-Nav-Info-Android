package com.xz.schoolnavinfo.di

import android.app.Application
import androidx.room.Room
import com.xz.schoolnavinfo.data.local.SchoolNavInfoDataBase
import com.xz.schoolnavinfo.data.repository.MPoiInfoRespImp
import com.xz.schoolnavinfo.domain.repository.MPoiInfoRepository
import com.xz.schoolnavinfo.domain.use_case.MPoiInfoUseCases
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MPoiInfoModule {

    @Provides
    @Singleton
    fun provideMPoiInfoDataBase(app: Application): SchoolNavInfoDataBase{
        return Room.databaseBuilder(
            app,
            SchoolNavInfoDataBase::class.java,
            SchoolNavInfoDataBase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideMPoiInfoRepository(db: SchoolNavInfoDataBase): MPoiInfoRepository{
        return MPoiInfoRespImp(db.mPoiInfoDao)
    }

    @Provides
    fun provideMPoiInfoUseCases(repository: MPoiInfoRepository): MPoiInfoUseCases{
        return MPoiInfoUseCases(repository)
    }

}