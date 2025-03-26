package com.xz.schoolnavinfo.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.xz.schoolnavinfo.presentation.home.components.BottomNav
import com.xz.schoolnavinfo.presentation.map.MapScreen

@Composable
fun HomeScreen(
    viewmodel: HomeViewModel = hiltViewModel(),
) {
    Column(
        modifier = Modifier.navigationBarsPadding()
    ) {
        val state = viewmodel.homeState.collectAsState().value
        val pagerState = rememberPagerState(pageCount = { MenuItems.items.size })

        LaunchedEffect(state.selectedMenuIndex) {
            pagerState.animateScrollToPage(state.selectedMenuIndex)
        }
        //pager主页面
        HorizontalPager(
            pagerState,
            userScrollEnabled = false,
            beyondViewportPageCount = 3,
            modifier = Modifier
                .weight(1f)
                .background(Color.Red)

        ) { page ->
            when (page) {
                0 -> MapScreen()
                1 -> Column { Text("校圈") }
                2 -> Column { Text("我") }
            }
        }
        BottomNav()
    }


}