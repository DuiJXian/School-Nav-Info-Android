package com.xz.schoolnavinfo.presentation.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch

@Composable
fun Modifier.shake(): Modifier {
    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    return this
        .offset { IntOffset(x = offsetX.value.toInt(), y = 0) }
        .clickable(interactionSource = null, indication = null) {
            coroutineScope.launch {
                repeat(3) {
                    offsetX.animateTo(-10f, animationSpec = tween(50))
                    offsetX.animateTo(10f, animationSpec = tween(50))
                }
                offsetX.animateTo(0f, animationSpec = tween(50))
            }
        }

}