package com.xz.schoolnavinfo.presentation.campus.publish

import android.app.Activity
import android.content.Intent
import android.view.Gravity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.createImagePickerIntent
import com.esafirm.imagepicker.model.Image
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.presentation.common.components.CustomCheckbox
import com.xz.schoolnavinfo.presentation.common.components.LoadingDialog
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.flow.collectLatest


@Composable
fun PublishDiscussScreen(
    publishArticleViewModel: PublishArticleViewModel,
    articleType: ArticleType,
    commonViewModel: CommonViewModel
) {
    publishArticleViewModel.switchArticleType(articleType)

    val uiState by publishArticleViewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()


    PublishArticleContent(
        articleType = articleType,
        scrollState = scrollState,
        viewModel = publishArticleViewModel,
        commonViewModel = commonViewModel,
        uiState = uiState
    )

    RunCoroutine(
        viewModel = publishArticleViewModel,
        commonViewModel = commonViewModel,
        scrollState = scrollState,
        imagesSize = uiState.images.size
    )
}

@Composable
fun RunCoroutine(
    viewModel: PublishArticleViewModel,
    commonViewModel: CommonViewModel,
    scrollState: ScrollState,
    imagesSize: Int,
) {
    LaunchedEffect(imagesSize) {
        scrollState.animateScrollTo(scrollState.maxValue)
    }
    LaunchedEffect(Unit) {
        viewModel.publishOver.collectLatest {
            commonViewModel.onNavEvent(NavEvent.BackPage)
        }
    }
    LaunchedEffect(Unit) {
        commonViewModel.selectLocationFlow.collectLatest {
            viewModel.setLocation(it)
        }
    }
}

@Composable
private fun PublishArticleContent(
    articleType: ArticleType,
    scrollState: ScrollState,
    viewModel: PublishArticleViewModel,
    commonViewModel: CommonViewModel,
    uiState: PublishArticleUiState,
) {

    val appColors = AppColors.current
    val statusBarPadding = WindowInsets.systemBars.asPaddingValues()
    val isEmpty = uiState.title.isBlank() && uiState.content.isBlank() && uiState.images.isEmpty()
    val isAddBanner =
        articleType == ArticleType.Activity && (uiState as PublishArticleUiState.Activity).isAddBanner
    val imageSize by remember { mutableStateOf(76.dp) }
    val (selectType, updateSelectType) = rememberSaveable { mutableIntStateOf(0) }


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = ImagePicker.getImages(result.data)
            data?.let {
                if (selectType == 0) {
                    viewModel.setImageUrls(it)
                } else {
                    viewModel.setBanner(it[0])
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .background(appColors.bgPrimary)
            .fillMaxSize()
            .padding(statusBarPadding)
    ) {

        Column(
            modifier = Modifier
                .background(appColors.bgPrimary)
                .fillMaxSize()
        ) {
            LoadingDialog(uiState.isLoading)

            PublishArticleTopBar(
                articleType = articleType,
                isEmpty = isEmpty,
                isAddBanner = isAddBanner,
                onBack = { commonViewModel.onNavEvent(NavEvent.BackPage) },
                onClear = { },
                onPublish = { }
            )

            PublishArticleBody(
                articleType = articleType,
                scrollState = scrollState,
                updateSelectType = updateSelectType,
                isAddBanner = isAddBanner,
                updateIsAddBanner = { viewModel.setAddBanner(it) },
                address = uiState.address,
                images = uiState.images,
                title = uiState.title,
                updateTitle = { viewModel.setTitle(it) },
                content = uiState.content,
                updateContent = { viewModel.setContent(it) },
                imagePickerLauncher = imagePickerLauncher,
                bannerSection = {
                    uiState.ifActivity { activityState ->
                        BannerImageSection(
                            banner = activityState.banner,
                            imageSize = imageSize,
                            imagePickerLauncher = imagePickerLauncher,
                            updateSelectType = updateSelectType
                        )
                    }
                },
                onLocation = {},
                onRemoveImage = { },
            )
        }
    }
}

@Composable
fun PublishArticleBody(
    articleType: ArticleType,
    scrollState: ScrollState,
    updateSelectType: (Int) -> Unit,
    isAddBanner: Boolean,
    updateIsAddBanner: (Boolean) -> Unit,
    address: String,
    images: List<Image>,
    title: String,
    updateTitle: (String) -> Unit,
    content: String,
    updateContent: (String) -> Unit,
    imagePickerLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    bannerSection: @Composable () -> Unit,
    onLocation: () -> Unit,
    onRemoveImage: () -> Unit
) {

    val context = LocalContext.current
    val imageSize by remember { mutableStateOf(76.dp) }
    val isSystemInDarkTheme = isSystemInDarkTheme()
    val appColors = AppColors.current

    //图片区域
    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)

    ) {
        bannerSection()
        for (image in images) {
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
                            onRemoveImage()
                        },
                    imageVector = Icons.Default.Clear,
                    tint = appColors.bgScreen,
                    contentDescription = null
                )
            }

        }

        //图片选择
        if (images.size < 9) {
            Box(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(imageSize)
                    .clip(RoundedCornerShape(10.dp))
                    .background(appColors.greyMedium.copy(.5f))
                    .clickable {
                        updateSelectType(0)
                        val intent = createImagePickerIntent(
                            context, ImagePickerConfig(
                                limit = 9 - images.size,
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
                        .background(appColors.greyMedium)

                )
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(26.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(appColors.greyMedium)

                )
            }
        }

    }

    //是否选择轮播图
    if (articleType == ArticleType.Activity) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, top = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomCheckbox(
                state = isAddBanner,
                size = 18.dp,
                uncheckedColor = appColors.greyMedium.copy(.5f),
                checkedColor = appColors.fontPrimary
            ) {
                updateIsAddBanner(it)
//                isChecked ->
//                viewModel.onEvent(PublishArticleEvent.AddBanner(isChecked))
            }
            Text(
                modifier = Modifier
                    .padding(start = 2.dp),
                text = "添加到轮播图",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isAddBanner) appColors.fontSecondary else appColors.greyHeavy,
                )
            )
        }
    }
    //选择地址
    Box(
        modifier = Modifier
            .padding(start = 10.dp, top = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(appColors.greyMedium.copy(.5f))
                .clickable {
                    onLocation()
//                    commonViewModel.onNavEvent(NavEvent.MapLocationSelect)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .size(20.dp),
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = appColors.fontSecondary
            )
            Text(
                modifier = Modifier
                    .padding(vertical = 5.dp)
                    .padding(end = 10.dp),
                text = address.ifBlank { "选择地址.." },
                style = TextStyle(
                    fontSize = 16.sp,
                    color = appColors.greyHeavy
                )
            )
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
            value = title,
            onValueChange = { text ->
                updateTitle(text)
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
                    if (title.isBlank()) {
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
            value = content,
            onValueChange = { text ->
                updateContent(text)
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 156.dp, max = 200.dp),
            maxLines = 100,
            minLines = 1,
            textStyle = TextStyle(
                color = appColors.fontPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            ),
            decorationBox = {
                if (content.isBlank()) {
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

@Composable
fun BannerImageSection(
    banner: Image?,
    imageSize: Dp,
    imagePickerLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    updateSelectType: (Int) -> Unit
) {
    val appColors = AppColors.current
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    Box(
        modifier = Modifier
            .padding(start = 10.dp)
            .size(imageSize)
            .clip(RoundedCornerShape(10.dp))
            .background(appColors.greyMedium.copy(.5f))
            .clickable {
                updateSelectType(1)
                val intent = createImagePickerIntent(
                    context, ImagePickerConfig(
                        limit = 1,
                        theme = if (isDark) R.style.ImagePickerThemeDark else R.style.ImagePickerThemeLight
                    )
                )
                imagePickerLauncher.launch(intent)
            },
        contentAlignment = Alignment.Center
    ) {
        if (banner == null) {
            Text(
                text = "封面",
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = appColors.greyMedium
                )
            )
        } else {
            Image(
                modifier = Modifier
                    .size(imageSize),
                contentScale = ContentScale.Crop,
                painter = rememberAsyncImagePainter(banner.uri),
                contentDescription = null
            )
        }
    }
}

@Composable
fun PublishArticleTopBar(
    articleType: ArticleType,
    isEmpty: Boolean,
    isAddBanner: Boolean,
    onBack: () -> Unit,
    onClear: () -> Unit,
    onPublish: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val appColors = AppColors.current
    val context = LocalContext.current
    Row(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(start = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .clickable {
                        keyboardController?.hide()
                        onBack()
//                        commonViewModel.onLocationSelectEvent(null)
//                        commonViewModel.onNavEvent(NavEvent.BackPage)
                    },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null
            )
            Text(
                text = articleType.title,
                style = TextStyle(
                    fontSize = 18.sp,
                    color = appColors.fontPrimary,
                    fontWeight = FontWeight.Bold
                )
            )

        }

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
                        onClear()
//                        publishArticleViewModel.onEvent(PublishArticleEvent.Clear(campusMenu))
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

                        if (isAddBanner) {
                            //addBanner && bannerImage == null
                            StyleableToast.Builder(context)
                                .text("请选择轮播图")
                                .textColor(Color.White.toArgb())
                                .backgroundColor(Color(0xFF0091EA).toArgb())
                                .cornerRadius(36)
                                .gravity(Gravity.TOP)
                                .show()
                        } else {
                            if (isEmpty) {
                                //articleInfo.title.isBlank() && articleInfo.content.isBlank() && selectImageList.isEmpty()
                                StyleableToast.Builder(context)
                                    .text("标题、内容、图片不能全为空")
                                    .textColor(Color.White.toArgb())
                                    .backgroundColor(Color(0xFF0091EA).toArgb())
                                    .cornerRadius(36)
                                    .gravity(Gravity.TOP)
                                    .show()
                            } else {
                                onPublish()
//                                publishArticleViewModel.onEvent(
//                                    PublishArticleEvent.PublishArticle(
//                                        if (campusMenu == CampusMenu.Discuss) ArticleType.Discuss else ArticleType.Activity,
//                                        addBanner
//                                    )
//                                )
//                                commonViewModel.onLocationSelectEvent(null)
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
}
