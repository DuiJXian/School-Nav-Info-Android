package com.xz.schoolnavinfo.presentation.campus.detail

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.net.montageCompleteUrl
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.domain.data.type.RoleType
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.compose.ImageHorizontalScroll
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun ArticleDetailScreen(
    articleDTO: ArticleDTO,
    articleType: ArticleType,
    articleDetailViewModel: ArticleDetailViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel,
) {

    val imageList = articleDTO.imageList?.map { montageCompleteUrl(it) }
    val article = articleDTO.article
    val userInfo = articleDTO.userInfo

    val appColors = AppColors.current
    val scrollState = rememberScrollState()

    var commentText by remember { mutableStateOf("") }

    val imeInsets = WindowInsets.ime
    val imeBottom = imeInsets.getBottom(LocalDensity.current)

    val commentDTOList by articleDetailViewModel.commentDTOList.collectAsState()

    val animatedOffset by animateDpAsState(targetValue = with(LocalDensity.current) {
        if (imeBottom.toDp() > 10.dp) imeBottom.toDp() - 10.dp else imeBottom.toDp()
    })

    var isFocused by remember { mutableStateOf(false) }
    val systemPadding = WindowInsets.systemBars

    var showDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    LaunchedEffect(true) {
        article?.id?.let { articleDetailViewModel.onGetComments(it) }
    }


    if (showDialog) {
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
                        TextButton(onClick = {
                            showDialog = false
                        }) {
                            Text(
                                text = "取消",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                )
                            )
                        }
                        TextButton(onClick = {
                            article?.id?.let {
                                articleDetailViewModel.onDeleteArticle(it, articleType)
                                commonViewModel.onNavEvent(NavEvent.BackPage)
                                scope.launch {
                                    commonViewModel.globalFlow.onRefreshDataEvent(if (articleType == ArticleType.Discuss) CampusMenu.Discuss else CampusMenu.Activity)
                                    delay(300)
                                    showDialog = false
                                }
                            }
                        }) {
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

    Box(
        modifier = Modifier
            .background(appColors.bgPrimary)
            .padding(systemPadding.asPaddingValues())
            .offset(x = 0.dp, y = -animatedOffset)
    ) {

        //顶部
        Row(
            modifier = Modifier
                .height(46.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            commonViewModel.onNavEvent(NavEvent.BackPage)
                        },
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "返回",
                    tint = appColors.fontSecondary
                )
                if (articleType == ArticleType.Discuss) {
                    Image(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(40.dp)
                            .clip(CircleShape),
                        painter = if (userInfo?.avatarUrl != null)
                            rememberAsyncImagePainter(montageCompleteUrl(userInfo.avatarUrl)) else
                            painterResource(R.drawable.heard_image),
                        contentDescription = "头像",
                        contentScale = ContentScale.Crop
                    )
                    Column {
                        Text(
                            modifier = Modifier
                                .padding(start = 5.dp),
                            text = userInfo?.nickname ?: "",
                            style = TextStyle(
                                fontSize = 16.sp,
                                color = appColors.fontPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 5.dp),
                            text = if (article?.createTime != null)
                                TimeUtils.formatTimeDifference(article.createTime) else
                                "err",
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = appColors.greyMedium,
                            )
                        )
                    }
                }
            }


            if ((article!!.userId == userInfo!!.id) && (articleType == ArticleType.Activity && userInfo.role == RoleType.ADMIN.name))
                Row(Modifier.padding(end = 10.dp)) {
                    Box(
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .shadow(3.dp, RoundedCornerShape(10.dp))
                            .background(appColors.err)
                            .clip(RoundedCornerShape(10.dp))
                            .width(60.dp)
                            .height(30.dp)
                            .clickable {
                                showDialog = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "删除",
                            style = TextStyle(
                                color = appColors.onButtonColor,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
        }


        Column(
            modifier = Modifier
                .padding(top = 46.dp, bottom = 56.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            //图片区域
            Row {
                val contentImageList =
                    if (!imageList.isNullOrEmpty()) imageList.filterNot { it.contains("banner") } else emptyList()
                if (contentImageList.isNotEmpty()) {
                    ImageHorizontalScroll(
                        contentImageList
                    ) { index ->
                        commonViewModel.onLoadImageUrlEvent(
                            contentImageList,
                            index,
                            0.dp
                        )
                    }
                }
            }

            if (article?.title != null) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                ) {
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

            if (article?.content != null) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 10.dp, top = 10.dp)
                ) {
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


            Spacer(
                Modifier
                    .height(1.dp)
                    .background(appColors.greyMedium)
            )


            if (articleType != ArticleType.Activity && commentDTOList.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 10.dp, bottom = 10.dp)
                ) {
                    Text(
                        text = "共${commentDTOList.size}条评论",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = appColors.primary
                        )
                    )
                }
            }



            for (commentDTO in commentDTOList.reversed()) {
                CommentCard(commentDTO)
            }


        }


        if (articleType != ArticleType.Activity) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 10.dp, end = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(appColors.bgPrimary)
                        .height(48.dp)
                        .border(
                            1.dp,
                            if (isFocused) appColors.primary else appColors.greyMedium.copy(.5f),
                            RoundedCornerShape(16.dp)
                        )
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BasicTextField(
                        modifier = Modifier
                            .padding(horizontal = 15.dp)
                            .weight(1f)
                            .onFocusChanged { focusState ->
                                isFocused = focusState.isFocused
                            }
                            .fillMaxWidth(),
                        value = commentText,
                        textStyle = TextStyle(
                            fontSize = 18.sp
                        ),
                        onValueChange = {
                            commentText = it
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(appColors.primary)
                            .height(48.dp)
                            .width(76.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) {
                                if (commentText.isNotBlank()) {
                                    if (article?.id != null) {
                                        articleDetailViewModel.onSendComments(
                                            article.id,
                                            commentText
                                        )
                                        commentText = ""
                                    }
                                }
                            }
                    ) {
                        Text(
                            text = "发送",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = appColors.onButtonColor
                            )
                        )
                    }
                }
            }
        }


    }
}

