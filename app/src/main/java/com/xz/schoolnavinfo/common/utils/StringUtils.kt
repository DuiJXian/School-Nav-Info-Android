package com.xz.schoolnavinfo.common.utils

object StringUtils {

    fun truncateText(
        text: String,
        maxLength: Int,
        suffix: String = ".."
    ): String {
        return if (text.length <= maxLength) text
        else text.take(maxLength) + suffix
    }

}