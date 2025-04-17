package com.xz.schoolnavinfo.domain.data.type

sealed class ArticleType(val title:String, val type:String) {
    data object Discuss: ArticleType("讨论","DISCUSS")
    data object Activity: ArticleType("活动","ACTIVITY")
}