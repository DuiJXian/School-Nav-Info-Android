package com.xz.schoolnavinfo.presentation.campus.discuss.publish

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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.createImagePickerIntent
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.presentation.theme.AppColors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublishDiscussScreen(
    publishDiscussViewModel: PublishDiscussViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val appColors = AppColors.current

    val title by publishDiscussViewModel.title
    val content by publishDiscussViewModel.content
    val images by publishDiscussViewModel.images

    val imageSize by remember { mutableStateOf(76.dp) }
    val scrollState = rememberScrollState()
    val isSystemInDarkTheme = isSystemInDarkTheme()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = ImagePicker.getImages(result.data)
            data?.let {
                publishDiscussViewModel.onEvent(PublishDiscussEvent.ImagesAdd(it))
            }
        }
    }

    LaunchedEffect(images) {
        // 确保图片列表加载完成后滚动到最后
        scrollState.animateScrollTo(scrollState.maxValue)
    }


    Scaffold(
        containerColor = appColors.bgScreen,
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .background(appColors.bgScreen),
                title = {
                    Text(
                        text = "讨论",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = appColors.fontPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = appColors.bgScreen,
                    titleContentColor = appColors.fontPrimary,
                    navigationIconContentColor = appColors.fontPrimary
                ),
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "返回"
                    )
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .shadow(3.dp, RoundedCornerShape(10.dp))
                            .background(appColors.primary)
                            .width(60.dp)
                            .height(30.dp),
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
            )
        }
    ) {

        Column(
            modifier = Modifier
                .background(appColors.bgScreen)
                .padding(it)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .horizontalScroll(scrollState)

            ) {

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
                                    publishDiscussViewModel.onEvent(
                                        PublishDiscussEvent.ImagesRemove(
                                            image
                                        )
                                    )
                                },
                            imageVector = Icons.Default.Clear,
                            tint = appColors.bgScreen,
                            contentDescription = null
                        )
                    }

                }

                if (images.size < 9){
                    Box(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(imageSize)
                            .clip(RoundedCornerShape(10.dp))
                            .background(appColors.greyMedium)
                            .clickable {
                                val intent = createImagePickerIntent(
                                    context, ImagePickerConfig(
                                        limit = 9 - images.size,
                                        theme = if(isSystemInDarkTheme) R.style.ImagePickerThemeDark else R.style.ImagePickerThemeLight
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
                    value = title,
                    onValueChange = { text ->
                        publishDiscussViewModel.onEvent(
                            PublishDiscussEvent.TitleChange(
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
                        publishDiscussViewModel.onEvent(
                            PublishDiscussEvent.ContentChange(
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
                        if (content.isBlank()) {
                            Text(
                                text = "输入你想讨论的内容..",
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


@Preview
@Composable
private fun Tmp() {
    PublishDiscussScreen()
}