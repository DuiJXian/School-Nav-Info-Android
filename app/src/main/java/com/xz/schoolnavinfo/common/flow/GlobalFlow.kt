package com.xz.schoolnavinfo.common.flow

import com.xz.schoolnavinfo.common.exception.NetExceptionEvent
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class GlobalFlow {

    private val _netErrFlow = MutableSharedFlow<NetExceptionEvent>()
    val netErrFlow: SharedFlow<NetExceptionEvent> = _netErrFlow.asSharedFlow()

    private val _snackBarFlow = MutableSharedFlow<String>()
    val snackBarFlow: SharedFlow<String> = _snackBarFlow.asSharedFlow()

    private val _refreshDataFlow = MutableSharedFlow<CampusMenu>(replay = 1)
    val refreshDataFlow: SharedFlow<CampusMenu> = _refreshDataFlow.asSharedFlow()


    suspend fun onRefreshDataEvent(campusMenu: CampusMenu) {
        _refreshDataFlow.emit(campusMenu)
    }

    suspend fun onNetEvent(event: NetExceptionEvent) {
        _netErrFlow.emit(event)
    }
}

sealed class RefreshPage{
    data object Activity: RefreshPage()
    data object Discuss: RefreshPage()
    data object Stuff: RefreshPage()
}


