package com.xz.schoolnavinfo.presentation.common.components

import AppColors
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.xz.schoolnavinfo.presentation.common.utils.LocateUtils

@Composable
fun CheckGps(){
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(!LocateUtils.isGpsEnabled(context)) }
    var appColors = AppColors.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("定位功能未开启") },
            text = { Text("请开启定位以使用地图功能") },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.secondary
                    ),
                    onClick = {
                        showDialog = false
                        LocateUtils.openGpsSettings(context)
                    }
                ) {
                    Text("去开启")
                }
            },
            dismissButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.secondary
                    ),
                    onClick = { showDialog = false }
                ) {
                    Text("取消")
                }
            }
        )
    }
}