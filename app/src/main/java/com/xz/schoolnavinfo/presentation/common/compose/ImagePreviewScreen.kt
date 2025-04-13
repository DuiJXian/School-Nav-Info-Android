package com.xz.schoolnavinfo.presentation.common.compose


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.jvziyaoyao.scale.image.pager.ImagePager
import com.jvziyaoyao.scale.zoomable.pager.rememberZoomablePagerState
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun ImagePreviewScreen(
    imageList: List<String>,
    startIndex: Int = 0,
    displayMaxHeight: Dp = 600.dp, // 默认最大高度
//    onBack: () -> Unit
) {
    val appColors = AppColors.current
    val pageState = rememberZoomablePagerState(initialPage = startIndex) { imageList.size }
    val modifier = if (displayMaxHeight == 0.dp) {
        Modifier
            .fillMaxSize()
            .background(appColors.bgPrimary)
    } else {
        Modifier
            .heightIn(max = displayMaxHeight)
            .background(appColors.bgPrimary)
    }

    Box(
        modifier = modifier
    ) {
        ImagePager(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(10.dp)),
            pagerState = pageState,
            imageLoader = { index ->
                val painter = rememberAsyncImagePainter(imageList[index])
                return@ImagePager Pair(painter, painter.intrinsicSize)
            },
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(appColors.greyLight)
                .height(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = "${pageState.currentPage + 1}/${imageList.size}",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = appColors.fontPrimary
                )
            )
        }
    }
}
