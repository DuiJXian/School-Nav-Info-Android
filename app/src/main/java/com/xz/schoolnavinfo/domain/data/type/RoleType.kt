package com.xz.schoolnavinfo.domain.data.type

sealed class RoleType(val name:String) {
    data object ADMIN: RoleType("ADMIN")
    data object NORMAL: RoleType("NORMAL")
}