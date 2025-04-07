package com.xz.schoolnavinfo.presentation.campus.activity

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
@Preview
fun ActivityScreen(){

    val appColors = AppColors.current
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(10.dp)
                .fillMaxSize(),

        ) {
            Row {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop,
                        painter = painterResource(R.drawable.lunbo1),
                        contentDescription = null
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .height(50.dp)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black),
                                    startY = 0f,
                                    endY = Float.POSITIVE_INFINITY
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .align(alignment = Alignment.BottomEnd)
                            .padding(bottom = 10.dp, end = 10.dp)
                    ) {
                        Box(
                            modifier =  Modifier
                                .clip(CircleShape)
                                .background(appColors.bgPrimary)
                                .size(10.dp)
                        )
                        Box(
                            modifier =  Modifier
                                .padding(horizontal = 5.dp)
                                .clip(CircleShape)
                                .background(appColors.greyMedium)
                                .size(10.dp)
                        )
                        Box(
                            modifier =  Modifier
                                .clip(CircleShape)
                                .background(appColors.greyMedium)
                                .size(10.dp)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 10.dp, bottom = 5.dp)
                    ) {
                        Text(
                            text = "活动测试1",
                            color = appColors.bgPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(appColors.bgPrimary)
                    .padding(top = 10.dp, bottom = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .padding(start = 5.dp, end = 10.dp)
                        .size(16.dp),
                    painter = painterResource(R.drawable.top_up),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(appColors.primary)
                )
                Text(
                    text = "置顶置顶置顶置顶置顶置顶置顶置顶置顶置顶置顶置顶置顶",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = appColors.fontPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 5.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(appColors.bgPrimary)
                    .padding(top = 10.dp, bottom = 10.dp, end = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .padding(start = 5.dp, end = 10.dp)
                        .size(16.dp),
                    painter = painterResource(R.drawable.notify),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(appColors.primary)
                )
                Text(
                    text = "通知通知通知通知通知通知通知通知通知通知通知通知通知通知通知通知通知通知通知",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = appColors.fontPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                )
            }

            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(appColors.bgPrimary)
                    .fillMaxWidth()
                    .padding(10.dp),
            ) {
                Row(
                    modifier = Modifier
                ) {
                    Text(
                        text = "9月1日在操场举行开学典礼活动",
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(
                            color = appColors.fontPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                        )

                    )
                }
                Column(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .clip(RoundedCornerShape(5.dp))
                ) {
                    Row {
                        Image(
                            modifier = Modifier
                                .size(120.dp),
                            painter = painterResource(R.drawable.img1),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                        Image(
                            modifier = Modifier
                                .size(120.dp),
                            painter = painterResource(R.drawable.img3),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier
                                .size(16.dp),
                            imageVector = Icons.Default.LocationOn,
                            tint = appColors.fontSecondary,
                            contentDescription = null
                        )
                        Text(
                            "晨光体育场",
                            style = TextStyle(
                                color = appColors.fontSecondary,
                                fontSize = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }


}