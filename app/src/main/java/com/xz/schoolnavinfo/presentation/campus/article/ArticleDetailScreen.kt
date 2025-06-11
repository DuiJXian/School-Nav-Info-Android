package com.xz.schoolnavinfo.presentation.campus.article

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.net.getImagesUrl
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.data.dto.CommentDTO
import com.xz.schoolnavinfo.domain.data.entity.Article
import com.xz.schoolnavinfo.domain.data.entity.UserInfo
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.presentation.LocalNavController
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.components.ButtonType
import com.xz.schoolnavinfo.presentation.common.components.CustomTextFiled
import com.xz.schoolnavinfo.presentation.common.components.CustomTopBar
import com.xz.schoolnavinfo.presentation.common.components.ImageHorizontalScroll
import com.xz.schoolnavinfo.presentation.common.components.LocationBox
import com.xz.schoolnavinfo.presentation.common.components.MyButton
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun ArticleDetailScreen(
    articleDTO: ArticleDTO,
    articleDetailViewModel: ArticleDetailViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel,
) {

    val article = articleDTO.article
    val commentDTOS by articleDetailViewModel.commentDTOs.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    var (showDialog, updateShowDialog) = rememberSaveable { mutableStateOf(false) }
    val navigator = LocalNavController.current
    val articleType = ArticleType.getType(articleDTO.article!!.type!!)
    LaunchedEffect(Unit) {
        article?.id?.let { articleDetailViewModel.onGetComments(it) }
    }

    ArticleDetailContent(
        article = articleDTO.article,
        userInfo = articleDTO.userInfo,
        commentDTOs = commentDTOS,
        imageUrls = articleDTO.imageList?.map { getImagesUrl(it) },
        articleType = articleType,
        showDialog = showDialog,
        updateShowDialog = updateShowDialog,
        onBack = { navigator.popBack() },
        onImage = { urls, index -> },//{ urls, index -> navigator.navigate(Routes.ImagePreview(urls, index)) },
        onSend = { articleDetailViewModel.onSendComments(article?.id, it) },
        onDelete = {
            articleDetailViewModel.onDeleteArticle(it, articleType)
            navigator.popBack()
            coroutineScope.launch {
                commonViewModel.globalFlow.onRefreshDataEvent(if (articleType == ArticleType.DISCUSS) CampusMenu.Discuss else CampusMenu.Activity)
                delay(300)
                showDialog = false
            }
        }
    )
}

@Composable
fun ArticleDetailContent(
    article: Article?,
    userInfo: UserInfo?,
    commentDTOs: List<CommentDTO>,
    imageUrls: List<String>?,
    articleType: ArticleType,
    showDialog: Boolean,
    updateShowDialog: (Boolean) -> Unit,
    onBack: () -> Unit,
    onImage: (urls: List<String>, index: Int) -> Unit,
    onSend: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    val appColors = AppColors.current
    val systemPadding = WindowInsets.systemBars.asPaddingValues()

    DeleteDialog(
        isShow = showDialog,
        onUpdateShow = updateShowDialog,
        onDelete = {
            article?.id?.let {
                onDelete(it)
            }
        }
    )
    Column(
        modifier = Modifier
            .background(appColors.bgPrimary)
            .fillMaxSize()
            .padding(systemPadding)
    ) {
        ArticleTopBar(
            nickname = userInfo?.nickname,
            avatarUrl = userInfo?.avatarUrl,
            createTime = article?.createTime,
            onBack = { onBack() },
            isCanDelete = (article!!.userId == userInfo!!.id),
            showDialog = updateShowDialog
        )

        Box(Modifier.weight(1f)) {
            ArticleBody(
                modifier = Modifier,
                article = article,
                imageUrls = imageUrls,
                articleType = articleType,
                commentDTOs = commentDTOs,
                onImage = onImage
            )
            if (articleType == ArticleType.DISCUSS) {
                CommentInput(Modifier.align(Alignment.BottomCenter), onSend = onSend)
            }
        }

    }
}

@Composable
fun CommentInput(
    modifier: Modifier,
    onSend: (String) -> Unit
) {
    val systemPadding = WindowInsets.systemBars.asPaddingValues()
    val imeInsets = WindowInsets.ime
    val imeBottom = imeInsets.getBottom(LocalDensity.current)
    val offsetValue = if (imeBottom > 0) {
        (imeBottom - with(LocalDensity.current) {
            systemPadding.calculateBottomPadding().toPx()
        })
    } else 0f
    val animatedOffset by animateOffsetAsState(targetValue = Offset(0f, offsetValue))
    var commentText by remember { mutableStateOf("") }
    val appColors = AppColors.current
    Box(modifier
        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
        .background(Color.Transparent)
        .offset { IntOffset(x = 0, y = -animatedOffset.y.toInt()) }) {
        CustomTextFiled(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp)
                .clip(shape = CircleShape)
                .background(appColors.greyLight)
                .border(1.dp, appColors.primary, CircleShape),
            text = commentText,
            contentPaddingValues = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
            textColor = appColors.fontPrimary,
            brushColors = appColors.primary,
            rightSection = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(
                        Modifier
                            .width(2.dp)
                            .clip(CircleShape)
                            .height(16.dp)
                            .background(appColors.primary.copy(.5f))
                    )
                    Spacer(Modifier.width(5.dp))
                    TextButton(onClick = {
                        onSend(commentText)
                        commentText = ""
                    }) {
                        Text(
                            "发送",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = appColors.primary,
                                fontSize = 16.sp
                            )
                        )
                    }
                    Spacer(Modifier.width(5.dp))
                }
            },
            onValueChange = { commentText = it }
        )
    }
}

@Composable
fun ArticleBody(
    modifier: Modifier = Modifier,
    article: Article?,
    imageUrls: List<String>?,
    articleType: ArticleType,
    commentDTOs: List<CommentDTO>,
    onImage: (urls: List<String>, index: Int) -> Unit
) {

    val appColors = AppColors.current
    val scrollState = rememberScrollState()
    Column(
        modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
        if (!imageUrls.isNullOrEmpty()) {
            val contentImageList = imageUrls.filterNot { it.contains("banner") }
            if (contentImageList.isNotEmpty()) {
                Box(Modifier.height(320.dp)) {
                    ImageHorizontalScroll(
                        contentImageList
                    ) { index -> onImage(contentImageList, index) }
                }
            } else {
                Spacer(Modifier.height(10.dp))
            }
        }

        if (article?.title != null && article.title.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Row(Modifier.padding(horizontal = 10.dp)) {
                Text(
                    text = article.title,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = appColors.fontPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                )
            }
        }

        if (article?.content != null && article.content.isNotEmpty()) {
            Spacer(Modifier.height(5.dp))
            Row(Modifier.padding(horizontal = 10.dp)) {
                Text(
                    text = article.content,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = appColors.fontSecondary,
                        fontSize = 18.sp,
                    )

                )
            }
        }

        if (article?.address?.isNotBlank() == true) {
            Spacer(Modifier.height(5.dp))
            Box(Modifier.padding(horizontal = 10.dp)) { LocationBox(article.address) }
        }

        Spacer(
            Modifier
                .height(1.dp)
                .padding(horizontal = 10.dp)
                .background(appColors.greyMedium)
        )
        if (articleType == ArticleType.DISCUSS) {
            Row(Modifier.padding(10.dp)) {
                Text(
                    text = "共${commentDTOs.size}条评论",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.primary
                    )
                )
            }
        }
        for (commentDTO in commentDTOs.reversed()) {
            CommentCard(commentDTO)
        }
        Spacer(Modifier.height(52.dp))
    }
}

@Composable
fun ArticleTopBar(
    nickname: String?,
    avatarUrl: String?,
    createTime: String?,
    onBack: () -> Unit,
    isCanDelete: Boolean,
    showDialog: (Boolean) -> Unit
) {
    val appColors = AppColors.current
    CustomTopBar(
        leftContent = {
            Spacer(Modifier.width(10.dp))
            Image(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape),
                painter = if (avatarUrl != null)
                    rememberAsyncImagePainter(getImagesUrl(avatarUrl)) else
                    painterResource(R.drawable.heard_image),
                contentDescription = "头像",
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(start = 10.dp)) {
                Text(
                    text = nickname ?: "",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = appColors.fontPrimary,
                    )
                )
                Text(
                    text = if (createTime != null)
                        TimeUtils.formatTimeDifference(createTime.replace("+", " ")) else
                        "err",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = appColors.greyMedium,
                    )
                )
            }
        },
        rightContent = {
            if (isCanDelete) MyButton("删除", ButtonType.ERR) { showDialog(true) }
        },
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeleteDialog(
    isShow: Boolean,
    onUpdateShow: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val appColors = AppColors.current
    if (isShow) {
        BasicAlertDialog(onDismissRequest = {}) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .width(280.dp)
                ) {
                    Text(
                        text = "提醒",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = appColors.primary
                        )
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "确认删除",
                        style = TextStyle(
                            fontSize = 16.sp,
                        )

                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onUpdateShow(false) }) {
                            Text(
                                text = "取消",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                )
                            )
                        }
                        TextButton(onClick = { onDelete() }) {
                            Text(
                                text = "确定",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CommentCard(
    commentDTO: CommentDTO
) {
    val appColor = AppColors.current
    val userInfo = commentDTO.userInfo
    val comment = commentDTO.comment
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(appColor.bgPrimary)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(40.dp)
                    .clip(CircleShape),
                painter = if (userInfo.avatarUrl.isBlank())
                    painterResource(R.drawable.heard_image) else
                    rememberAsyncImagePainter(getImagesUrl(userInfo.avatarUrl)),
                contentScale = ContentScale.Crop,
                contentDescription = "头像",
            )

            Column(
                modifier = Modifier
                    .padding(start = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = userInfo.nickname,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = appColor.fontSecondary
                    )
                )
                comment.createTime?.let {
                    Text(
                        text = TimeUtils.formatTimeDifference(comment.createTime),
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = appColor.greyMedium
                        )
                    )
                }
            }
        }
        Row {
            Spacer(
                modifier = Modifier
                    .width(50.dp)
            )
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp),
                text = comment.content,
                style = TextStyle(
                    color = appColor.fontPrimary,
                    fontSize = 16.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}