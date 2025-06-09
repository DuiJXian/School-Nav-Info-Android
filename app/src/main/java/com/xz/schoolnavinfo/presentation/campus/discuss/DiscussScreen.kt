package com.xz.schoolnavinfo.presentation.campus.discuss

import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.net.getImagesUrl
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.presentation.LocalAppNavigator
import com.xz.schoolnavinfo.presentation.Routes
import com.xz.schoolnavinfo.presentation.TestActivity
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.components.CustomTextFiled
import com.xz.schoolnavinfo.presentation.common.components.ChangeHeightBar
import com.xz.schoolnavinfo.presentation.common.components.ImageGrid
import com.xz.schoolnavinfo.presentation.common.components.LocationBox
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.flow.collectLatest

@Composable
fun DiscussScreen(
    discussViewModel: DiscussViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel
) {

    val discussArticles by discussViewModel.discussArticles.collectAsStateWithLifecycle()
    val navigator = LocalAppNavigator.current
    val lazyListState = rememberLazyListState()
    val appColors = AppColors.current
    val barHeight = 52.dp
    var searchText by remember { mutableStateOf("") }
    ChangeHeightBar(
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
                        .background(appColors.bgLight),
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
                navigator.navigate(Routes.ArticleDetail(it))
            },
            onImage = { urls, index ->
                navigator.navigate(Routes.ImagePreview(urls = urls, startIndex = index))
            },
            onRoute = {
                commonViewModel.onRoutePlan(it)
            }
        )
    }

    LaunchedEffect(Unit) {
        commonViewModel.globalFlow.refreshDataFlow.collectLatest {
            if (it == CampusMenu.Discuss) {
                discussViewModel.refreshData()
            }
        }
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
    val context = LocalContext.current
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
                    .clickable {
//                        val intent = Intent(context, TestActivity::class.java)
//                        context.startActivity(intent)
                        onDetail(item)
                    },
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

@Composable
fun DiscussCard(
    modifier: Modifier = Modifier,
    articleDTO: ArticleDTO,
    onImageClick: (String) -> Unit,
    onLocation: (String) -> Unit,
) {
    val appColors = AppColors.current
    val article = articleDTO.article
    val userInfo = articleDTO.userInfo
    val imageList = articleDTO.imageList
    Column {
        Column(
            modifier = modifier
                .background(appColors.bgPrimary)
                .padding(10.dp)
                .fillMaxWidth(),
        ) {
            //用户信息区域
            Row {
                Image(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape),
                    painter = if (userInfo?.avatarUrl?.isNotBlank() == true)
                        rememberAsyncImagePainter(getImagesUrl(userInfo.avatarUrl)) else
                        painterResource(R.drawable.heard_image),
                    contentDescription = "头像",
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(36.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = userInfo?.nickname ?: "",
                        style = TextStyle(
                            color = appColors.fontPrimary,
                            fontSize = 16.sp
                        )
                    )
                    article?.createTime?.let {
                        Text(
                            text = TimeUtils.formatTimeDifference(article.createTime),
                            style = TextStyle(
                                color = appColors.greyMedium
                            )
                        )
                    }
                }
            }
            Spacer(Modifier.height(5.dp))
            //标题区域
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
            //内容区域
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
        }
        Spacer(Modifier.height(5.dp))
    }
}
