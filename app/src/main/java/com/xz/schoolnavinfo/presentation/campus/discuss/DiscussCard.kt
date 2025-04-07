package com.xz.schoolnavinfo.presentation.campus.discuss

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.xz.schoolnavinfo.domain.model.dto.ArticleDTO
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun DiscussCard(
    articleDTO: ArticleDTO
) {
    val appColors = AppColors.current
    val article = articleDTO.article
    val userInfo = articleDTO.userInfo
    val images = articleDTO.images
    Log.e("TAG", "DiscussCard: $article $userInfo")
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
                Text(
                    text = "${article?.createTime}",
                    style = TextStyle(
                        color = appColors.greyMedium
                    )
                )
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
                .padding(top = 3.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            Column {

                Row {
                    if (images != null) {
                        for (image in images) {
                            // todo
                            val imgUrl = "$BASE_URL/uploads/${image.url}"
                            Log.e("TAG", "DiscussCard: $imgUrl")
                            Image(
                                modifier = Modifier
                                    .size(120.dp),
                                painter = rememberAsyncImagePainter(imgUrl),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

            }

        }
    }
}