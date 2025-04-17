package com.xz.schoolnavinfo.common.utils

object StringUtils {

    fun truncateText(
        text: String,
        maxLength: Int,
        suffix: String = ".."
    ): String {
        if (text.length <= maxLength) return text
        return text.take(maxLength) + suffix
    }

}