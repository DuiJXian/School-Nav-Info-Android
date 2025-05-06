package com.xz.schoolnavinfo.presentation.common.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.xz.schoolnavinfo.common.utils.DensityUtil
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlin.math.ceil

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ImageHorizontalScroll(
    imgUrlList: List<String>,
    maxHeight: Dp = 300.dp,
    onClick: (Int) -> Unit
) {
    val scrollState = rememberScrollState()
    val appColors = AppColors.current
    val context = LocalContext.current

    val widthSizeList =
        remember { mutableStateListOf(*List(imgUrlList.size) { 1000f }.toTypedArray()) }
    val heightSizeList =
        remember { mutableStateListOf(*List(imgUrlList.size) { 1000f }.toTypedArray()) }

    var nowValue by remember { mutableIntStateOf(1) }

    var isDisplayPoint by remember { mutableStateOf(false) }


    LaunchedEffect(scrollState.value) {
        val avg = scrollState.maxValue / imgUrlList.size
        if (avg > 0) {
            nowValue = (scrollState.value / avg + 1).coerceAtMost(imgUrlList.size)
        }
    }
    LaunchedEffect(scrollState.maxValue) {
        isDisplayPoint = scrollState.maxValue > 0
    }
    var rowMaxWidth by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.bgPrimary)
            .onGloballyPositioned {
                if (it.size.width > rowMaxWidth) {
                    rowMaxWidth = it.size.width
                }
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(start = 5.dp, end = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for ((index, url) in imgUrlList.withIndex()) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .padding(top = 10.dp)
                        .height(if (heightSizeList.max() > maxHeight.value) maxHeight else heightSizeList.max().dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(widthSizeList[index].dp)
                            .height(if (heightSizeList.max() > maxHeight.value) maxHeight.value.dp else heightSizeList.max().dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(appColors.greyLight.copy(.8f))
                            .border(1.dp, appColors.greyLight.copy(.5f), RoundedCornerShape(10.dp))
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) {
                                onClick(index)
                            },
                    )
                    Image(
                        modifier = Modifier
                            .width(widthSizeList[index].dp)
                            .height(if (heightSizeList[index] > 300) 300.dp else heightSizeList[index].dp),
                        contentScale = ContentScale.Fit,
                        painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(url)
                                .listener(
                                    onSuccess = { _, result ->
                                        val drawable = result.drawable
                                        val width = ceil(
                                            DensityUtil.px2dip(
                                                context,
                                                drawable.intrinsicWidth.toFloat()
                                            ).value
                                        )
                                        val height = (DensityUtil.px2dip(
                                            context,
                                            drawable.intrinsicHeight.toFloat()
                                        ).value)
                                        widthSizeList[index] = width
                                        heightSizeList[index] = height
                                    }
                                )
                                .build()
                        ),
                        contentDescription = null
                    )
                }
            }
        }
        if (isDisplayPoint) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier
                        .padding(5.dp)
                        .clip(RoundedCornerShape(16.dp))
                    //.background(appColors.greyMedium.copy(.5f))
                ) {
                    repeat(imgUrlList.size) {
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (nowValue == it + 1) appColors.primary else appColors.greyMedium)
                        )
                    }
                }
            }
        }

    }
}