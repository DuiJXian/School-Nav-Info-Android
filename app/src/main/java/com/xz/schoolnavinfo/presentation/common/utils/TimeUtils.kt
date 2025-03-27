package com.xz.schoolnavinfo.presentation.common.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {

    // 判断当前时间是否在给定的时间区间内
    fun isTimeInRange(timeRange: String): Boolean {
        var res = false
        if(timeRange.contains(",")){
            for (time in timeRange.split(",")){
                if (timeCheck(time)) return true
            }
        }else{
            res = timeCheck(timeRange)
        }
        return res
    }

    private fun timeCheck(timeRange: String): Boolean {
        // 将时间区间通过 "-" 分割成开始时间和结束时间
        val times = timeRange.split("-")
        Log.e("TAG", "timeCheck: $times")
        if (times.size != 2) {
            throw IllegalArgumentException("时间区间格式不正确${timeRange}，正确格式为 'HH:mm-HH:mm'")
        }

        val startTime = times[0]
        val endTime = times[1]

        // 使用 SimpleDateFormat 来解析时间字符串
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())

        // 获取当前时间
        val currentTime = format.format(Date())

        // 将字符串时间转换为 Date 对象
        val startDate = format.parse(startTime)
        val endDate = format.parse(endTime)
        val currentDate = format.parse(currentTime)

        // 判断当前时间是否在开始时间和结束时间之间
        return if (endDate.before(startDate)) {
            // 如果结束时间小于开始时间，表示时间区间跨越了午夜
            currentDate.after(startDate) || currentDate.before(endDate)
        } else {
            // 普通情况，判断当前时间是否在区间内
            currentDate.after(startDate) && currentDate.before(endDate)
        }
    }

    fun formatTime(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val remainingSeconds = seconds % 60

        return when {
            hours > 0 -> String.format("%d时%d分%d秒", hours, minutes, remainingSeconds)
            minutes > 0 -> String.format("%d分%d秒", minutes, remainingSeconds)
            else -> "${remainingSeconds}秒"
        }
    }
}
