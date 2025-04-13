package com.xz.schoolnavinfo.data.dao.remote.request

data class ArticleRequest(
    val pageSize: Int = 10,
    var pageNum: Int = 1
)