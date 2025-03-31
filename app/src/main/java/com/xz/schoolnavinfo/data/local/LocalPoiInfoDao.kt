package com.xz.schoolnavinfo.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.xz.schoolnavinfo.domain.model.LocalPoiInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface LocalPoiInfoDao {
    @Query("SELECT * FROM mpoiinfo")
    fun getLocalPoiInfos(): Flow<List<LocalPoiInfo>>

    @Query("SELECT * FROM mpoiinfo WHERE uid = :uid")
    suspend fun getLocalPoiInfoByUid(uid: String): LocalPoiInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalPoiInfo(mPoiInfo: LocalPoiInfo)

    @Delete
    suspend fun deleteLocalPoiInfo(mPoiInfo: LocalPoiInfo)

    @Update
    suspend fun updateLocalPoiInfo(mPoiInfo: LocalPoiInfo)
}