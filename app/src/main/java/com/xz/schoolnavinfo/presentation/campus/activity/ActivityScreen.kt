package com.xz.schoolnavinfo.presentation.campus.activity

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.common.net.montageCompleteUrl
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.campus.CampusScreen
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ActivityScreen(
    commonViewModel: CommonViewModel,
    activityViewModel: ActivityViewModel = hiltViewModel()
) {

    val appColors = AppColors.current
    val scrollState = rememberScrollState()

    val articleList = activityViewModel.activityList
    val bannerList = activityViewModel.bannerList
    val lazyListState = rememberLazyListState()


    LaunchedEffect(true) {
        commonViewModel.globalFlow.refreshDataFlow.collectLatest {
            if (it == CampusMenu.Activity) {
                activityViewModel.onGetActivityEvent()
                activityViewModel.onGetBannerEvent()
            }
        }
    }

    LaunchedEffect(lazyListState) {
        snapshotFlow {
            val layoutInfo = lazyListState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = layoutInfo.totalItemsCount
            lastVisibleItem to totalItems
        }.collect { (lastVisibleItem, totalItems) ->
            if (lastVisibleItem == totalItems - 1 && activityViewModel.hasMore) {
                activityViewModel.onGetMoreActivityEvent()
            }
        }
    }


    // 使用 LazyColumn 显示文章
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(
            top = 10.dp,
            start = 10.dp,
            end = 10.dp
        )
    ) {
        if (bannerList.isNotEmpty()) {
            item {
                CarouselBanner(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .height(300.dp),
                    articleDTOList = bannerList.reversed()
                )
            }

        }

        items(articleList) { item ->
            Box(
                modifier = Modifier
                    .clickable {
                        commonViewModel.onNavEvent(
                            NavEvent.ArticleDetail(
                                articleDTO = item,
                                campusMenu = CampusMenu.Activity
                            )
                        )
                    }
                    .padding(bottom = 10.dp)
            ) {
                ActivityCard(
                    articleDTO = item
                ) {
                    if (item.imageList != null) {
                        val startIndex = item.imageList.indexOf(it)
                        commonViewModel.onLoadImageUrlEvent(
                            item.imageList.map { url -> montageCompleteUrl(url) },
                            startIndex,
                            0.dp
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun CarouselBanner(
    articleDTOList: List<ArticleDTO>,
    modifier: Modifier = Modifier,
    autoScrollInterval: Long = 3000L, // 自动滚动间隔
    onImageClick: (index: Int) -> Unit = {}
) {
    val pagerState = rememberPagerState { articleDTOList.size }
    val appColor = AppColors.current
    val scope = rememberCoroutineScope()

    // 自动滚动
    LaunchedEffect(pagerState.currentPage) {
        if (articleDTOList.size <= 1) return@LaunchedEffect
        delay(autoScrollInterval)
        val nextPage = (pagerState.currentPage + 1) % articleDTOList.size
        scope.launch {
            pagerState.animateScrollToPage(nextPage, animationSpec = tween(durationMillis = 1000))
        }
    }


    val imageList = articleDTOList.flatMap { e ->
        e.imageList?.filter { v -> v.contains("banner") }
            ?: listOf()
    }
    if (imageList.isNotEmpty()) {
        Box(
            modifier = modifier
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) { page ->
                Image(
                    painter = rememberAsyncImagePainter(montageCompleteUrl(imageList[page])),
                    contentDescription = "banner-$page",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onImageClick(page) }
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.3f)
                            ), // 渐变从透明到半透明黑色
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .padding(bottom = 10.dp, start = 10.dp)
                    .align(Alignment.BottomStart)
            ) {
                val article = articleDTOList[pagerState.currentPage]
                article.article?.title?.let {
                    Text(
                        text = it,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
            // 小圆点指示器
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(bottom = 10.dp, end = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.BottomEnd)
                    .background(Color.Gray.copy(alpha = .5f))
            ) {
                repeat(imageList.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) Color.White else appColor.greyHeavy)
                    )
                }
            }
        }
    }
}