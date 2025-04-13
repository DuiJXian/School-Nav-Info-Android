package com.xz.schoolnavinfo.presentation.common.viewmodel

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ImageUrlListState(
    val list: List<String> = emptyList(),
    val startIndex: Int = 0,
    val displayHeight: Dp = 0.dp
)