package com.xz.schoolnavinfo.common.net

const val BASE_URL = "http://192.168.1.105:8080"

fun montageCompleteUrl(path: String):String{
    return "$BASE_URL/uploads/$path"
}