package com.xz.schoolnavinfo.presentation.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

data class TitleIcon(
    val title: String,
    val icon: Int
)

@Composable
fun BottomNavigationBar(
    menus: List<TitleIcon>,
    startIndex: Int = 0,
    height: Dp = 56.dp,
    iconSize: Dp = 24.dp,
    fontScale: Float = 0.8f,
    fontSize: Float = 14f,
    dividerColor: Color = Color(0xFFECECEC),
    backgroundColor: Color = Color.White,
    selectedColor: Color = Color.Blue,
    unSelectedColor: Color = Color.Gray,
    circleColorAlpha: Float = 0.3f,
    onIndexChange: (Int) -> Unit = {}
) {
    val (lastIndex, setLastIndex) = rememberSaveable { mutableIntStateOf(startIndex) }
    val coroutineScope = rememberCoroutineScope()
    val circleSizes = remember {
        List(menus.size) { Animatable(if (it == startIndex) iconSize.value * 2 else 0f) }
    }
    val iconScales = remember {
        List(menus.size) { Animatable(if (it == startIndex) 1f else fontScale) }
    }

    Column(Modifier.height(height)) {
        Spacer(
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(dividerColor)
        )
        Row(
            Modifier
                .fillMaxHeight()
                .background(backgroundColor),
            verticalAlignment = Alignment.CenterVertically
        ) {
            menus.forEachIndexed { index, menu ->
                val interactionSource = remember { MutableInteractionSource() }
                val color = if (index == lastIndex) selectedColor else unSelectedColor
                Column(
                    Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) { if (index != lastIndex) onIndexChange(index) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Box(
                        Modifier.height(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            Modifier
                                .height(circleSizes[index].value.dp / 2)
                                .width(circleSizes[index].value.dp)
                                .clip(CircleShape)
                                .background(selectedColor.copy(circleColorAlpha))
                        )
                        Image(
                            modifier = Modifier
                                .size(iconSize * iconScales[index].value),
                            painter = painterResource(menu.icon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(color)
                        )
                    }
                    Text(
                        menu.title,
                        style = TextStyle(color = color, fontSize = fontSize.sp)
                    )
                }

            }
        }
    }

    LaunchedEffect(startIndex) {
        coroutineScope.launch { circleSizes[lastIndex].animateTo(0f) }
        coroutineScope.launch { circleSizes[startIndex].animateTo(iconSize.value * 2) }
        coroutineScope.launch { iconScales[lastIndex].animateTo(0.8f) }
        coroutineScope.launch { iconScales[startIndex].animateTo(1f) }
        setLastIndex(startIndex)
    }
}