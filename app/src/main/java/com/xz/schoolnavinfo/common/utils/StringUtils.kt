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


    fun parseDataClassToJson(input: String): String {
        // 替换掉类名（例如 ArticleDTO(...) -> {...）
        val replaced = input.replace(Regex("""\w+\(""")) { "{" }
            .replace(")", "}")
            .replace("=", ": ")
        // 添加双引号包裹键和值（可能需要更复杂的处理逻辑）
        val quoted = replaced.replace(Regex("""(\w+): """)) { "\"${it.groupValues[1]}\":" }
        return quoted
    }


}