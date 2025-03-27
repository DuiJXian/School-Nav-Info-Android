package com.xz.schoolnavinfo.domain.use_case

import com.xz.schoolnavinfo.domain.model.MPoiInfo
import com.xz.schoolnavinfo.domain.repository.MPoiInfoRepository
import kotlinx.coroutines.flow.Flow

class MPoiInfoUseCases(
    private val mPoiInfoRepository: MPoiInfoRepository
) {
    fun getMPoiInfos(): Flow<List<MPoiInfo>> {
        return mPoiInfoRepository.getMPoiInfos()
    }

    suspend fun getMPoiInfoByUid(uid: String): MPoiInfo? {
        return mPoiInfoRepository.getMPoiInfoByUid(uid)
    }

    suspend fun insertMPoiInfo(mPoiInfo: MPoiInfo) {
        return mPoiInfoRepository.insertMPoiInfo(mPoiInfo)
    }

    suspend fun deleteMPoiInfo(mPoiInfo: MPoiInfo) {
        return mPoiInfoRepository.deleteMPoiInfo(mPoiInfo)
    }
}