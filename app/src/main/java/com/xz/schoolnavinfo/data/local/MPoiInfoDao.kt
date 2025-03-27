package com.xz.schoolnavinfo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.xz.schoolnavinfo.domain.model.MPoiInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface MPoiInfoDao {
    @Query("SELECT * FROM mpoiinfo")
    fun getMPoiInfos(): Flow<List<MPoiInfo>>

    @Query("SELECT * FROM mpoiinfo WHERE uid = :uid")
    suspend fun getMPoiInfoByUid(uid: String): MPoiInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMPoiInfo(mPoiInfo: MPoiInfo)

    @Delete
    suspend fun deleteMPoiInfo(mPoiInfo: MPoiInfo)
}