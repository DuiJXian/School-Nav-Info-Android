package com.xz.schoolnavinfo.presentation.campus.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.net.montageCompleteUrl
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.domain.data.dto.CommentDTO
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun CommentCard(
    commentDTO: CommentDTO
) {
    val appColor = AppColors.current
    val userInfo = commentDTO.userInfo
    val comment = commentDTO.comment
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(appColor.bgPrimary)
    ) {
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(appColor.greyLight)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(40.dp)
                    .clip(CircleShape),
                painter = if (userInfo.avatarUrl.isNullOrBlank())
                    painterResource(R.drawable.heard_image) else
                    rememberAsyncImagePainter(montageCompleteUrl(userInfo.avatarUrl)),
                contentScale = ContentScale.Crop,
                contentDescription = "头像",
            )

            Column(
                modifier = Modifier
                    .padding(start = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = userInfo.nickname,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = appColor.fontSecondary
                    )
                )

                comment.createTime?.let {
                    Text(
                        text = TimeUtils.formatTimeDifference(comment.createTime),
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = appColor.greyMedium
                        )
                    )
                }
            }
        }
        Row(
            modifier = Modifier
        ) {
            Spacer(
                modifier = Modifier
                    .width(50.dp)
            )
            Text(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp),
                text = comment.content,
                style = TextStyle(
                    color = appColor.fontPrimary,
                    fontSize = 16.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
    }
}