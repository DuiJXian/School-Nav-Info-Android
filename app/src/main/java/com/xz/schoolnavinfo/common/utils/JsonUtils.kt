package com.xz.schoolnavinfo.common.utils

import android.util.Log
import com.google.gson.Gson
import kotlin.reflect.KClass

object JsonUtils {
    val gson = Gson()

    fun toJson(src: Any?): String {
        if (src == null) return ""
        return gson.toJson(src)
    }

    inline fun <reified T : Any> fromJson(json: String): T {
        return gson.fromJson(json, T::class.java)
    }

    fun <T> fromJson(json: String, clazz: Class<T>): T {
        Log.e("TAG", "JsonUtils fromJson: $json")
        return gson.fromJson(json, clazz)
    }

}