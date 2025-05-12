package com.xz.schoolnavinfo.presentation.campus.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.common.net.getImagesUrl
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun ActivityCard(
    modifier: Modifier = Modifier,
    articleDTO: ArticleDTO,
    onImageClick: (String) -> Unit,
    onLocation: (String) -> Unit,
) {
    val appColors = AppColors.current
    val article = articleDTO.article
    val imageList = articleDTO.imageList
    Column(
        modifier = modifier
            .background(appColors.bgPrimary)
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        //标题
        if (article?.title?.isNotBlank() == true) {
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
        //正文
        if (article?.content?.isNotBlank() == true) {
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
        //图片
        if (!imageList.isNullOrEmpty()) {
            Spacer(Modifier.height(3.dp))
            ImageGrid(imageList = imageList.filterNot { it.contains("banner") }) {
                onImageClick(it)
            }
        }
        //地址
        if (article?.address?.isNotBlank() == true) {
            Spacer(Modifier.height(5.dp))
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(appColors.greyMedium.copy(.5f))
                    .clickable { article.location?.let { onLocation(it) } },
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
                    text = article.address,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.greyHeavy
                    )
                )
            }
        }

        Spacer(Modifier.height(5.dp))
        Row {
            Text(
                "${articleDTO.userInfo?.nickname}",
                style = TextStyle(color = appColors.greyMedium)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "${article?.createTime?.let { TimeUtils.formatTimeDifference(it) }}",
                style = TextStyle(color = appColors.greyMedium)
            )
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
            val imgUrl = getImagesUrl(image)
            Image(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .size(120.dp)
                    .clickable { onImageClick(image) },
                painter = rememberAsyncImagePainter(imgUrl),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}



