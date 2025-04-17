package com.xz.schoolnavinfo.presentation.map.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.baidu.mapapi.search.core.PoiDetailInfo
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.utils.LocateUtils
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun PoiDetailCard(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    poiDetailInfo: PoiDetailInfo,
    onCancel: () -> Unit,
    onCall: (tel: String) -> Unit,
    onFavorite: () -> Unit,
    onRoute: () -> Unit
) {
    val appColors = AppColors.current

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = appColors.bgPrimary,
        shadowElevation = 10.dp
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {

                if(!poiDetailInfo.image.isNullOrBlank()){
                    Image(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .size(128.dp)
                            .border(1.dp, appColors.greyLight, RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop,
                        painter = rememberAsyncImagePainter(poiDetailInfo.image),
                        contentDescription = null,
                    )
                }else{
                    Image(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, appColors.greyLight, RoundedCornerShape(10.dp))
                            .size(128.dp),
                        painter = painterResource(R.drawable.home),
                        contentDescription = null
                    )
                }

                Column(
                    Modifier
                        .padding(start = 10.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = poiDetailInfo.name,
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = appColors.fontPrimary
                            )
                        )
                        Spacer(Modifier.width(3.dp))

                        Icon(
                            modifier = Modifier
                                .size(22.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    onFavorite()
                                },
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "收藏",
                            tint = if (isFavorite) {
                                Color(0xFFFFA000)
                            } else {
                                appColors.greyMedium
                            }
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (poiDetailInfo.overallRating > 0) {
                            StarRatingCanvas(rating = poiDetailInfo.overallRating, starSize = 18.dp)
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = appColors.err,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(" ${poiDetailInfo.overallRating}分")
                                    }
                                }
                            )
                        }

                    }
                    Text(
                        text = buildAnnotatedString {
                            if (poiDetailInfo.price != 0.0) {
                                withStyle(
                                    style = SpanStyle(
                                        color = appColors.fontSecondary
                                    )
                                ) {
                                    append("人均: ")
                                }
                                withStyle(
                                    style = SpanStyle(
                                        color = appColors.err,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("${poiDetailInfo.price}￥ ")
                                }
                            }
                            withStyle(style = SpanStyle(color = appColors.fontSecondary)) {
                                append("${poiDetailInfo.tag.replace(";", "/")}")
                            }
                        }
                    )

                    if (!poiDetailInfo.shopHours.isNullOrBlank()) {
                        var businessColor = appColors.greyMedium
                        if (TimeUtils.isTimeInRange(poiDetailInfo.shopHours)) {
                            businessColor = appColors.info
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
                                append(LocateUtils.metersToKilometers(poiDetailInfo.distance))
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


                Button(
                    modifier = Modifier,
                    shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.warn
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
                            contentDescription = "取消"
                        )
                        Text("取消")
                    }

                }

                if (!poiDetailInfo.telephone.isNullOrEmpty()) {
                    Button(
                        shape = RoundedCornerShape(0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = appColors.info
                        ),
                        onClick = {
                            onCall(poiDetailInfo.telephone)
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.primary
                    ),
                    shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
                    onClick = {
                        onRoute()
                    }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp).padding(end = 3.dp),
                            painter = painterResource(R.drawable.route_24px),
                            contentDescription = "路线"
                        )
                        Text("路线")
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
            //overallRating = 4.2
           // shopHours = "09:45-22:30"
            telephone = "18274340988"
            tag = "休闲娱乐;度假村"
            price = 9.2
        }
        PoiDetailCard(
            poiDetailInfo = poiDetailInfo,
            isFavorite = false,
            onRoute = {},
            onCancel = {},
            onCall = {},
            onFavorite = {})
    }
}

