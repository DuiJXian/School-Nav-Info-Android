package com.xz.schoolnavinfo.presentation.theme
import androidx.compose.ui.graphics.Color

// 定义自定义颜色
data class CustomColors(
    val primary: Color,
    val info: Color,
    val warn: Color,
    val err: Color,
    val fontPrimary: Color,
    val fontSecondary: Color,
    val bgScreen: Color,
    val bgPrimary: Color,
    val bgSecondary: Color,
    val greyHeavy: Color,
    val greyMedium: Color,
    val greyLight: Color,
    val onButtonColor: Color,
)

// 亮色模式的颜色方案
val LightCustomColors = CustomColors(
    primary = Color(0xFF0091EA),
    fontPrimary = Color.Black,
    fontSecondary = Color(0xFF2F2F2F),
    err = Color(0xFFDE4E4E),
    info = Color(0xff18A058),
    warn = Color(0xffF0A020),
    bgPrimary = Color.White,
    bgSecondary = Color(0xFFF5F5F5),
    greyHeavy = Color(0xFF858585),
    greyMedium = Color(0xFFBDBDBD),
    greyLight = Color(0xFFF1F2F5),
    onButtonColor = Color.White,
    bgScreen = Color(0xFFEAEAEA)
)

// 暗色模式的颜色方案
val DarkCustomColors = CustomColors(
    primary = Color(0xFF0091EA),
    fontPrimary = Color.White,
    fontSecondary = Color.White,
    err = Color(0xFFDE4E4E),
    info = Color(0xff18A058),
    warn = Color(0xffF0A020),
    bgPrimary = Color(0xFF313131),
    bgSecondary = Color(0xFF5C5C5C),
    greyHeavy = Color(0xFFC5C5C5),
    greyMedium = Color(0xFFDCDCDC),
    greyLight = Color(0xFFF3F3F3),
    onButtonColor = Color.White,
    bgScreen = Color(0xFF464646)
)