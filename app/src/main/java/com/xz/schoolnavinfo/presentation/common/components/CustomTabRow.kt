package com.xz.schoolnavinfo.presentation.common.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xz.schoolnavinfo.common.utils.DensityUtil
import kotlinx.coroutines.launch

@Composable
fun CustomTabRow(
    tabWidth: Dp,
    startPage: Int = 0,
    pagerState: PagerState? = null,
    backgroundColor: Color = Color.White,
    tabs: List<@Composable () -> Unit>,
    indicator: @Composable () -> Unit,
    alignment: Alignment = Alignment.Center,
    onPageChange: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    val tabWidthPx = with(LocalDensity.current) { tabWidth.toPx() }
    var tabRowWidthPx by remember { mutableFloatStateOf(0f) }
    val animateWidth = remember { Animatable(startPage * tabWidthPx) }

    Box(
        Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .onGloballyPositioned {
                tabRowWidthPx = it.size.width.toFloat()
            }, contentAlignment = alignment
    ) {
        Column(Modifier.horizontalScroll(scrollState)) {
            Row {
                tabs.forEachIndexed { index, tab ->
                    Box(
                        Modifier
                            .padding(top = 5.dp)
                            .width(tabWidth)
                            .clickable {
                                if (animateWidth.value != index * tabWidth.value) coroutineScope.launch {
                                    pagerState?.animateScrollToPage(index)
                                    animateWidth.animateTo(index * tabWidthPx)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        tab()
                    }
                }
            }
            Row(Modifier.width(tabWidth * tabs.size)) {
                Box(
                    Modifier
                        .padding(start = DensityUtil.pxToDip(context, animateWidth.value))
                        .width(tabWidth), contentAlignment = Alignment.Center
                ) { indicator() }
            }
        }
    }

    LaunchedEffect(Unit) {
        pagerState?.scrollToPage(startPage)
    }
    pagerState?.let {
        LaunchedEffect(it.currentPage, it.currentPageOffsetFraction) {
            val indicatorLeftPadding = (it.currentPage + it.currentPageOffsetFraction) * tabWidthPx

            onPageChange(it.currentPage)
            animateWidth.snapTo(indicatorLeftPadding)
            scrollState.scrollTo(
                centerScrollPosition(indicatorLeftPadding, tabRowWidthPx, tabWidthPx)
            )
        }
    }
}

fun centerScrollPosition(currentPx: Float, containerWidthPx: Float, itemWidthPx: Float): Int {
    return (currentPx - (containerWidthPx - itemWidthPx) / 2f).toInt()
}