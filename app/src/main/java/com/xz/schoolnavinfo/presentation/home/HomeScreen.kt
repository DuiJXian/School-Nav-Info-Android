package com.xz.schoolnavinfo.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.presentation.campus.CampusScreen
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.map.MapScreen
import com.xz.schoolnavinfo.presentation.my.MyScreen
import com.xz.schoolnavinfo.presentation.theme.AppColors
import com.xz.schoolnavinfo.presentation.timetable.TimetableScreen
import kotlinx.coroutines.flow.collectLatest

data class MenuItem(val name: String, val iconRes: Int)

private val items = listOf(
    MenuItem("地图", R.drawable.map_24px),
    MenuItem("校园", R.drawable.newsmode_24px),
    MenuItem("课表", R.drawable.baseline_calendar_month_24),
    MenuItem("我", R.drawable.person_24px)
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    commonViewModel: CommonViewModel
) {
    val pagerState = rememberPagerState(initialPage = 0) { items.size }

    val (currentMenu, updateMenu) = rememberSaveable {
        mutableStateOf(items.first().name)
    }

    LaunchedEffect(currentMenu) {
        pagerState.animateScrollToPage(items.map { it.name }.toList().indexOf(currentMenu))
    }

    LaunchedEffect(Unit) {
        commonViewModel.homePageChange.collectLatest {
            updateMenu(items[it].name)
        }
    }

    Column(
        modifier = Modifier.navigationBarsPadding()
    ) {
        HorizontalPager(
            pagerState,
            userScrollEnabled = false,
            beyondViewportPageCount = 3,
            modifier = Modifier
                .weight(1f)

        ) { page ->
            when (page) {
                0 -> MapScreen(commonViewModel = commonViewModel)
                1 -> CampusScreen(commonViewModel)
                2 -> TimetableScreen()
                3 -> MyScreen(commonViewModel = commonViewModel)
            }
        }
        BottomNav(currentMenu, updateMenu)
    }

}

@Composable
fun BottomNav(
    currentTitle: String,
    onChange: (String) -> Unit
) {
    val currentColor = AppColors.current
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .background(currentColor.bgPrimary)
    )
    {
        for (item in items) {
            BNTabItem(
                modifier = Modifier.weight(1f),
                item = item,
                currentTitle = currentTitle,
                onChange = onChange
            )
        }
    }
}

@Composable
fun BNTabItem(
    modifier: Modifier = Modifier,
    item: MenuItem,
    currentTitle: String,
    onChange: (String) -> Unit
) {
    val currentColor = AppColors.current
    val color =
        if (item.name == currentTitle) currentColor.primary else currentColor.greyMedium
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                interactionSource = null,
                indication = null
            ) {
                onChange(item.name)
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Image(
                painter = painterResource(item.iconRes),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color)
            )
            Text(text = item.name, color = color)
        }
    }
}

