package com.xz.schoolnavinfo.domain.repository

import com.xz.schoolnavinfo.domain.data.entity.LocalPoiInfo
import kotlinx.coroutines.flow.Flow

interface LocalPoiInfoRepository {
    fun getLocalPoiInfos(): Flow<List<LocalPoiInfo>>

    suspend fun getLocalPoiInfoByUid(uid: String): LocalPoiInfo?

    suspend fun insertLocalPoiInfo(mPoiInfo: LocalPoiInfo)

    suspend fun deleteLocalPoiInfo(mPoiInfo: LocalPoiInfo)

    suspend fun updateLocalPoiInfo(mPoiInfo: LocalPoiInfo)
}