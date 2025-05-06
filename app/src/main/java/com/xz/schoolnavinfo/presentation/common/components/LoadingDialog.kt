package com.xz.schoolnavinfo.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun LoadingDialog(show: Boolean) {
    val appColors = AppColors.current
    if (show) {
        Dialog(onDismissRequest = { /* 禁止取消 */ }) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(appColors.bgPrimary, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = appColors.primary
                )
            }
        }
    }

}
