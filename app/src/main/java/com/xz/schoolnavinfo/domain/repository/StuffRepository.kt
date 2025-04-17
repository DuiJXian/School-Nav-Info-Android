package com.xz.schoolnavinfo.domain.repository

import com.xz.schoolnavinfo.common.model.BaseResponse
import com.xz.schoolnavinfo.domain.data.dto.StuffDTO
import com.xz.schoolnavinfo.domain.data.entity.Stuff
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface StuffRepository {
    suspend fun createStuff(stuff: Stuff?): BaseResponse<Unit>

    suspend fun deleteById(id: String): BaseResponse<Unit>

    suspend fun updateStatus(id: String): BaseResponse<Unit>

    suspend fun getStuffList(): BaseResponse<List<StuffDTO>>

    suspend fun getStuffById(id: String): BaseResponse<StuffDTO>
}