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
import androidx.compose.foundation.layout.width
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
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.presentation.LocalNavController
import com.xz.schoolnavinfo.presentation.MyRoutes
import com.xz.schoolnavinfo.presentation.common.components.ImageGrid
import com.xz.schoolnavinfo.presentation.common.components.LocationBox

@Composable
fun ActivityScreen(
    commonViewModel: CommonViewModel,
    activityViewModel: ActivityViewModel = hiltViewModel()
) {

    val uiState by activityViewModel.uiState.collectAsStateWithLifecycle()
    val lazyColumnState = rememberLazyListState()
    val navigator = LocalNavController.current
    ActivityContent(
        uiState = uiState,
        onRefreshData = { activityViewModel.refreshData() },
        lazyColumnState = lazyColumnState,
        onNavDetail = {
            navigator.navigate(MyRoutes.ArticleDetail(it))
        },
        onNavImageFull = { urls, startIndex ->
            navigator.navigate(
                MyRoutes.ImagePreview(
                    urls.map { url -> getImagesUrl(url) },
                    startIndex
                )
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
    val configuration = LocalConfiguration.current
    val isPad = configuration.smallestScreenWidthDp >= 600
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
                                .height(if (isPad) 300.dp else 200.dp),
                            bannerArticleDTOs = banners.reversed()
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
    bannerArticleDTOs: List<ArticleDTO>,
    modifier: Modifier = Modifier,
    autoScrollInterval: Long = 3000L,
    onImageClick: (index: Int) -> Unit = {}
) {
    val pagerState = rememberPagerState { bannerArticleDTOs.size }
    val appColor = AppColors.current
    val scope = rememberCoroutineScope()
    // 自动滚动
    LaunchedEffect(pagerState.currentPage) {
        if (bannerArticleDTOs.size <= 1) return@LaunchedEffect
        delay(autoScrollInterval)
        val nextPage = (pagerState.currentPage + 1) % bannerArticleDTOs.size
        scope.launch {
            pagerState.animateScrollToPage(nextPage, animationSpec = tween(durationMillis = 1000))
        }
    }


    val bannerUrls = bannerArticleDTOs.flatMap { e ->
        e.imageList?.filter { v -> v.contains("banner") } ?: listOf()
    }
    if (bannerUrls.isNotEmpty()) {
        Box(modifier) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) { page ->
                Image(
                    painter = rememberAsyncImagePainter(getImagesUrl(bannerUrls[page])),
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
                val article = bannerArticleDTOs[pagerState.currentPage]
                article.article?.title?.let {
                    Text(
                        text = it,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                repeat(bannerUrls.size) { index ->
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
    LaunchedEffect(Unit) {
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

@Composable
fun ActivityCard(
    modifier: Modifier = Modifier,
    articleDTO: ArticleDTO,
    onImageClick: (String) -> Unit,
    onLocation: (String) -> Unit,
) {
    val appColors = AppColors.current
    val article = articleDTO.article
    val imageList = articleDTO.imageList
    Column(
        modifier = modifier
            .background(appColors.bgPrimary)
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        //标题
        if (article?.title?.isNotBlank() == true) {
            Spacer(Modifier.height(5.dp))
            Text(
                text = article.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = appColors.fontPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

            )

        }
        //正文
        if (article?.content?.isNotBlank() == true) {
            Spacer(Modifier.height(5.dp))
            Text(
                text = article.content,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    color = appColors.fontPrimary,
                    fontSize = 16.sp,
                )
            )
        }
        //图片区域
        if (!imageList.isNullOrEmpty()) {
            Spacer(Modifier.height(3.dp))
            ImageGrid(imageList = imageList) { onImageClick(it) }
        }
        //位置区域
        if (article?.address?.isNotBlank() == true) {
            Spacer(Modifier.height(5.dp))
            LocationBox(article.address) { article.location?.let { onLocation(it) } }
        }

        Spacer(Modifier.height(5.dp))
        Row {
            Text(
                "${articleDTO.userInfo?.nickname}",
                style = TextStyle(color = appColors.greyMedium)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "${article?.createTime?.let { TimeUtils.formatTimeDifference(it) }}",
                style = TextStyle(color = appColors.greyMedium)
            )
        }
    }
}