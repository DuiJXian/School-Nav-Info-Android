package com.xz.schoolnavinfo.presentation.map

import com.xz.schoolnavinfo.presentation.common.baidu.RoutePlanType

sealed class NavMsgEvent {
    data class CalculateMsg(val msg: String): NavMsgEvent()
    data class EnterNav(val routePlanType: RoutePlanType) : NavMsgEvent()
}