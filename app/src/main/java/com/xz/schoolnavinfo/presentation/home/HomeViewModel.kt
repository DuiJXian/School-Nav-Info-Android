package com.xz.schoolnavinfo.presentation.home

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor():ViewModel() {
    private val _selectedBtMenuIndex = mutableIntStateOf(0)
    val selectedBtMenuIndex = _selectedBtMenuIndex

    fun onEvent(event: HomeEvent){
        when(event){
            is HomeEvent.ChangeBTMenu -> {
                _selectedBtMenuIndex.value = event.selectedMenuIndex
            }
        }
    }
}