package com.xz.schoolnavinfo.presentation.campus.publish

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.createImagePickerIntent
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.presentation.common.compose.LoadingDialog
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishDiscussScreen(
    publishArticleViewModel: PublishArticleViewModel,
    title: String,
    commonViewModel: CommonViewModel
) {
    val context = LocalContext.current
    val appColors = AppColors.current

    val articleState by publishArticleViewModel.articleState

    val discussImages by publishArticleViewModel.discussImages
    val activityImages by publishArticleViewModel.activityImages

    val bannerImage by publishArticleViewModel.imageBanner

    val imageSize by remember { mutableStateOf(76.dp) }
    val scrollState = rememberScrollState()
    val isSystemInDarkTheme = isSystemInDarkTheme()

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current

    var isBanner by remember { mutableStateOf(false) }

    var selectType by remember { mutableIntStateOf(0) } //0内容图片 轮播图

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = ImagePicker.getImages(result.data)
            data?.let {
                if (selectType == 0) {
                    if (title == "活动") {
                        publishArticleViewModel.onEvent(PublishArticleEvent.ActivityImagesAdd(it))
                    } else {
                        publishArticleViewModel.onEvent(PublishArticleEvent.DiscussImagesAdd(it))
                    }
                } else {
                    publishArticleViewModel.onEvent(PublishArticleEvent.ImageBanner(it[0]))
                }
            }
        }
    }

    LaunchedEffect(discussImages) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }

    LaunchedEffect(true) {
        publishArticleViewModel.netOver.collectLatest {
            commonViewModel.onNavEvent(NavEvent.BackPage)
        }
    }

    Scaffold(
        containerColor = appColors.bgScreen,
        snackbarHost = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                SnackbarHost(
                    modifier = Modifier
                        .align(Alignment.TopCenter),
                    hostState = snackbarHostState,
                )
            }
        },
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .background(appColors.bgScreen),
                title = {
                    Row(
                        modifier = Modifier
                            .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = appColors.fontPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (title == "活动") {
                            Box(
                                modifier = Modifier
                                    .padding(start = 5.dp)
                                    .clip(CircleShape)
                                    .size(20.dp)
                                    .background(appColors.primary.copy(alpha = .3f))
                                    .clickable {
                                        isBanner = !isBanner
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .size(16.dp)
                                        .background(if (isBanner) appColors.primary else appColors.bgPrimary)
                                )
                            }
                            Text(
                                text = "添加到轮播图",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appColors.fontPrimary,
                                )
                            )
                        }

                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appColors.bgScreen,
                    titleContentColor = appColors.fontPrimary,
                    navigationIconContentColor = appColors.fontPrimary
                ),
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) {
                                keyboardController?.hide()
                                //CommonViewModel.
                                commonViewModel.onNavEvent(NavEvent.BackPage)
                            },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回"
                    )
                },
                actions = {
                    Row(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .height(30.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .shadow(3.dp, RoundedCornerShape(10.dp))
                                .background(appColors.err)
                                .clip(RoundedCornerShape(10.dp))
                                .width(60.dp)
                                .height(30.dp)
                                .clickable {
                                    publishArticleViewModel.onEvent(PublishArticleEvent.Clear(title))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "清空",
                                style = TextStyle(
                                    color = appColors.onButtonColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .shadow(3.dp, RoundedCornerShape(10.dp))
                                .background(appColors.primary)
                                .clip(RoundedCornerShape(10.dp))
                                .width(60.dp)
                                .height(30.dp)
                                .clickable {
                                    val selectImageList =
                                        if (title == "活动") activityImages else discussImages
                                    if (isBanner && bannerImage.path.isBlank()) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar(
                                                "请选择轮播图片",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    } else {
                                        if (articleState.title.isBlank() && articleState.content.isBlank() && selectImageList.isEmpty()) {
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    "标题、内容、图片不能全为空",
                                                    duration = SnackbarDuration.Short
                                                )
                                            }
                                        } else {
                                            publishArticleViewModel.onEvent(
                                                PublishArticleEvent.PublishArticle(
                                                    if (title == "讨论") ArticleType.Discuss else ArticleType.Activity,
                                                    isBanner
                                                )
                                            )
                                        }
                                    }

                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "发布",
                                style = TextStyle(
                                    color = appColors.onButtonColor,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }

                }
            )
        }
    ) {


        Column(
            modifier = Modifier
                .background(appColors.bgScreen)
                .padding(it)
                .fillMaxSize()
        ) {
            LoadingDialog(publishArticleViewModel.isShowLoading.value)
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)

            ) {
                //是否显示轮播图选项
                if (isBanner) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(imageSize)
                            .clip(RoundedCornerShape(10.dp))
                            .background(appColors.greyMedium)
                            .clickable {
                                selectType = 1
                                val intent = createImagePickerIntent(
                                    context, ImagePickerConfig(
                                        limit = 1,
                                        theme = if (isSystemInDarkTheme) R.style.ImagePickerThemeDark else R.style.ImagePickerThemeLight
                                    )
                                )
                                imagePickerLauncher.launch(intent)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (bannerImage.path.isBlank()) {
                            Text(
                                text = "选择轮播图",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appColors.fontPrimary
                                )
                            )
                        } else {
                            Image(
                                modifier = Modifier
                                    .size(imageSize),
                                contentScale = ContentScale.Crop,
                                painter = rememberAsyncImagePainter(bannerImage.path),
                                contentDescription = null
                            )
                        }
                    }
                }

                //图片预览列表
                val disImageList = if (title == "活动") activityImages else discussImages
                for (image in disImageList) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Image(
                            modifier = Modifier
                                .size(imageSize),
                            contentScale = ContentScale.Crop,
                            painter = rememberAsyncImagePainter(image.path),
                            contentDescription = null
                        )
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(appColors.greyHeavy.copy(alpha = 0.5f))
                                .align(Alignment.TopEnd)
                                .size(20.dp)
                                .clickable {
                                    publishArticleViewModel.onEvent(
                                        PublishArticleEvent.ImagesRemove(
                                            image,
                                            title
                                        )
                                    )
                                },
                            imageVector = Icons.Default.Clear,
                            tint = appColors.bgScreen,
                            contentDescription = null
                        )
                    }

                }

                //选择按钮
                val size = if (title == "活动") activityImages.size else discussImages.size
                if (size < 9) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(imageSize)
                            .clip(RoundedCornerShape(10.dp))
                            .background(appColors.greyMedium)
                            .clickable {
                                selectType = 0
                                val intent = createImagePickerIntent(
                                    context, ImagePickerConfig(
                                        limit = 9 - size,
                                        theme = if (isSystemInDarkTheme) R.style.ImagePickerThemeDark else R.style.ImagePickerThemeLight
                                    )
                                )
                                imagePickerLauncher.launch(intent)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .width(26.dp)
                                .height(3.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(appColors.greyHeavy)

                        )
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(26.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(appColors.greyHeavy)

                        )
                    }
                }

            }
            Box(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 5.dp)
                    .padding(vertical = 5.dp),
                contentAlignment = Alignment.CenterStart
            ) {

                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    cursorBrush = SolidColor(appColors.primary),
                    value = articleState.title,
                    onValueChange = { text ->
                        publishArticleViewModel.onEvent(
                            PublishArticleEvent.TitleChange(
                                text
                            )
                        )
                    },
                    maxLines = 1,
                    textStyle = TextStyle(
                        color = appColors.fontPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            if (articleState.title.isBlank()) {
                                Text(
                                    text = "标题",
                                    style = TextStyle(
                                        color = appColors.greyHeavy,
                                        fontSize = 20.sp
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
            Spacer(
                Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(appColors.greyHeavy)
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 5.dp, horizontal = 10.dp)
            ) {
                BasicTextField(
                    value = articleState.content,
                    onValueChange = { text ->
                        publishArticleViewModel.onEvent(
                            PublishArticleEvent.ContentChange(
                                text
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 156.dp, max = 200.dp), // 可以设置最小和最大高度
                    maxLines = 100,  // 设置最多显示5行
                    minLines = 1,  // 设置至少显示1行
                    textStyle = TextStyle(
                        color = appColors.fontPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    decorationBox = {
                        if (articleState.content.isBlank()) {
                            Text(
                                text = "内容..",
                                style = TextStyle(
                                    color = appColors.greyHeavy,
                                    fontSize = 16.sp
                                )
                            )
                        }
                        it()
                    }
                )

            }

        }
    }
}


//@Preview
//@Composable
//private fun Tmp() {
//    PublishDiscussScreen()
//}