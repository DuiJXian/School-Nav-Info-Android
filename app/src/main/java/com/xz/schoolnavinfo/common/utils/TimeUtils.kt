package com.xz.schoolnavinfo.common.utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
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


    fun formatTimeDifference(pastTime: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val pastDate = formatter.parse(pastTime) ?: return "时间格式错误"
        val currentDate = Date()

        val diffInMillis = currentDate.time - pastDate.time
        val diffInSeconds = diffInMillis / 1000
        val diffInMinutes = diffInSeconds / 60
        val diffInHours = diffInMinutes / 60
        val diffInDays = diffInHours / 24

        return when {
            diffInSeconds < 60 -> "$diffInSeconds 秒钟前"  // 小于60秒
            diffInMinutes < 60 -> "$diffInMinutes 分钟前"  // 小于60分钟
            diffInHours < 24 -> "$diffInHours 小时前"      // 小于24小时
            diffInDays in 1..30 -> "$diffInDays 天前"       // 小于30天
            else -> {
                val calendar = Calendar.getInstance()
                calendar.time = pastDate
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)

                // 如果年份相同，显示月日，如4月1日
                if (calendar.get(Calendar.YEAR) == currentYear) {
                    "${calendar.get(Calendar.MONTH) + 1}月${calendar.get(Calendar.DAY_OF_MONTH)}日"
                } else {
                    // 否则显示年份，月日，如2024年4月1日
                    "${calendar.get(Calendar.YEAR)}年${calendar.get(Calendar.MONTH) + 1}月${calendar.get(Calendar.DAY_OF_MONTH)}日"
                }
            }
        }
    }

    fun formatToMonthDayHourMinute(input: String): String {
        return try {
            val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val outputFormat = DateTimeFormatter.ofPattern("MM-dd HH:mm")
            val dateTime = LocalDateTime.parse(input, inputFormat)
            dateTime.format(outputFormat)
        } catch (e: Exception) {
            input // 出错就返回原始字符串
        }
    }

}