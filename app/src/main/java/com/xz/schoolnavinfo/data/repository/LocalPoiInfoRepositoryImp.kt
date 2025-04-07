package com.xz.schoolnavinfo.data.repository

import com.xz.schoolnavinfo.data.dao.local.LocalPoiInfoDao
import com.xz.schoolnavinfo.domain.model.entity.LocalPoiInfo
import com.xz.schoolnavinfo.domain.repository.LocalPoiInfoRepository
import kotlinx.coroutines.flow.Flow

class LocalPoiInfoRepositoryImp(private val dao: LocalPoiInfoDao) : LocalPoiInfoRepository {
    override fun getLocalPoiInfos(): Flow<List<LocalPoiInfo>> {
        return dao.getLocalPoiInfos()
    }

    override suspend fun getLocalPoiInfoByUid(uid: String): LocalPoiInfo? {
        return dao.getLocalPoiInfoByUid(uid)
    }

    override suspend fun insertLocalPoiInfo(mPoiInfo: LocalPoiInfo) {
        return dao.insertLocalPoiInfo(mPoiInfo)
    }

    override suspend fun deleteLocalPoiInfo(mPoiInfo: LocalPoiInfo) {
        return dao.deleteLocalPoiInfo(mPoiInfo)
    }

    override suspend fun updateLocalPoiInfo(mPoiInfo: LocalPoiInfo) {
        return dao.updateLocalPoiInfo(mPoiInfo)
    }
}