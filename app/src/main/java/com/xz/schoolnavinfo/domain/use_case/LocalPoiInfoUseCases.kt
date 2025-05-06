package com.xz.schoolnavinfo.domain.use_case

import com.xz.schoolnavinfo.domain.data.entity.LocalPoiInfo
import com.xz.schoolnavinfo.domain.repository.LocalPoiInfoRepository
import kotlinx.coroutines.flow.Flow

class LocalPoiInfoUseCases(
    private val mPoiInfoRepository: LocalPoiInfoRepository
) {
    fun getMPoiInfos(): Flow<List<LocalPoiInfo>> {
        return mPoiInfoRepository.getLocalPoiInfos()
    }

    suspend fun getMPoiInfoByUid(uid: String): LocalPoiInfo? {
        return mPoiInfoRepository.getLocalPoiInfoByUid(uid)
    }

    suspend fun insertMPoiInfo(localPoiInfo: LocalPoiInfo) {
        return mPoiInfoRepository.insertLocalPoiInfo(localPoiInfo)
    }

    suspend fun deleteMPoiInfo(localPoiInfo: LocalPoiInfo) {
        return mPoiInfoRepository.deleteLocalPoiInfo(localPoiInfo)
    }

    suspend fun updateLocalPoiInfo(localPoiInfo: LocalPoiInfo){
        return mPoiInfoRepository.updateLocalPoiInfo(localPoiInfo)
    }
}