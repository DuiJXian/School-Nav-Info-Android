package com.xz.schoolnavinfo.common.utils

import android.annotation.SuppressLint

object UnitCovertUtils {
    @SuppressLint("DefaultLocale")
    fun metersToKilometers(distance: Int): String {
        if (distance<1000){
            return "${distance}m"
        }
        val kilometers = distance / 1000.0
        return "${String.format("%.1f", kilometers)}km"
    }

}