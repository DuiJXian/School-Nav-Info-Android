package com.xz.schoolnavinfo.presentation.campus.discuss

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xz.schoolnavinfo.common.net.BASE_URL
import com.xz.schoolnavinfo.common.net.getStaticCompleteUrl
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.roundToInt

@Composable
fun DiscussScreen(
    discussViewModel: DiscussViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel
) {

    val articleList = discussViewModel.discussList
    var searchText by remember { mutableStateOf("") }
    val lazyListState = rememberLazyListState()

    val appColors = AppColors.current

    val searchHeight = 66.dp
    val searchHeightPx = with(LocalDensity.current) { searchHeight.roundToPx().toFloat() }
    var searchHeightOffset by remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                searchHeightOffset += delta
                searchHeightOffset = searchHeightOffset.coerceIn(-searchHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    LaunchedEffect(true) {
        commonViewModel.globalFlow.refreshData.collectLatest {
            if (it == ArticleType.Discuss) {
                discussViewModel.onGetArticlesEvent()
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
            if (lastVisibleItem == totalItems - 1 && discussViewModel.hasMore) {
                discussViewModel.onGetMoreArticlesEvent()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection) // 在 Column 上统一使用 nestedScroll
    ) {

        // 使用 LazyColumn 显示文章
        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(
                top = searchHeight + 10.dp,
                start = 10.dp,
                end = 10.dp
            )
        ) {
            items(articleList) { item ->
                Box(
                    modifier = Modifier
                        .clickable {
                            commonViewModel.onNavEvent(
                                NavEvent.ArticleDetail(
                                    articleDTO = item,
                                    type = "讨论"
                                )
                            )
                        }
                        .padding(bottom = 10.dp)
                ) {
                    DiscussCard(
                        articleDTO = item
                    ) {
                        if (item.imageList != null) {
                            val startIndex = item.imageList.indexOf(it)
                            commonViewModel.onLoadImageUrlEvent(
                                item.imageList.map { url -> getStaticCompleteUrl(url) },
                                startIndex,
                                0.dp
                            )
                        }
                    }
                }
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .offset {
                    IntOffset(x = 0, y = searchHeightOffset.roundToInt())
                }

        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(searchHeight)
                    .background(appColors.bgPrimary)
                    .padding(horizontal = 10.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .height(48.dp)
                        .fillMaxWidth(),
                    value = searchText,
                    onValueChange = { searchText = it },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon",
                            tint = appColors.fontPrimary
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = appColors.bgPrimary,
                        focusedIndicatorColor = appColors.primary,
                        focusedTextColor = appColors.fontPrimary,

                        unfocusedContainerColor = appColors.bgPrimary,
                        unfocusedIndicatorColor = appColors.greyLight,
                        unfocusedTextColor = appColors.fontSecondary,

                        cursorColor = appColors.primary
                    ),
                    shape = RoundedCornerShape(16.dp),
                    trailingIcon = {
                        if (searchText.isNotEmpty()) {
                            IconButton(onClick = { searchText = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear Icon",
                                    tint = appColors.fontPrimary
                                )
                            }
                        }
                    },
                    singleLine = true
                )
            }
        }
    }
}
