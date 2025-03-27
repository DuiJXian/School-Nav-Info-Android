package com.xz.schoolnavinfo.presentation.map

import com.xz.schoolnavinfo.domain.model.MPoiInfo

sealed class MPoiInfoEvent {
    data class DeleteMPoiInfo(val mPoiInfo: MPoiInfo): MPoiInfoEvent()
    data class InsertMPoiInfo(val mPoiInfo: MPoiInfo): MPoiInfoEvent()
    data class GetMPoiInfoByUid(val uid: String): MPoiInfoEvent()
}