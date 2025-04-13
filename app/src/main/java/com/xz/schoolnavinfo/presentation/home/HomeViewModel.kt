package com.xz.schoolnavinfo.presentation.home

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _selectedBtMenuIndex = mutableIntStateOf(1)
    val selectedBtMenuIndex = _selectedBtMenuIndex


    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ChangeBTMenu -> {
                _selectedBtMenuIndex.intValue = event.selectedMenuIndex
            }
        }
    }
}