package com.xz.schoolnavinfo.presentation.common.components

import android.content.Context
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs


@Composable
fun VariableHeightTopBar(
    scrollableState: ScrollableState,
    barHeight: Dp,
    isBounce: Boolean = true,
    topPadding: Dp = 0.dp,
    backgroundColor: Color = Color.White,
    topBar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val barHeightPx = with(LocalDensity.current) { barHeight.roundToPx().toFloat() }
    val searchHeightAnimate = remember { Animatable(0f) }
    var searchHeightScroll by remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                searchHeightScroll += -available.y
                searchHeightScroll = searchHeightScroll.coerceIn(0f, barHeightPx)
                coroutineScope.launch { searchHeightAnimate.snapTo(searchHeightScroll) }
                if (searchHeightScroll > 0 && searchHeightScroll < barHeightPx) {
                    return Offset(0f, available.y)
                }
                return Offset.Zero
            }
        }
    }

    VariableHeightTopBarContent(
        nestedScrollConnection = nestedScrollConnection,
        topBarHeight = barHeight,
        variableValue = searchHeightAnimate.value,
        backgroundColor = backgroundColor,
        topPadding = topPadding,
        topBar = topBar,
        content = content
    )

    if (isBounce) {
        LaunchedEffect(Unit) {
            snapshotFlow {
                scrollableState.isScrollInProgress
            }.collectLatest {
                if (!it && searchHeightScroll > 0 && searchHeightScroll < barHeightPx) {
                    val endValue =
                        if (searchHeightScroll > barHeightPx / 2) barHeightPx else 0f
                    searchHeightScroll = endValue
                    searchHeightAnimate.animateTo(endValue)
                }
            }
        }
    }
}

@Composable
fun VariableHeightTopBarContent(
    nestedScrollConnection: NestedScrollConnection,
    topBarHeight: Dp,
    variableValue: Float,
    backgroundColor: Color,
    topPadding: Dp = 0.dp,
    topBar: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    Column(
        Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(top = topPadding)
            .nestedScroll(nestedScrollConnection)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(topBarHeight - pxToDip(context, variableValue)),
            contentAlignment = Alignment.Center
        ) {
            topBar()
        }
        content()
    }
}

@Composable
fun MovableTopBar(
    scrollableState: ScrollableState,
    topBarBackgroundColor: Color,
    offsetBarHeight: Dp,
    topPadding: Dp = 0.dp,
    isBounce: Boolean = true,
    offsetBar: @Composable () -> Unit,
    fixedBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val offsetBarHeightPx = with(LocalDensity.current) { offsetBarHeight.roundToPx().toFloat() }
    val offsetBarAnimate = remember { Animatable(0f) }
    var scrollValue by remember { mutableFloatStateOf(0f) }
    var transParentPercent by remember { mutableFloatStateOf(1f) }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            scrollValue += -available.y
            scrollValue = scrollValue.coerceIn(0f, offsetBarHeightPx)
            coroutineScope.launch {
                offsetBarAnimate.snapTo(scrollValue)
            }
            if (scrollValue > 0 && scrollValue < offsetBarHeightPx) {
                return Offset(0f, available.y)
            }
            return Offset.Zero
        }
    }
    MovableTopBarContent(
        nestedScrollConnection = nestedScrollConnection,
        offsetY = -offsetBarAnimate.value.toInt(),
        offsetBarHeight = offsetBarHeight,
        topBarBackgroundColor = topBarBackgroundColor,
        transParentPercent = transParentPercent,
        offsetBar = offsetBar,
        fixedBar = fixedBar,
        content = content
    )

    LaunchedEffect(offsetBarAnimate.value) {
        transParentPercent = offsetBarAnimate.value / offsetBarHeightPx
    }

    if (isBounce) {
        LaunchedEffect(Unit) {
            snapshotFlow {
                scrollableState.isScrollInProgress
            }.collectLatest {
                if (!it && scrollValue > 0 && scrollValue < offsetBarHeightPx) {
                    val terminateValue = if (abs(scrollValue) > offsetBarHeightPx / 2) {
                        offsetBarHeightPx
                    } else {
                        0f
                    }
                    scrollValue = terminateValue
                    offsetBarAnimate.animateTo(terminateValue)
                }
            }
        }
    }

}

@Composable
private fun MovableTopBarContent(
    nestedScrollConnection: NestedScrollConnection,
    offsetY: Int,
    offsetBarHeight: Dp,
    topPadding: Dp = 0.dp,
    topBarBackgroundColor: Color,
    transParentPercent: Float = 1f,
    offsetBar: @Composable () -> Unit,
    fixedBar: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection)
            .offset { IntOffset(0, offsetY) }
            .background(topBarBackgroundColor)
            .padding(top = topPadding)
    ) {
        Column {
            Column(
                Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    offsetBar()
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(offsetBarHeight)
                            .background(topBarBackgroundColor.copy(transParentPercent))
                    )
                }
                fixedBar()
            }
            content()
        }
    }
}

fun pxToDip(context: Context, pxValue: Float): Dp {
    val scale = context.resources.displayMetrics.density
    return (pxValue / scale).dp
}