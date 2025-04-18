package com.xz.schoolnavinfo.presentation.map.components

import androidx.compose.foundation.background
import com.xz.schoolnavinfo.presentation.theme.AppColors
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.presentation.common.baidu.map.RoutePlanType


@Composable
fun RoutePlan(
    onCancel: () -> Unit = {},
    onNavi: () -> Unit = {},
    onRoutePlanType: (routePlanType: RoutePlanType) -> Unit = {},
    distance: String = "0km",
    duration: String = "0分钟",
    routePlanType: RoutePlanType = RoutePlanType.Walking
) {
    val appColors = AppColors.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 46.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .width(300.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onRoutePlanType(RoutePlanType.Walking)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (routePlanType == RoutePlanType.Walking)
                            appColors.primary else appColors.greyHeavy
                    ),
                    shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(R.drawable.directions_walk_24px),
                            contentDescription = "步行"
                        )
                        Text("步行", style = TextStyle(color = appColors.bgPrimary))
                    }

                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onRoutePlanType(RoutePlanType.Biking)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (routePlanType == RoutePlanType.Biking)
                            appColors.primary else appColors.greyHeavy
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(R.drawable.directions_bike_24px),
                            contentDescription = "骑行"
                        )
                        Text("骑行", style = TextStyle(color = appColors.bgPrimary))
                    }
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onRoutePlanType(RoutePlanType.Driving)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (routePlanType == RoutePlanType.Driving)
                            appColors.primary else appColors.greyHeavy
                    ),
                    shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(R.drawable.directions_car_24px),
                            contentDescription = "驾车"
                        )
                        Text("驾车", style = TextStyle(color = appColors.bgPrimary))
                    }

                }
            }

            Text(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(appColors.greyMedium.copy(alpha = 0.3f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                text = "全程${distance}千米 约${duration}",
                style = TextStyle(
                    color = appColors.fontPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            )
        }



        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .width(200.dp)
                    .padding(bottom = 10.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onCancel()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.warn
                    ),
                    shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            imageVector = Icons.Default.Close,
                            contentDescription = "取消"
                        )
                        Text("取消", style = TextStyle(color = appColors.bgPrimary))
                    }
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onNavi()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.primary
                    ),
                    shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.size(18.dp),
                            painter = painterResource(R.drawable.navigation_24px),
                            contentDescription = "取消"
                        )
                        Text("导航", style = TextStyle(color = appColors.bgPrimary))
                    }

                }
            }
        }
    }
}

@Preview
@Composable
fun Tmp() {
    RoutePlan()
}