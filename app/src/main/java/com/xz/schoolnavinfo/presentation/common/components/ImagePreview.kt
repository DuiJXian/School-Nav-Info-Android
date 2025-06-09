package com.xz.schoolnavinfo.presentation.common.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun ImagePreview(
    imageList: List<String>,
    startIndex: Int = 0,
//    onBack: () -> Unit
) {
    val appColors = AppColors.current
    val pageState = rememberPagerState(initialPage = startIndex) { imageList.size }

    Box(Modifier.navigationBarsPadding()) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxSize(),
            state = pageState
        ) { index ->
            Image(
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .diskCacheKey(CachePolicy.DISABLED.name)
                        .data(imageList[index])
                        .size(500)
                        .listener(
                        )
                        .build()
                ),
                contentDescription = null
            )
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(appColors.greyMedium.copy(.5f))
                .height(30.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 10.dp),
                text = "${pageState.currentPage + 1}/${imageList.size}",
                style = TextStyle(
                    fontSize = 16.sp,
                    color = appColors.fontPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
