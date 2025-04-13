package com.xz.schoolnavinfo.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.xz.schoolnavinfo.R
import kotlin.math.roundToInt

@Composable
fun TestScreen() {
    val toolbarHeight = 200.dp
    val maxUpPx = with(LocalDensity.current) {
        toolbarHeight.roundToPx().toFloat() - 56.dp.roundToPx().toFloat()
    }
    var toolbarOffsetHeightPx by remember { mutableStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                toolbarOffsetHeightPx += delta
                toolbarOffsetHeightPx = toolbarOffsetHeightPx.coerceIn(-maxUpPx, 0f)
                return Offset.Zero
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection) // 作为父级附加到嵌套滚动系统
    ) {

        Log.e("TAG", "TestScreen: $toolbarHeight", )
        // 列表带有内置的嵌套滚动支持，它将通知我们它的滚动
        LazyColumn(contentPadding = PaddingValues(top = toolbarHeight)) {
            items(100) { index ->
                Text(
                    "I'm item $index", modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
        ScrollableAppBar(
            scrollableAppBarHeight = toolbarHeight,
            toolbarOffsetHeightPx = toolbarOffsetHeightPx
        )
    }
}

@Composable
fun ScrollableAppBar(
    scrollableAppBarHeight: Dp,
    toolbarOffsetHeightPx: Float //向上偏移量
) {
    Box(modifier = Modifier
        .height(scrollableAppBarHeight)
        .offset {
            IntOffset(x = 0, y = toolbarOffsetHeightPx.roundToInt()) //设置偏移量
        }
        .fillMaxWidth()
    ) {
        Image(
            modifier = Modifier,
            painter = painterResource(id = R.drawable.lunbo1),
            contentDescription = "background",
            contentScale = ContentScale.FillBounds
        )

    }
}

@Composable
@Preview
fun text(){
    var checked by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it }
        )
        Text(text = "我已阅读并同意协议")
    }
}