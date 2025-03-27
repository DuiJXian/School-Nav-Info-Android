package com.xz.schoolnavinfo.domain.repository

import com.xz.schoolnavinfo.domain.model.MPoiInfo
import kotlinx.coroutines.flow.Flow

interface MPoiInfoRepository {
    fun getMPoiInfos(): Flow<List<MPoiInfo>>

    suspend fun getMPoiInfoByUid(uid: String): MPoiInfo?

    suspend fun insertMPoiInfo(mPoiInfo: MPoiInfo)

    suspend fun deleteMPoiInfo(mPoiInfo: MPoiInfo)
}