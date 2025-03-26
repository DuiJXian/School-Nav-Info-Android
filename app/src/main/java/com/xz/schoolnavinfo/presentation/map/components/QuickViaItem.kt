package com.xz.schoolnavinfo.presentation.map.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


@Composable
@Preview(showBackground = true)
fun QuickViaItem(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val bottomBoxHeightDp = 86.dp
    val bottomBoxHeightPx = with(LocalDensity.current) { bottomBoxHeightDp.toPx() }

    val menuPosition = remember { Animatable(0f) }
    val decay = rememberSplineBasedDecay<Float>()
    Column(
        modifier
            .offset {
                IntOffset(0, menuPosition.value.roundToInt())
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState {
                        coroutineScope.launch {
                            val offsetY = (menuPosition.value + it).coerceIn(0f, bottomBoxHeightPx)
                            menuPosition.snapTo(offsetY)
                        }
                    },
                    onDragStopped = { velocity ->
                        coroutineScope.launch {
                            val targetPosition =
                                decay.calculateTargetValue(menuPosition.value, velocity)
                            val finalPosition = if (targetPosition < bottomBoxHeightPx / 2) {
                                0f // Fully visible
                            } else {
                                bottomBoxHeightPx // Fully hidden
                            }
                            menuPosition.animateTo(finalPosition)
                        }
                    }
                )
        ) {
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .width(88.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xC3D5D5D5))
            )
        }
        Box(
            modifier = Modifier
                .draggable(
                    orientation = Orientation.Vertical,
                    state = rememberDraggableState {
                        coroutineScope.launch {
                            val offsetY = (menuPosition.value + it).coerceIn(0f, bottomBoxHeightPx)
                            menuPosition.snapTo(offsetY)
                        }
                    },
                    onDragStopped = { velocity ->
                        coroutineScope.launch {
                            val targetPosition =
                                decay.calculateTargetValue(menuPosition.value, velocity)
                            val finalPosition = if (targetPosition < bottomBoxHeightPx / 2) {
                                0f // Fully visible
                            } else {
                                bottomBoxHeightPx // Fully hidden
                            }
                            menuPosition.animateTo(finalPosition)
                        }
                    }
                )
                .shadow(
                    10.dp,
                    RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                )

        ) {
            BottomMenu()
        }
    }
}

@Composable
fun BottomMenu(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .height(86.dp)
            .background(Color.White)
            .padding(10.dp)
            .fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Cyan)
                    .height(50.dp)
                    .width(50.dp)
            ) {
                Text("食", modifier = Modifier.align(Alignment.Center))
            }
            Box(modifier = Modifier.height(16.dp)) {
                Text("食堂", style = TextStyle(fontSize = 10.sp))
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(start = 10.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Color.Cyan)
                    .height(50.dp)
                    .width(50.dp)
            ) {
                Text("菜", modifier = Modifier.align(Alignment.Center))
            }
            Box(modifier = Modifier.height(16.dp)) {
                Text("菜鸟驿站", style = TextStyle(fontSize = 10.sp))
            }
        }

    }
}