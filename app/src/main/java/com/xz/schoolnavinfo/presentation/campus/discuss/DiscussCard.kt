package com.xz.schoolnavinfo.presentation.campus.discuss

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.net.BASE_URL
import com.xz.schoolnavinfo.common.net.getStaticCompleteUrl
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun DiscussCard(
    articleDTO: ArticleDTO,
    onImageClick: (String) -> Unit
) {
    val appColors = AppColors.current
    val article = articleDTO.article
    val userInfo = articleDTO.userInfo
    val imageList = articleDTO.imageList
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(appColors.bgPrimary)
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        Row {
            Image(
                modifier = Modifier
                    .size(46.dp),
                painter = painterResource(R.drawable.heard_image),
                contentDescription = "头像"
            )
            Column(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .height(46.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = userInfo?.nickname ?: "",
                    style = TextStyle(
                        color = appColors.fontPrimary,
                        fontSize = 20.sp
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

        if (article?.title?.isNotBlank() == true) {
            Row(
                modifier = Modifier
                    .padding(top = 5.dp)
            ) {
                Text(
                    text = article.title,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = appColors.fontPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                )
            }

        }

        if (article?.content?.isNotBlank() == true) {
            Row(
                modifier = Modifier
                    .padding(top = 3.dp)
            ) {
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
        }

        Row(
            modifier = Modifier
                .padding(top = 5.dp)
        ) {
            if (!imageList.isNullOrEmpty()) {
                ImageGrid(imageList = imageList) {
                    onImageClick(it)
                }
            }
        }

    }
}

@Composable
fun ImageGrid(imageList: List<String>, onImageClick: (String) -> Unit) {
    // 图片数量和每行最大显示数量
    val maxImagesPerRow = 3
    val rows = (imageList.size + maxImagesPerRow - 1) / maxImagesPerRow // 计算行数

    Column(
        verticalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .padding(top = 3.dp)
    ) {
        if (imageList.size == 4) {
            for (i in 0 until 2) {
                val start = i * 2
                val end = start + 2
                val rowImages = imageList.subList(start, end)
                ImageRow(rowImages, onImageClick)
            }
        } else {
            for (i in 0 until rows) {
                // 当前行的图片子列表
                val start = i * maxImagesPerRow
                val end = minOf(start + maxImagesPerRow, imageList.size)
                val rowImages = imageList.subList(start, end)
                ImageRow(rowImages, onImageClick)
            }
        }
    }
}

@Composable
private fun ImageRow(
    rowImageUrlList: List<String>,
    onImageClick: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp), // 设置间隔
        modifier = Modifier.fillMaxWidth()
    ) {
        rowImageUrlList.forEach { image ->
            Image(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .size(120.dp)
                    .clickable { onImageClick(image) },
                painter = rememberAsyncImagePainter(getStaticCompleteUrl(image)),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}



