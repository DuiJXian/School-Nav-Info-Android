package com.xz.schoolnavinfo.presentation.map.components

import AppColors
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xz.schoolnavinfo.R


@Composable
fun LocateNow(
    modifier: Modifier = Modifier
) {
    var appColors = AppColors.current
    Box(
        modifier = modifier
            .size(36.dp)
            .shadow(5.dp, shape = CircleShape)
            .background(appColors.bgSecondary, shape = CircleShape)
    ) {
        Image(
            painter = painterResource(R.drawable.position),
            contentDescription = "移动到当前位置",
            colorFilter = ColorFilter.tint(color = Color.Black),
            modifier = Modifier
                .align(Alignment.Center)
                .width(30.dp)
                .height(30.dp)
        )
    }
}