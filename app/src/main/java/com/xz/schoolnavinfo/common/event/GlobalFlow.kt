package com.xz.schoolnavinfo.common.event

import com.xz.schoolnavinfo.domain.data.type.ArticleType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow


class GlobalFlow{

    private val _netErrFlow = MutableSharedFlow<NetExceptionEvent>()
    val netErrFlow:SharedFlow<NetExceptionEvent> = _netErrFlow.asSharedFlow()

    private val _snackBarShow = MutableSharedFlow<String>()
    val snackBarMsgShow: SharedFlow<String> = _snackBarShow.asSharedFlow()

    private val _refreshData = MutableSharedFlow<ArticleType>(replay = 1)
    val refreshData: SharedFlow<ArticleType> = _refreshData.asSharedFlow()

    suspend fun onRefreshDataEvent(event: ArticleType){
        _refreshData.emit(event)
    }


    suspend fun onNetEvent(event: NetExceptionEvent){
        _netErrFlow.emit(event)
    }

    suspend fun onSnackBarEvent(msg: String){
        _snackBarShow.emit(msg)
    }
}


