package com.xz.schoolnavinfo.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.presentation.LocalAppNavigator
import com.xz.schoolnavinfo.presentation.campus.CampusScreen
import com.xz.schoolnavinfo.presentation.common.components.BottomNavigationBar
import com.xz.schoolnavinfo.presentation.common.components.TitleIcon
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.map.MapScreen
import com.xz.schoolnavinfo.presentation.my.MyScreen
import com.xz.schoolnavinfo.presentation.theme.AppColors
import com.xz.schoolnavinfo.presentation.timetable.TimetableScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


private val menus = listOf(
    TitleIcon("地图", R.drawable.menu_map),
    TitleIcon("校园", R.drawable.menu_clarify),
    TitleIcon("课表", R.drawable.menu_calendar),
    TitleIcon("我的", R.drawable.menu_account)
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    commonViewModel: CommonViewModel
) {
    val pagerState = rememberPagerState(initialPage = 0) { menus.size }
    val appColors = AppColors.current
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        commonViewModel.homePageChange.collectLatest {
            pagerState.scrollToPage(it)
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
        BottomNavigationBar(
            menus = menus,
            startIndex = pagerState.currentPage,
            dividerColor = if (pagerState.currentPage != 0) appColors.bgLight else appColors.bgPrimary,
            selectedColor = appColors.primary,
            backgroundColor = appColors.bgPrimary,
            onIndexChange = { coroutineScope.launch { pagerState.scrollToPage(it) } }
        )
    }

}