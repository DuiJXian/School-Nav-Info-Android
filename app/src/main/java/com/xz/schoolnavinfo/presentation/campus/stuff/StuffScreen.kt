package com.xz.schoolnavinfo.presentation.campus.stuff


import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.common.net.getImagesUrl
import com.xz.schoolnavinfo.common.utils.StringUtils
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.domain.data.dto.StuffDTO
import com.xz.schoolnavinfo.presentation.campus.CampusMenu
import com.xz.schoolnavinfo.presentation.common.components.CustomTextFiled
import com.xz.schoolnavinfo.presentation.common.components.VariableHeightTopBar
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.min

@Composable
fun StuffScreen(
    commonViewModel: CommonViewModel,
    stuffViewModel: StuffViewModel = hiltViewModel()
) {
    val stuffList by stuffViewModel.stuffs.collectAsStateWithLifecycle()
    val appColors = AppColors.current
    val gridState = rememberLazyGridState()
    val topHeight = 52.dp
    var searchText by remember { mutableStateOf("") }

    VariableHeightTopBar(
        scrollableState = gridState,
        barHeight = topHeight,
        backgroundColor = appColors.bgPrimary,
        topBar = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .height(topHeight)
                    .background(appColors.bgPrimary)
                    .padding(horizontal = 10.dp)
            ) {
                CustomTextFiled(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(shape = CircleShape)
                        .background(appColors.greyLight),
                    text = searchText,
                    leftSection = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(24.dp),
                            tint = appColors.greyMedium
                        )
                    },
                ) {
                    searchText = it
                    stuffViewModel.searchStuff(it)
                }
            }
        }
    ) {
        Box(Modifier.background(appColors.bgScreen)) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(calculateGridColumns()),
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                state = gridState,
                contentPadding = PaddingValues(10.dp)
            ) {
                items(stuffList.reversed()) { item ->
                    StuffCard(
                        stuffDTO = item,
                        onClick = {
                            if (item.stuff.id != null) {
                                commonViewModel.onNavEvent(NavEvent.StuffDetail(item.stuff.id))
                            }
                        }
                    ) {
                        commonViewModel.onRoutePlan(it)
                    }
                }
            }
        }
    }

    LaunchedEffect(true) {
        stuffViewModel.getStuff()
        commonViewModel.globalFlow.refreshDataFlow.collectLatest {
            if (it == CampusMenu.Stuff) {
                stuffViewModel.getStuff()
            }
        }
    }
}

@Composable
fun StuffCard(
    stuffDTO: StuffDTO,
    onClick: () -> Unit,
    onLocation: (String) -> Unit,
) {
    val appColors = AppColors.current
    val stuff = stuffDTO.stuff
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(appColors.bgPrimary)
            .clickable { onClick() }
    ) {
        Column {
            Image(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop,
                painter = rememberAsyncImagePainter(getImagesUrl(stuff.imageUrl)),
                contentDescription = null,
            )
            Spacer(Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(appColors.greyLight))
            Box(
                Modifier
                    .height(56.dp)
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp),
            ) {
                Text(
                    text = stuff.desc,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.fontPrimary,
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )
            }
            Row(
                Modifier
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(appColors.greyMedium.copy(.5f))
                    .clickable { onLocation(stuff.location) },
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
                Text(
                    modifier = Modifier
                        .padding(vertical = 5.dp)
                        .padding(end = 10.dp),
                    text = StringUtils.truncateText(stuff.address, 20),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.greyHeavy
                    )
                )
            }
            Spacer(Modifier.height(5.dp))
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

@Composable
fun calculateGridColumns(): Int {
    val configuration = LocalConfiguration.current
    val minDp = min(configuration.screenWidthDp, configuration.screenHeightDp)
    val isTablet = minDp >= 600
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    return when {
        !isTablet && isPortrait -> 2
        !isTablet && !isPortrait -> 3
        isTablet && isPortrait -> 3
        else -> 4
    }
}


private fun getTextByTypeAndStatus(type: Boolean, status: Boolean): String {
    if (status) return "已完成"
    return if (!type) "认领中" else "寻找中"
}
