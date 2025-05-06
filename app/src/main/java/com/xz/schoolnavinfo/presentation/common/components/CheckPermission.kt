package com.xz.schoolnavinfo.presentation.common.components

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermission() {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    )
    when{
        //已授予
        permissionsState.allPermissionsGranted -> {

        }
        //被拒绝一次
        permissionsState.shouldShowRationale -> {

        }
        //拒绝多次
        else -> {

        }
    }
    if (!permissionsState.allPermissionsGranted){
        LaunchedEffect(permissionsState) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }
}