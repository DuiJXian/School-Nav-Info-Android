package com.xz.schoolnavinfo.presentation.common.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun CustomTextFiled(
    modifier: Modifier = Modifier,
    text: String,
    isPassword: Boolean = false,
    hintText: String = "",
    hintTextColor: Color = Color.Gray,
    textColor: Color = Color.Black,
    textSize: TextUnit = 16.sp,
    brushColors: Color = Color.Black,
    contentPaddingValues: PaddingValues = PaddingValues(horizontal = 5.dp, vertical = 0.dp),
    leftSection: @Composable () -> Unit = {},
    rightSection: @Composable () -> Unit = {},
    onFocusChange: (Boolean) -> Unit = {},
    onValueChange: (text: String) -> Unit = {},
) {
    var isFocus by remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leftSection()
        BasicTextField(
            value = text,
            onValueChange = {
                onValueChange(it)
            },
            modifier = Modifier
                .weight(1f)
                .padding(contentPaddingValues)
                .onFocusChanged {
                    isFocus = it.isFocused
                    onFocusChange(it.isFocused)
                },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            textStyle = TextStyle(
                fontSize = textSize,
                color = textColor
            ),
            decorationBox = { innerTextField ->
                if (text.isEmpty() && hintText.isNotEmpty() && !isFocus) {
                    Text(hintText, color = hintTextColor)
                }
                innerTextField()
            },
            cursorBrush = SolidColor(brushColors),
            singleLine = true
        )
        rightSection()
    }
}