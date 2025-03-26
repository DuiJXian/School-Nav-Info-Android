package com.xz.schoolnavinfo.presentation.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor():ViewModel() {
    private val _homeState = MutableStateFlow(HomeState(
        selectedMenuIndex = 0
    ))
    val homeState:StateFlow<HomeState> = _homeState

    fun onEvent(event: HomeEvent){
        when(event){
            is HomeEvent.ChangeBTMenu -> {
                _homeState.value = homeState.value.copy(
                    selectedMenuIndex = event.selectedMenuIndex
                )
            }
        }
    }
}