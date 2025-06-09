package com.xz.schoolnavinfo.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xz.schoolnavinfo.presentation.theme.AppColors

enum class ButtonType {
    PRIMARY,
    ERR,
    INFO,
    WARNING
}

@Composable
fun MyButton(
    text: String,
    type: ButtonType = ButtonType.PRIMARY,
    shape: Shape = RoundedCornerShape(6.dp),
    textStyle: TextStyle = TextStyle(color = Color.White, fontWeight = FontWeight.Bold),
    width: Dp? = null,
    height: Dp? = null,
    onClick: () -> Unit
) {
    var modifier = Modifier
        .clip(shape)
        .background(getButtonColor(type))
        .clickable {
            onClick()
        }

    modifier = if (width != null) modifier.width(width) else modifier.padding(horizontal = 12.dp)
    modifier = if (height != null) modifier.height(height) else modifier.padding(vertical = 6.dp)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = textStyle
        )
    }
}

@Composable
private fun getButtonColor(buttonType: ButtonType): Color {
    val appColors = AppColors.current
    return when (buttonType) {
        ButtonType.PRIMARY -> appColors.primary
        ButtonType.ERR -> appColors.err
        ButtonType.INFO -> appColors.info
        ButtonType.WARNING -> appColors.warn
    }
}