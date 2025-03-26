package com.xz.schoolnavinfo.presentation.theme
import androidx.compose.ui.graphics.Color

// 定义自定义颜色
data class CustomColors(
    val primary: Color,
    val secondary: Color,
    val fontPrimary: Color,
    val fontSecondary: Color,
    val fontAccent: Color,
    val bgPrimary: Color,
    val bgSecondary: Color,
    val unSelect: Color,
    val unSelectBorder: Color,
    val onButtonColor: Color,
)

// 亮色模式的颜色方案
val LightCustomColors = CustomColors(
    primary = Color(0xFF0091EA),
    secondary = Color(0xFF55B7F5),
    fontPrimary = Color.Black,
    fontSecondary = Color(0xFF3D3D3D),
    fontAccent = Color(0xFFDE4E4E),
    bgPrimary = Color.White,
    bgSecondary = Color(0xFFF5F5F5),
    unSelect = Color(0xFFBDBDBD),
    unSelectBorder = Color(0xFFDCDCDC),
    onButtonColor = Color.White
)

// 暗色模式的颜色方案
val DarkCustomColors = CustomColors(
    primary = Color(0xFF0091EA),
    secondary = Color(0xFF018786),
    fontPrimary = Color.White,
    fontSecondary = Color.White,
    fontAccent = Color(0xFFDE4E4E),
    bgPrimary = Color.Black,
    bgSecondary = Color(0xFF5C5C5C),
    unSelect = Color(0xFFBDBDBD),
    unSelectBorder = Color(0xFFD4D4D4),
    onButtonColor = Color.White
)