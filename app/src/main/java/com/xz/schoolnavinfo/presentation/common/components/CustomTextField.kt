package com.xz.schoolnavinfo.presentation.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun CustomTextFiled(
    modifier: Modifier = Modifier,
    borderColor: Color,
    containerColor: Color,
    onFocusContainerColor: Color,
    defaultText: String = "",
    alpha: Float = 0.5f,
    icon: ImageVector,
    borderSize: Dp = 1.dp,
    shape: RoundedCornerShape = RoundedCornerShape(10.dp),
    height: Dp = 50.dp,
    width: Dp = 210.dp,
    onValueChange: (text: String) -> Unit
){
    val appColors = AppColors.current
    var text by remember { mutableStateOf(defaultText) }
    var isFocused by remember { mutableStateOf(false) }
    var mBorderColor by remember { mutableStateOf(borderColor.copy(alpha = alpha)) }
    var mContainerColor by remember { mutableStateOf(onFocusContainerColor) }
    val focusRequester = remember { FocusRequester() }
    var mTextColor by remember { mutableStateOf(Color.Black.copy(alpha)) }



    if (isFocused){
        mBorderColor = borderColor
        mContainerColor = onFocusContainerColor
        mTextColor = Color.Black
    }else{
        mBorderColor = borderColor.copy(alpha = alpha)
        mContainerColor = containerColor
        mTextColor = Color.Black.copy(alpha)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(mContainerColor, shape)
            .border(borderSize, mBorderColor, shape = shape)
            .height(height)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 10.dp)
                .size(20.dp),
            tint = appColors.greyMedium
        )
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onValueChange(it)
            },
            modifier = Modifier
                .width(width)
                .padding(start = 5.dp, end = 16.dp)
                .focusRequester(focusRequester)
                .onFocusChanged {
                    isFocused = it.isFocused
                },
            textStyle = TextStyle(
                fontSize = 18.sp,
                color = mTextColor
            ),
            cursorBrush = SolidColor(appColors.primary),
            singleLine = true
        )
    }
}