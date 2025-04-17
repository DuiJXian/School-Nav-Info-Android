package com.xz.schoolnavinfo.presentation.campus.detail

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
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
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.compose.ImageHorizontalScroll
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun ArticleDetailScreen(
    articleDTO: ArticleDTO,
    campusMenu: CampusMenu,
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

    LaunchedEffect(true) {
        article?.id?.let { articleDetailViewModel.onGetComments(it) }
    }

    Box(
        modifier = Modifier
            .background(appColors.bgPrimary)
            .offset(x = 0.dp, y = -animatedOffset)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp)
                .height(50.dp),
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



            if (campusMenu == CampusMenu.Discuss) {
                if (userInfo?.avatarUrl != null) {
                    Image(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(40.dp),
                        painter = rememberAsyncImagePainter(userInfo.avatarUrl),
                        contentDescription = "头像",
                    )
                } else {
                    Image(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(40.dp),
                        painter = painterResource(R.drawable.heard_image),
                        contentDescription = "头像",
                    )
                }
            }



            Column(
            ) {
                if (campusMenu != CampusMenu.Activity) {
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
                }

                if (article?.createTime != null) {
                    Text(
                        modifier = Modifier
                            .padding(start = 5.dp),
                        text = TimeUtils.formatTimeDifference(article.createTime),
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = appColors.greyMedium,
                        )
                    )
                }

            }
        }

        Column(
            modifier = Modifier
                .padding(top = 86.dp, bottom = 56.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {


            //图片区域
            Row {
                val contentImageList = if (!imageList.isNullOrEmpty()) imageList.filterNot { it.contains("banner") } else emptyList()
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
                            color = appColors.fontPrimary,
                            fontWeight = FontWeight.Bold,
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


            if (campusMenu != CampusMenu.Activity && commentDTOList.isNotEmpty()) {
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

            Spacer(Modifier.height(18.dp))

        }


        if (campusMenu != CampusMenu.Activity) {
            val borderColor = if (isFocused) appColors.primary else appColors.greyHeavy
            var sendButtonColor by remember { mutableStateOf(Color(0xFFBDBDBD)) }
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .background(appColors.bgPrimary)
                    .align(Alignment.BottomCenter)
                    .padding(10.dp)
            ) {

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .height(48.dp)
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
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
                            sendButtonColor = if (commentText.isNotBlank()) {
                                appColors.primary
                            } else {
                                Color(0xFFBDBDBD)
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                    )
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(sendButtonColor)
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

