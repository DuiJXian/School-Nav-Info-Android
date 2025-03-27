package com.xz.schoolnavinfo.data.repository

import android.util.Log
import com.xz.schoolnavinfo.data.local.MPoiInfoDao
import com.xz.schoolnavinfo.domain.model.MPoiInfo
import com.xz.schoolnavinfo.domain.repository.MPoiInfoRepository
import kotlinx.coroutines.flow.Flow

class MPoiInfoRespImp(private val dao: MPoiInfoDao) : MPoiInfoRepository {
    override fun getMPoiInfos(): Flow<List<MPoiInfo>> {
        return dao.getMPoiInfos()
    }

    override suspend fun getMPoiInfoByUid(uid: String): MPoiInfo? {
        return dao.getMPoiInfoByUid(uid)
    }

    override suspend fun insertMPoiInfo(mPoiInfo: MPoiInfo) {
        return dao.insertMPoiInfo(mPoiInfo)
    }

    override suspend fun deleteMPoiInfo(mPoiInfo: MPoiInfo) {
        return dao.deleteMPoiInfo(mPoiInfo)
    }
}