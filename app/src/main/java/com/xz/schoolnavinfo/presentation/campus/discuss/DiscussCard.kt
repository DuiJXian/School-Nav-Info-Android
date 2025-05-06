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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.net.getImagesUrl
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun DiscussCard(
    articleDTO: ArticleDTO,
    onImageClick: (String) -> Unit,
    onLocation: (String) -> Unit,
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
        //用户信息区域
        Row {

            Image(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape),
                painter = if (userInfo?.avatarUrl?.isNotBlank() == true)
                    rememberAsyncImagePainter(getImagesUrl(userInfo.avatarUrl)) else
                    painterResource(R.drawable.heard_image),
                contentDescription = "头像",
                contentScale = ContentScale.Crop
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
        //标题区域
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
        //内容区域
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
        //图片区域
        if (!imageList.isNullOrEmpty()) {
            Row(
                modifier = Modifier
                    .padding(top = 5.dp)
            ) {
                ImageGrid(imageList = imageList) {
                    onImageClick(it)
                }
            }

        }
        //位置区域
        if (article?.address?.isNotBlank() == true) {
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(appColors.greyMedium.copy(.5f))
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {
                        article.location?.let { onLocation(it) }
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
                    text = article.address,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.greyHeavy
                    )
                )
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
                painter = rememberAsyncImagePainter(getImagesUrl(image)),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
        }
    }
}



