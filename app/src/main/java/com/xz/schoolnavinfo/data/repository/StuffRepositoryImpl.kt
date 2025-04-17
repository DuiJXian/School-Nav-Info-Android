package com.xz.schoolnavinfo.data.repository

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.data.dao.remote.StuffApi
import com.xz.schoolnavinfo.domain.data.dto.StuffDTO
import com.xz.schoolnavinfo.domain.data.entity.Stuff
import com.xz.schoolnavinfo.domain.repository.StuffRepository
import javax.inject.Inject

class StuffRepositoryImpl @Inject constructor(
    private val stuffApi: StuffApi
) : StuffRepository {
    override suspend fun createStuff(stuff: Stuff?): BaseResponse<Unit> {
        return stuffApi.createStuff(stuff)
    }

    override suspend fun deleteById(id: String): BaseResponse<Unit> {
        return stuffApi.deleteById(id)
    }

    override suspend fun updateStatus(id: String): BaseResponse<Unit> {
        return stuffApi.updateStatus(id)
    }

    override suspend fun getStuffList(): BaseResponse<List<StuffDTO>> {
        return stuffApi.getStuffList()
    }

    override suspend fun getStuffById(id: String): BaseResponse<StuffDTO> {
        return stuffApi.getStuffById(id)
    }
}