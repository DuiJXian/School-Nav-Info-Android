package com.xz.schoolnavinfo.common.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object JsonUtil {
    val gson = Gson()

    // 对象转 JSON 字符串
    fun toJson(obj: Any?): String {
        return gson.toJson(obj)
    }

    // JSON 字符串转对象
    inline fun <reified T> fromJson(json: String): T? {
        return try {
            gson.fromJson(json, T::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // JSON 字符串转列表
    inline fun <reified T> fromJsonList(json: String): List<T>? {
        return try {
            val type = object : TypeToken<List<T>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // JSON 转 Map<String, Any>
    fun fromJsonToMap(json: String): Map<String, Any>? {
        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Map<String, Any> 转 JSON
    fun toJsonFromMap(map: Map<String, Any>?): String {
        return gson.toJson(map)
    }
}
