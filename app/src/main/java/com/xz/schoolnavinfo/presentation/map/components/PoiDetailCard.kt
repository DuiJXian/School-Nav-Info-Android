package com.xz.schoolnavinfo.presentation.map.components

import AppColors
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baidu.mapapi.search.core.PoiDetailInfo
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.presentation.common.utils.LocateUtils
import com.xz.schoolnavinfo.presentation.common.utils.TimeUtils

@Composable
fun PoiDetailCard(
    modifier: Modifier = Modifier,
    poiDetailInfo: PoiDetailInfo,
    onCancel: () -> Unit,
    onGo: () -> Unit
) {
    val appColors = AppColors.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = appColors.bgPrimary,
        shadowElevation = 5.dp
    ) {

        Column(
            modifier = Modifier
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.home),
                    contentDescription = null
                )
                Column(
                    Modifier
                        .padding(start = 10.dp)
                ) {
                    Text(
                        text = "${poiDetailInfo.name}",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = appColors.fontPrimary
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StarRatingCanvas(rating = poiDetailInfo.overallRating, starSize = 18.dp)
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = appColors.fontAccent,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(" ${poiDetailInfo.overallRating}分")
                                }
                            }
                        )
                    }
                    Text(
                        text = buildAnnotatedString {
                            if(!poiDetailInfo.brand.isNullOrBlank()){
                                withStyle(
                                    style = SpanStyle(
                                        color = appColors.fontSecondary, fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("${poiDetailInfo.brand} ")
                                }
                            }
                            withStyle(style = SpanStyle(color = appColors.fontSecondary)) {
                                append("${poiDetailInfo.tag.replace(";", "/")}")
                            }
                        }
                    )

                    if (!poiDetailInfo.shopHours.isNullOrBlank()) {
                        var businessColor = appColors.fontPrimary
                        if (TimeUtils.isTimeInRange(poiDetailInfo.shopHours)) {
                            businessColor = Color(0xFF54C459)
                        }
                        Text(
                            text = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = businessColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("营业中")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = appColors.fontPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(" ${poiDetailInfo.shopHours}")
                                }

                            }
                        )
                    }

                    Text(
                        text = buildAnnotatedString {
                            withStyle(
                                style = SpanStyle(
                                    color = appColors.fontSecondary,
                                )
                            ) {
                                append("距您")
                            }

                            withStyle(
                                style = SpanStyle(
                                    color = appColors.fontPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("${LocateUtils.metersToKilometers(poiDetailInfo.distance)}km")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = appColors.fontSecondary,
                                )
                            ) {
                                append(" ${poiDetailInfo.address}")
                            }

                        },
                        modifier = Modifier.width(250.dp)
                    )
                }
            }
            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center,
            ) {

                if (!poiDetailInfo.telephone.isNullOrEmpty()) {
                    Button(
                        modifier = Modifier
                            .padding(end = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appColors.primary
                        ),
                        onClick = {
                            onCancel()
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                modifier = Modifier.size(18.dp),
                                imageVector = Icons.Default.Phone,
                                contentDescription = "电话"
                            )
                            Text("电话")
                        }
                    }
                }
                Button(
                    modifier = Modifier
                        .padding(end = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.primary
                    ),
                    onClick = {
                        onCancel()
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Default.Close,
                            contentDescription = "电话"
                        )
                        Text("取消")
                    }

                }

                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.primary
                    ),
                    onClick = {
                        onGo()
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Default.Place,
                            contentDescription = "电话"
                        )
                        Text("导航")
                    }
                }
            }
        }
    }
}


@Composable
@Preview
fun PrePoiDetailCard() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val poiDetailInfo = PoiDetailInfo().apply {
            name = "蜜雪冰城(金石桥)"
            type = "奶茶店"
            distance = 1200
            address = "湖南省邵阳市隆回县羊古坳乡罗英村"
            brand = "蜜雪冰城"
            overallRating = 4.2
            shopHours = "09:45-22:30"
            tag = "休闲娱乐;度假村"
        }
        PoiDetailCard(poiDetailInfo = poiDetailInfo, onGo = {}, onCancel = {})
    }
}

