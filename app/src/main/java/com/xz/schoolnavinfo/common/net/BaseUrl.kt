package com.xz.schoolnavinfo.common.net

const val BASE_URL = "http://8.134.62.230:8080"

fun getImagesUrl(path: String):String{
    return "$BASE_URL/images/$path"
}