package com.xz.schoolnavinfo.presentation.campus.discuss

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xz.schoolnavinfo.common.net.getImagesUrl
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.presentation.common.components.CustomTextFiled
import com.xz.schoolnavinfo.presentation.common.components.VariableHeightTopBar
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun DiscussScreen(
    discussViewModel: DiscussViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel
) {

    val discussArticles by discussViewModel.discussArticles.collectAsStateWithLifecycle()
    val lazyListState = rememberLazyListState()
    val appColors = AppColors.current
    val barHeight = 52.dp
    var searchText by remember { mutableStateOf("") }
    VariableHeightTopBar(
        scrollableState = lazyListState,
        barHeight = barHeight,
        backgroundColor = appColors.bgPrimary,
        topBar = {
            Box(
                Modifier
                    .height(barHeight)
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                CustomTextFiled(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(shape = CircleShape)
                        .background(appColors.greyLight),
                    text = searchText,
                    leftSection = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(24.dp),
                            tint = appColors.greyMedium
                        )
                    },
                ) {
                    searchText = it
                    discussViewModel.searchDiscussArticles(it)
                }
            }
        },
    ) {
        DiscussContent(
            lazyListState = lazyListState,
            discussArticles = discussArticles,
            onDetail = {
                commonViewModel.onNavEvent(
                    NavEvent.ArticleDetail(articleDTO = it, articleType = ArticleType.Discuss)
                )
            },
            onImage = { urls, index ->
                commonViewModel.onLoadImageUrlEvent(urls, index, 0.dp)
            },
            onRoute = {
                commonViewModel.onRoutePlan(it)
            }
        )
    }
}

@Composable
private fun DiscussContent(
    lazyListState: LazyListState,
    discussArticles: List<ArticleDTO>,
    onImage: (urls: List<String>, startIndex: Int) -> Unit,
    onDetail: (ArticleDTO) -> Unit,
    onRoute: (String) -> Unit
) {
    val appColors = AppColors.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.bgScreen),
        state = lazyListState,
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 5.dp)
    ) {
        items(discussArticles) { item ->
            DiscussCard(
                Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onDetail(item) },
                articleDTO = item,
                onImageClick = {
                    if (item.imageList != null) {
                        val imageStartIndex = item.imageList.indexOf(it)
                        onImage(item.imageList.map { url -> getImagesUrl(url) }, imageStartIndex)
                    }
                },
                onLocation = { onRoute(it) }
            )
        }
    }
}
