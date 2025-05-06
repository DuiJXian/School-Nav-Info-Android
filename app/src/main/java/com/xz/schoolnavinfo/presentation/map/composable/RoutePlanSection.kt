package com.xz.schoolnavinfo.presentation.map.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import com.xz.schoolnavinfo.presentation.theme.AppColors
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.common.utils.UnitCovertUtils
import com.xz.schoolnavinfo.presentation.map.RouteType

private val shapeList = listOf(
    RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
    RoundedCornerShape(0.dp),
    RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
)

private val routeTypeIcon = listOf(
    R.drawable.directions_walk_24px,
    R.drawable.directions_bike_24px,
    R.drawable.directions_car_24px
)

@Composable
fun RoutePlan(
    modifier: Modifier = Modifier,
    visible: Boolean,
    onRouteTypeChange: (routeType: RouteType) -> Unit = {},
    distance: String = "0",
    duration: String = "0",
    routeType: RouteType = RouteType.Walking,
    onCancel: () -> Unit = {},
    onNavi: () -> Unit = {},
) {
    val appColors = AppColors.current
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(46.dp))

                RoutePlanRouteTypeButtons(onRouteTypeChange, routeType)

                Text(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(appColors.greyMedium.copy(alpha = 0.3f))
                        .padding(horizontal = 10.dp, vertical = 5.dp),
                    text = "全程${UnitCovertUtils.metersToKilometers(distance.toInt())}千米 约${
                        TimeUtils.formatTime(
                            duration.toInt()
                        )
                    }",
                    style = TextStyle(
                        color = appColors.fontPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                )

            }

            RoutePlanSelectButtons(
                modifier = Modifier.align(Alignment.BottomCenter),
                onCancel = onCancel,
                onNavi = onNavi
            )

        }
    }

}

@Composable
fun RoutePlanRouteTypeButtons(
    onRouteTypeChange: (routeType: RouteType) -> Unit,
    routeType: RouteType
) {
    val appColors = AppColors.current
    Row {
        for ((index, value) in RouteType.entries.withIndex()) {
            Button(
                modifier = Modifier.width(100.dp),
                onClick = { onRouteTypeChange(value) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (routeType == value)
                        appColors.primary else appColors.greyHeavy
                ),
                shape = shapeList[index]
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        modifier = Modifier.size(18.dp),
                        painter = painterResource(routeTypeIcon[index]),
                        contentDescription = value.title
                    )
                    Text(value.title, style = TextStyle(color = appColors.bgPrimary))
                }

            }
        }
    }
}

@Composable
fun RoutePlanSelectButtons(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onNavi: () -> Unit
) {
    val appColors = AppColors.current
    Row(modifier) {
        Button(
            modifier = Modifier.width(100.dp),
            onClick = { onCancel() },
            colors = ButtonDefaults.buttonColors(containerColor = appColors.warn),
            shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    imageVector = Icons.Default.Close,
                    contentDescription = "取消"
                )
                Text("取消", style = TextStyle(color = appColors.bgPrimary))
            }
        }
        Button(
            modifier = Modifier.width(100.dp),
            onClick = { onNavi() },
            colors = ButtonDefaults.buttonColors(containerColor = appColors.primary),
            shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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