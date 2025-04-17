package com.xz.schoolnavinfo.presentation.campus.publish

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.compose.CustomCheckbox
import com.xz.schoolnavinfo.presentation.common.compose.LoadingDialog
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.flow.collectLatest


@Composable
fun PublishDiscussScreen(
    publishArticleViewModel: PublishArticleViewModel,
    campusMenu: CampusMenu,
    commonViewModel: CommonViewModel
) {
    val context = LocalContext.current
    val appColors = AppColors.current

    val articleInfo = publishArticleViewModel.articleInfo

    val discussImages by publishArticleViewModel.discussImages
    val activityImages by publishArticleViewModel.activityImages

    val bannerImage = publishArticleViewModel.imageBanner

    val imageSize by remember { mutableStateOf(76.dp) }
    val scrollState = rememberScrollState()
    val isSystemInDarkTheme = isSystemInDarkTheme()


    val keyboardController = LocalSoftwareKeyboardController.current

    val addBanner = publishArticleViewModel.addBanner

    var selectType by remember { mutableIntStateOf(0) } //0内容图片 轮播图

    val statusBarPadding = WindowInsets.systemBars.asPaddingValues()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = ImagePicker.getImages(result.data)
            data?.let {
                if (selectType == 0) {
                    if (campusMenu == CampusMenu.Activity) {
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
    LaunchedEffect(true) {
        commonViewModel.selectLocationFlow.collectLatest {
            publishArticleViewModel.onEvent(PublishArticleEvent.LocationChange(it))
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
            LoadingDialog(publishArticleViewModel.isShowLoading.value)

            Row(
                modifier = Modifier
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
                                commonViewModel.onLocationSelectEvent(null)
                                commonViewModel.onNavEvent(NavEvent.BackPage)
                            },
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null
                    )
                    Text(
                        text = campusMenu.title,
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
                                publishArticleViewModel.onEvent(PublishArticleEvent.Clear(campusMenu))
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
                                    if (campusMenu == CampusMenu.Activity) activityImages else discussImages
                                if (addBanner && bannerImage == null) {
                                    Toast.makeText(
                                        context,
                                        "请选择轮播图",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    if (articleInfo.title.isBlank() && articleInfo.content.isBlank() && selectImageList.isEmpty()) {
                                        Toast.makeText(
                                            context,
                                            "标题、内容、图片不能全为空",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        publishArticleViewModel.onEvent(
                                            PublishArticleEvent.PublishArticle(
                                                if (campusMenu == CampusMenu.Discuss) ArticleType.Discuss else ArticleType.Activity,
                                                addBanner
                                            )
                                        )
                                        commonViewModel.onLocationSelectEvent(null)
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
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)

            ) {
                //轮播图封面选择
                if (addBanner) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(imageSize)
                            .clip(RoundedCornerShape(10.dp))
                            .background(appColors.greyMedium.copy(.5f))
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
                        if (bannerImage == null) {
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
                                painter = rememberAsyncImagePainter(bannerImage.path),
                                contentDescription = null
                            )
                        }
                    }
                }

                //图片预览列表
                val disImageList =
                    if (campusMenu == CampusMenu.Activity) activityImages else discussImages
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
                                            campusMenu.title
                                        )
                                    )
                                },
                            imageVector = Icons.Default.Clear,
                            tint = appColors.bgScreen,
                            contentDescription = null
                        )
                    }

                }

                //图片选择
                val size =
                    if (campusMenu == CampusMenu.Activity) activityImages.size else discussImages.size
                if (size < 9) {
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(imageSize)
                            .clip(RoundedCornerShape(10.dp))
                            .background(appColors.greyMedium.copy(.5f))
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
            if (campusMenu == CampusMenu.Activity) {
                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomCheckbox(
                        state = addBanner,
                        size = 18.dp,
                        uncheckedColor = appColors.greyMedium.copy(.5f),
                        checkedColor = appColors.fontPrimary
                    ) { isChecked ->
                        publishArticleViewModel.onEvent(PublishArticleEvent.AddBanner(isChecked))
                    }
                    Text(
                        modifier = Modifier
                            .padding(start = 2.dp),
                        text = "添加到轮播图",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (addBanner) appColors.fontSecondary else appColors.greyHeavy,
                        )
                    )
                }
            }
            //选择地址
            Log.e("TAG", "PublishDiscussScreen: ${articleInfo.address}", )
            Box(
                modifier = Modifier
                    .padding(start = 10.dp, top = 5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(appColors.greyMedium.copy(.5f))
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            commonViewModel.onNavEvent(NavEvent.MapLocationSelect)
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
                        text = articleInfo.address.ifBlank { "选择地址.." },
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
                    value = articleInfo.title,
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
                            if (articleInfo.title.isBlank()) {
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
                    value = articleInfo.content,
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
                        if (articleInfo.content.isBlank()) {
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