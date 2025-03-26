package com.xz.schoolnavinfo.presentation.map.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Preview
@Composable
fun StartPre() {
    StarRatingCanvas(4.3)
}

@Composable
fun StarRatingCanvas(
    rating: Double, // 评分 0~5，支持任意小数
    maxRating: Int = 5,
    starSize: Dp = 16.dp,
    starColor: Color = Color(0xFFFFA000),
    unSelectStarColor: Color = Color(0xFFBDBDBD)
) {
    val density = LocalDensity.current
    val pxSize = with(density) { starSize.toPx() }
    Row {
        for (i in 1..maxRating) {
            Box {
                val ratio = when {
                    i <= rating -> 1f // 完整填充
                    i - 1 < rating -> rating - (i - 1) // 计算剩余填充比例
                    else -> 0f // 不填充
                }
                Canvas(modifier = Modifier.size(starSize)) {
                    val centerX = pxSize / 2
                    val centerY = pxSize / 2
                    val radius = pxSize / 2 // 五角星的外接圆半径

                    val path = Path()

                    // 极坐标下计算五角星的五个顶点
                    val points = mutableListOf<Offset>()
                    for (i in 0 until 5) {
                        val angle = (i * 2 * PI / 5) - PI / 2 // 五角星的每个顶点角度
                        val x = centerX + radius * cos(angle)
                        val y = centerY + radius * sin(angle)
                        points.add(Offset(x.toFloat(), y.toFloat()))
                    }

                    // 绘制五角星
                    path.moveTo(points[0].x, points[0].y)
                    path.lineTo(points[2].x, points[2].y)
                    path.lineTo(points[4].x, points[4].y)
                    path.lineTo(points[1].x, points[1].y)
                    path.lineTo(points[3].x, points[3].y)
                    path.close()

                    // 绘制路径
                    drawPath(
                        path = path,
                        color = unSelectStarColor,
                    )

                    clipRect(right = (size.width * ratio.toFloat())) {
                        drawPath(path = path, color = starColor)
                    }
                }
            }
        }
    }
}

