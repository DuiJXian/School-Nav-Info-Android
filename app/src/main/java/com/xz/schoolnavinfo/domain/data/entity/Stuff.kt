package com.xz.schoolnavinfo.domain.data.entity

data class Stuff(
    val id: String? = null,
    val desc: String,
    val createTime: String? = null,
    val happenTime: String,
    val finishTime: String? = null,
    val location: String,
    val address: String,
    val publisherId: String? = null,
    val imageUrl: String,
    val type: Boolean = false,
    val status: Boolean = false
)
