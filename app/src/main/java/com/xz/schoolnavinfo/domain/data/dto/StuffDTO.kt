package com.xz.schoolnavinfo.domain.data.dto

import com.xz.schoolnavinfo.domain.data.entity.Stuff
import com.xz.schoolnavinfo.domain.data.entity.UserInfo

data class StuffDTO(
    val stuff: Stuff,
    val userInfo: UserInfo?
)
