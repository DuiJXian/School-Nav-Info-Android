package com.xz.schoolnavinfo.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun LocationBox(
    address: String,
    onClick: () -> Unit = {}
) {
    val appColors = AppColors.current
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .height(34.dp)
            .background(appColors.greyMedium.copy(.5f))
            .clickable {
                onClick()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .padding(start = 5.dp)
                .size(20.dp),
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = appColors.fontSecondary
        )
        Text(
            modifier = Modifier
                .padding(vertical = 5.dp)
                .padding(end = 10.dp),
            text = address.ifBlank { "选择地址.." },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontSize = 16.sp,
                color = appColors.fontSecondary
            )
        )
    }
}