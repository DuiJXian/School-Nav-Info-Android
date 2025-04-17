package com.xz.schoolnavinfo.presentation.campus

sealed class CampusMenu(val title:String, val type:String) {
    data object Discuss: CampusMenu("讨论","DISCUSS")
    data object Activity: CampusMenu("活动","ACTIVITY")
    data object Stuff: CampusMenu("活动","ACTIVITY")
}