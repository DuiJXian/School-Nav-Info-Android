package com.xz.schoolnavinfo.presentation.map

import com.xz.schoolnavinfo.domain.model.LocalPoiInfo

sealed class LocalInfoEvent {
    data class DeleteMPoiInfo(val mPoiInfo: LocalPoiInfo): LocalInfoEvent()
    data class InsertMPoiInfo(val mPoiInfo: LocalPoiInfo): LocalInfoEvent()
    data class GetMPoiInfoByUid(val uid: String): LocalInfoEvent()
    data class UpdateMPoiInfo(val mPoiInfo: LocalPoiInfo): LocalInfoEvent()
}