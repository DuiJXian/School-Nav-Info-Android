package com.xz.schoolnavinfo.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.xz.schoolnavinfo.common.utils.DensityUtil
import com.xz.schoolnavinfo.presentation.common.compose.ImageHorizontalScroll

@Composable
fun TestScreen() {
    Test()
    Log.e("TAG", "TestScreenStart: ${DensityUtil.px2dip(LocalContext.current, 4f)}")
}

@Composable
@Preview
fun Test() {

}
