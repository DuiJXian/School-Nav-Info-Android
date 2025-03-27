package com.xz.schoolnavinfo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xz.schoolnavinfo.domain.model.MPoiInfo

@Database(
    entities = [MPoiInfo::class],
    version = 1,
    exportSchema = false
)
abstract class SchoolNavInfoDataBase : RoomDatabase() {
    abstract val mPoiInfoDao: MPoiInfoDao

    companion object{
        const val DATABASE_NAME ="school_nav_info_db"
    }
}