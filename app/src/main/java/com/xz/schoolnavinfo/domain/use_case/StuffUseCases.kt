package com.xz.schoolnavinfo.domain.use_case

import com.xz.schoolnavinfo.domain.data.resp.BaseResponse
import com.xz.schoolnavinfo.domain.data.dto.StuffDTO
import com.xz.schoolnavinfo.domain.data.entity.Stuff
import com.xz.schoolnavinfo.domain.repository.StuffRepository
import javax.inject.Inject

class StuffUseCases @Inject constructor(
    private val stuffRepository: StuffRepository
) {
    suspend fun createStuff(stuff: Stuff?): BaseResponse<Unit> {
        return stuffRepository.createStuff(stuff)
    }

    suspend fun deleteById(id: String): BaseResponse<Unit> {
        return stuffRepository.deleteById(id)
    }

    suspend fun updateStatus(id: String): BaseResponse<Unit> {
        return stuffRepository.updateStatus(id)
    }

    suspend fun getStuffList(): BaseResponse<List<StuffDTO>> {
        return stuffRepository.getStuffList()
    }

    suspend fun getStuffById(id: String): BaseResponse<StuffDTO> {
        return stuffRepository.getStuffById(id)
    }

    suspend fun searchStuffList(text: String): BaseResponse<List<StuffDTO>> {
        return stuffRepository.searchStuffList(text)
    }
}