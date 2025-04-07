package com.xz.schoolnavinfo.common.utils

class StringUtils {

    companion object

    fun limitLength(str: String, maxLength: Int, endStr: String = ""): String {
        return if (str.isBlank()) {
            ""
        } else {
            str.take(maxLength) + endStr
        }
    }
}