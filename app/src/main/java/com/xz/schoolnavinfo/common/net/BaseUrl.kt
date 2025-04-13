package com.xz.schoolnavinfo.common.net

const val BASE_URL = "http://192.168.1.104:8080"

fun getStaticCompleteUrl(path: String):String{
    return "$BASE_URL/uploads/$path"
}