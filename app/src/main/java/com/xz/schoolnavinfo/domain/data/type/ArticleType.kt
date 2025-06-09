package com.xz.schoolnavinfo.domain.data.type

import kotlinx.serialization.Serializable


@Serializable
enum class ArticleType(val title: String) {
    DISCUSS("讨论"),
    ACTIVITY("活动");

    companion object{
        fun getType(type: String): ArticleType {
            return if (type.uppercase() == "DISCUSS") {
                DISCUSS
            } else {
                ACTIVITY
            }
        }
    }

}