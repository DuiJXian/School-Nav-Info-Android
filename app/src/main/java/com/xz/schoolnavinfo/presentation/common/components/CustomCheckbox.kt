package com.xz.schoolnavinfo.presentation.common.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomCheckbox(
    state: Boolean = false,
    size: Dp = 24.dp,
    cornerSize: Dp = 5.dp,
    uncheckedColor: Color = Color.Gray,
    checkedColor: Color = Color.Blue,
    checkmarkColor: Color = Color.White,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(RoundedCornerShape(cornerSize))
            .background(uncheckedColor)
            .clickable {
                onCheckedChange(!state)
            }
    ) {
        if (state) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(checkedColor),
                contentAlignment = Alignment.Center
            ) {
                // 绘制勾选标记
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = checkmarkColor
                )
            }
        }
    }
}
