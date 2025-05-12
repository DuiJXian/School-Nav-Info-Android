package com.xz.schoolnavinfo.presentation.campus.activity

import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.common.net.getImagesUrl
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

@Composable
fun ActivityScreen(
    commonViewModel: CommonViewModel,
    activityViewModel: ActivityViewModel = hiltViewModel()
) {

    val uiState by activityViewModel.uiState.collectAsStateWithLifecycle()
    val lazyColumnState = rememberLazyListState()

    ActivityContent(
        uiState = uiState,
        onRefreshData = { activityViewModel.refreshData() },
        lazyColumnState = lazyColumnState,
        onNavDetail = {
            commonViewModel.onNavEvent(
                NavEvent.ArticleDetail(
                    articleDTO = it,
                    articleType = ArticleType.Activity
                )
            )
        },
        onNavImageFull = { urls, startIndex ->
            commonViewModel.onLoadImageUrlEvent(
                urls.map { url -> getImagesUrl(url) },
                startIndex,
                0.dp
            )
        },
        onLocation = {
            commonViewModel.onRoutePlan(it)
        }
    )

    RunCoroutine(
        refreshFlow = commonViewModel.globalFlow.refreshDataFlow,
        refreshData = { activityViewModel.refreshData() },
        lazyColumnState = lazyColumnState,
        getMoreArticles = { activityViewModel.getMoreActivityArticles() }
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActivityContent(
    uiState: ActivityUiState,
    lazyColumnState: LazyListState,
    onRefreshData: () -> Unit,
    onNavDetail: (ArticleDTO) -> Unit,
    onNavImageFull: (List<String>, Int) -> Unit,
    onLocation: (String) -> Unit
) {
    val articles = uiState.activities
    val banners = uiState.banners
    val refreshState = rememberPullToRefreshState()
    val appColors = AppColors.current
    PullToRefreshBox(
        state = refreshState,
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefreshData,
        indicator = {
            Indicator(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(),
                isRefreshing = uiState.isRefreshing,
                color = appColors.primary,
                state = refreshState
            )
        },
        content = {
            LazyColumn(
                Modifier
                    .fillMaxSize(),
                state = lazyColumnState,
                contentPadding = PaddingValues(
                    top = 10.dp,
                    start = 10.dp,
                    end = 10.dp,
                    bottom = 5.dp
                )
            ) {
                if (banners.isNotEmpty()) {
                    item {
                        ActivityBanner(
                            modifier = Modifier
                                .padding(bottom = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .height(300.dp),
                            articleDTOList = banners.reversed()
                        ) { index ->
                            onNavDetail(banners.reversed()[index])
                        }
                    }

                }

                items(articles) { article ->
                    Column {
                        ActivityCard(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { onNavDetail(article) },
                            articleDTO = article,
                            onImageClick = {
                                if (article.imageList != null) {
                                    val startIndex = article.imageList.indexOf(it)
                                    onNavImageFull(
                                        article.imageList.map { url -> getImagesUrl(url) },
                                        startIndex
                                    )
                                }
                            },
                            onLocation = { onLocation(it) },
                        )
                        Spacer(Modifier.height(5.dp))
                    }
                }
            }
        }
    )
}

@Composable
private fun ActivityBanner(
    articleDTOList: List<ArticleDTO>,
    modifier: Modifier = Modifier,
    autoScrollInterval: Long = 3000L,
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
        e.imageList?.filter { v -> v.contains("banner") } ?: listOf()
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
                    painter = rememberAsyncImagePainter(getImagesUrl(imageList[page])),
                    contentDescription = "banner-$page",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onImageClick(page) }
                )
            }
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Gray.copy(alpha = 0.5f)
                            ),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
            Box(
                Modifier
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
            Row(
                Modifier
                    .padding(bottom = 10.dp, end = 10.dp)
                    .align(Alignment.BottomEnd),
                horizontalArrangement = Arrangement.Center,
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

@Composable
private fun RunCoroutine(
    refreshFlow: SharedFlow<CampusMenu>,
    refreshData: () -> Unit,
    lazyColumnState: LazyListState,
    getMoreArticles: () -> Unit,
) {
    LaunchedEffect(true) {
        refreshFlow.collectLatest {
            if (it == CampusMenu.Activity) {
                refreshData()
            }
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            val layoutInfo = lazyColumnState.layoutInfo
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = layoutInfo.totalItemsCount
            lastVisibleItem to totalItems
        }.collectLatest { (lastVisibleItem, totalItems) ->
            if (lastVisibleItem == totalItems - 1) {
                getMoreArticles()
            }
        }
    }
}