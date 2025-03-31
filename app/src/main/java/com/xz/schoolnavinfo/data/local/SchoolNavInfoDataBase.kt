package com.xz.schoolnavinfo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xz.schoolnavinfo.domain.model.LocalPoiInfo

@Database(
    entities = [LocalPoiInfo::class],
    version = 1,
    exportSchema = false
)
abstract class SchoolNavInfoDataBase : RoomDatabase() {
    abstract val localPoiInfoDao: LocalPoiInfoDao

    companion object{
        const val DATABASE_NAME ="school_nav_info_db"
    }
}