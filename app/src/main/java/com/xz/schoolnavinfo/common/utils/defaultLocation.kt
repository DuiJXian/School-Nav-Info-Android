package com.xz.schoolnavinfo.common.utils

import android.content.Context
import com.baidu.mapapi.model.LatLng

val defaultLocation: LatLng = LatLng(39.5427, 116.2317)

object LocationUtils {
    //保存位置到本地
    suspend fun saveLocation(context: Context, point: LatLng) {
        DataStoreUtils.saveData(
            context,
            DataStoreUtils.Keys.LONGITUDE,
            point.longitude
        )
        DataStoreUtils.saveData(
            context,
            DataStoreUtils.Keys.LATITUDE,
            point.latitude
        )
    }

    //加载本地位置
    suspend fun loadLocation(context: Context): LatLng {
        val latitude =
            DataStoreUtils.getData(
                context,
                DataStoreUtils.Keys.LATITUDE,
                defaultLocation.latitude
            )
        val longitude =
            DataStoreUtils.getData(
                context,
                DataStoreUtils.Keys.LONGITUDE,
                defaultLocation.longitude
            )
        return LatLng(latitude, longitude)
    }
}