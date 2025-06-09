package com.xz.schoolnavinfo.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun CustomTopBar(
    barHeight: Dp = 56.dp,
    title: String? = null,
    leftContent: @Composable () -> Unit = {},
    rightContent: @Composable () -> Unit = {},
    onBack: () -> Unit
) {
    val appColors = AppColors.current
    Column {
        Row(Modifier.height(barHeight), verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier
                    .padding(start = 10.dp)
                    .fillMaxHeight()
                    .weight(1f),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { onBack() },
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
                if (title != null) {
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text = title,
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = appColors.fontPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
                leftContent()
            }


            Box(
                Modifier
                    .padding(end = 10.dp)
                    .fillMaxHeight()
                    .weight(.8f),
                contentAlignment = Alignment.CenterEnd
            ) {
                rightContent()
            }
        }
        Spacer(Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(appColors.greyLight))
    }
}