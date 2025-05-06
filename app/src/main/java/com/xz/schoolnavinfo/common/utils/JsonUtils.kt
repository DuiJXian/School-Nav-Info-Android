package com.xz.schoolnavinfo.common.utils

import com.google.gson.Gson

object JsonUtils {
    val gson = Gson()

    fun toJson(src:Any):String{
        return gson.toJson(src)
    }

    inline fun <reified T> fromJson(json:String):T{
        return gson.fromJson(json,T::class.java)
    }

}