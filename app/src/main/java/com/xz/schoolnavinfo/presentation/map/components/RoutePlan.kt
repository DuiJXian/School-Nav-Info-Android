package com.xz.schoolnavinfo.presentation.map.components

import AppColors
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xz.schoolnavinfo.presentation.common.baidu.RoutePlanType


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
                    .width(270.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onRoutePlanType(RoutePlanType.Walking)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = routePlanTypeColor(
                            routePlanType,
                            RoutePlanType.Walking,
                            appColors.primary,
                            appColors.secondary
                        )
                    ),
                    shape = RoundedCornerShape(topStart = 30.dp, bottomStart = 30.dp)
                ) {
                    Text("步行", style = TextStyle(color = appColors.bgPrimary))
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onRoutePlanType(RoutePlanType.Biking)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = routePlanTypeColor(
                            routePlanType,
                            RoutePlanType.Biking,
                            appColors.primary,
                            appColors.secondary
                        )
                    ),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Text("骑行", style = TextStyle(color = appColors.bgPrimary))
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onRoutePlanType(RoutePlanType.Driving)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = routePlanTypeColor(
                            routePlanType,
                            RoutePlanType.Driving,
                            appColors.primary,
                            appColors.secondary
                        )
                    ),
                    shape = RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp)
                ) {
                    Text("驾车", style = TextStyle(color = appColors.bgPrimary))
                }
            }

            Text(
                text = "全程${distance}千米 约${duration}",
                style = TextStyle(
                    color = appColors.fontInfo,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
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
                    .width(180.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onCancel()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.primary
                    ),
                    shape = RoundedCornerShape(topStart = 30.dp, bottomStart = 30.dp)
                ) {
                    Text("取消", style = TextStyle(color = appColors.bgPrimary))
                }
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onNavi()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = appColors.primary
                    ),
                    shape = RoundedCornerShape(topEnd = 30.dp, bottomEnd = 30.dp)
                ) {
                    Text("导航", style = TextStyle(color = appColors.bgPrimary))
                }
            }
        }
    }
}

fun routePlanTypeColor(
    selectedType: RoutePlanType,
    type: RoutePlanType,
    selectColor: Color,
    unselectColor: Color
): Color {
    return if (selectedType::class == type::class) selectColor else unselectColor
}

@Preview
@Composable
fun Tmp() {
    RoutePlan()
}