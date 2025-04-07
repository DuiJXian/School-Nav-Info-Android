package com.xz.schoolnavinfo.presentation.common.compose

import android.annotation.SuppressLint
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel

@SuppressLint("ContextCastToActivity")
@Composable
inline fun <reified VM : ViewModel> HiltActivityViewModel(): VM {
    val activity = LocalContext.current as ComponentActivity
    return hiltViewModel(activity)
}
