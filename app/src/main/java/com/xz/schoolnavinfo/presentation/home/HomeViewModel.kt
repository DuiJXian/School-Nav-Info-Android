package com.xz.schoolnavinfo.presentation.home

import android.util.Log
import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xz.schoolnavinfo.common.net.NetExceptionFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val _netExceptionFlow: NetExceptionFlow
) : ViewModel() {
    private val _selectedBtMenuIndex = mutableIntStateOf(1)
    val selectedBtMenuIndex = _selectedBtMenuIndex

    val netExceptionFlow = _netExceptionFlow.netErrFlow

    init {
        viewModelScope.launch {
            _netExceptionFlow.netErrFlow.collectLatest {
                Log.e(TAG, it.msg)
            }
        }
    }


    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ChangeBTMenu -> {
                _selectedBtMenuIndex.intValue = event.selectedMenuIndex
            }
        }
    }
}