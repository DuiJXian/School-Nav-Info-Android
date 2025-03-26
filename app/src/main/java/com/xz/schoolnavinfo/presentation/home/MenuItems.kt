package com.xz.schoolnavinfo.presentation.home

import com.xz.schoolnavinfo.R

// This class is used to store a list of BNTabItemModel objects


const val TAG = "myLog"

object MenuItems {
    data class MenuItem(val index: Int, val name: String, val iconRes: Int)

    val items = listOf(
        MenuItem(0,"地图", R.drawable.map_24px),
        MenuItem(1,"校圈", R.drawable.forum_24px),
        MenuItem(2,"我", R.drawable.person_24px)
    )
}