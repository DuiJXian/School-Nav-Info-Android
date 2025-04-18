package com.xz.schoolnavinfo.presentation.home

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private val _selectedBtMenuIndex = mutableIntStateOf(0)
    val selectedBtMenuIndex = _selectedBtMenuIndex

    fun changeBTMenu(page: Int) {
        _selectedBtMenuIndex.intValue = page
    }
}