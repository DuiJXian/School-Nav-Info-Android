package com.xz.schoolnavinfo.presentation.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.common.net.getImagesUrl

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
