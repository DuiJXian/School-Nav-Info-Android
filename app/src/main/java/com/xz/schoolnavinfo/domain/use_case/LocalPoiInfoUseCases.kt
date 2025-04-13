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

    suspend fun insertMPoiInfo(mPoiInfo: LocalPoiInfo) {
        return mPoiInfoRepository.insertLocalPoiInfo(mPoiInfo)
    }

    suspend fun deleteMPoiInfo(mPoiInfo: LocalPoiInfo) {
        return mPoiInfoRepository.deleteLocalPoiInfo(mPoiInfo)
    }

    suspend fun updateMPoiInfo(mPoiInfo: LocalPoiInfo){
        return mPoiInfoRepository.updateLocalPoiInfo(mPoiInfo)
    }
}