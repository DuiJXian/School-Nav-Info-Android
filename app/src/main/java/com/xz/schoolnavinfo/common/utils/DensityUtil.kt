package com.xz.schoolnavinfo.common.utils

import android.content.Context
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object DensityUtil {

    fun dip2px(context: Context, dpValue: Dp): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue.value * scale + 0.5f).toInt()
    }

    fun px2dip(context: Context, pxValue: Float): Dp {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale).dp
    }
}
