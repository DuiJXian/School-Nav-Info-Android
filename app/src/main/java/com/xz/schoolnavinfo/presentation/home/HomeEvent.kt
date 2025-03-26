package com.xz.schoolnavinfo.presentation.home

sealed class HomeEvent{
    data class ChangeBTMenu(val selectedMenuIndex: Int): HomeEvent()
}