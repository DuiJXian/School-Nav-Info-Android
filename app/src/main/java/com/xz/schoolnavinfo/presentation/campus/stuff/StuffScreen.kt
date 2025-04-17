package com.xz.schoolnavinfo.presentation.campus.stuff

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.common.net.montageCompleteUrl
import com.xz.schoolnavinfo.common.utils.StringUtils
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.domain.data.dto.StuffDTO
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.flow.collectLatest

@Composable
fun StuffScreen(
    commonViewModel: CommonViewModel,
    stuffViewModel: StuffViewModel = hiltViewModel()
) {
    val stuffList = stuffViewModel.stuffList

    LaunchedEffect(true) {
        stuffViewModel.getStuff()
        commonViewModel.globalFlow.refreshDataFlow.collectLatest {
            if (it == CampusMenu.Stuff) {
                stuffViewModel.getStuff()
            }
        }
    }

    Box {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // 固定2列
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(stuffList.reversed()) { item ->
                StuffCard(item) {
                    if (item.stuff.id != null) {
                        commonViewModel.onNavEvent(NavEvent.StuffDetail(item.stuff.id))
                    }
                }
            }
        }
    }
}

@Composable
fun StuffCard(
    stuffDTO: StuffDTO,
    onClick: () -> Unit
) {
    val appColors = AppColors.current
    val stuff = stuffDTO.stuff
    Box(
        modifier = Modifier
            .shadow(3.dp, RoundedCornerShape(10.dp))
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(appColors.bgPrimary)
            .clickable {
                onClick()
            }
    ) {
        Column {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop,
                painter = rememberAsyncImagePainter(montageCompleteUrl(stuff.imageUrl)),
                contentDescription = null,
            )
            //描述
            Row(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)
            ) {
                Text(
                    text = stuff.desc,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.fontPrimary
                    )
                )
            }
            //地址
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(appColors.greyMedium.copy(.5f))
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {

                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .size(20.dp),
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = appColors.fontSecondary
                    )
                    Row {
                        Text(
                            modifier = Modifier
                                .padding(vertical = 5.dp)
                                .padding(end = 10.dp),
                            text = StringUtils.truncateText(stuff.address, 20),
                            maxLines = 1,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = appColors.greyHeavy
                            )
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val text = getTextByTypeAndStatus(stuff.type, stuff.status)
                Box(
                    modifier = Modifier
                        .border(1.dp, appColors.greyMedium.copy(.5f), RoundedCornerShape(5.dp))
                ) {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 5.dp, horizontal = 10.dp),
                        text = text,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (text) {
                                "认领中" -> appColors.info
                                "寻找中" -> appColors.err
                                else -> appColors.primary
                            }
                        )
                    )
                }
                Text(
                    text = if (stuff.createTime == null) "err" else TimeUtils.formatToMonthDayHourMinute(
                        stuff.createTime
                    ),
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.greyHeavy
                    )
                )
            }
        }

    }
}

private fun getTextByTypeAndStatus(type: Boolean, status: Boolean): String {
    if (status) return "已完成"
    return if (!type) "认领中" else "寻找中"
}
