package com.xz.schoolnavinfo.domain.data.type

sealed class ArticleType(val name:String) {
    data object Discuss: ArticleType("DISCUSS")
    data object Activity: ArticleType("ACTIVITY")
}