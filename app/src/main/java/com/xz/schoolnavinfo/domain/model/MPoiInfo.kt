package com.xz.schoolnavinfo.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mpoiinfo")
data class MPoiInfo(
    @PrimaryKey val uid: String,            // uid
    val name: String,           // Poi名称
    val order: Int,             // 序号
    val iconPic: String,        // 图标
    val address: String,        // 地址
    val telephone: String,      // 联系电话
)
