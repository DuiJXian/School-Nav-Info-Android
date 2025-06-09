package com.xz.schoolnavinfo.presentation.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SliderButton(
    titles: List<String>,
    textStyle: TextStyle = TextStyle(),
    startIndex: Int = 0,
    padding: Dp = 2.dp,
    backgroundColors: Color = Color(0xFFF1F1F1),
    selectedColors: Color = Color.White,
    width: Dp = 300.dp,
    height: Dp = 36.dp,
    onChange: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(startIndex) }
    val sliderItemWidth = (width - padding * 2) / titles.size
    val sliderInsiderHeight = (height - padding * 2)
    val sliderItemWidthPx = with(LocalDensity.current) { sliderItemWidth.toPx() }
    val animateOffset = remember { Animatable(sliderItemWidthPx * startIndex) }

    Box(
        Modifier
            .clip(CircleShape)
            .background(backgroundColors)
            .padding(padding)
    ) {
        Box(Modifier.offset { IntOffset(x = animateOffset.value.toInt(), y = 0) }) {
            Box(
                Modifier
                    .width(sliderItemWidth)
                    .height(sliderInsiderHeight)
                    .clip(CircleShape)
                    .background(selectedColors)
            )
        }
        Row {
            titles.forEachIndexed { index, title ->
                Box(
                    Modifier
                        .width(sliderItemWidth)
                        .height(sliderInsiderHeight)
                        .clickable(interactionSource = null, indication = null) {
                            if (currentIndex != index) {
                                onChange(index)
                                coroutineScope.launch { animateOffset.animateTo(sliderItemWidthPx * index) }
                            }
                            currentIndex = index
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(title, style = textStyle)
                }
            }
        }
    }
}