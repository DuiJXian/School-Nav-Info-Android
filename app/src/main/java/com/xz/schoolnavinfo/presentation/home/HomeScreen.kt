package com.xz.schoolnavinfo.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xz.schoolnavinfo.presentation.campus.CampusScreen
import com.xz.schoolnavinfo.presentation.common.Screen
import com.xz.schoolnavinfo.presentation.home.components.BottomNav
import com.xz.schoolnavinfo.presentation.map.MapScreen
import kotlinx.coroutines.flow.collectLatest


@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    Column(
        modifier = Modifier.navigationBarsPadding()
    ) {
        val selectBtMenuIndex by homeViewModel.selectedBtMenuIndex
        val pagerState = rememberPagerState(initialPage = 1) { MenuItems.items.size }

        LaunchedEffect(selectBtMenuIndex) {
            pagerState.animateScrollToPage(selectBtMenuIndex)
        }
//        LaunchedEffect(true) {
//            homeViewModel.networkFlow.collectLatest {
//                if (it.contains("401")) {
//                    navController.navigate(Screen.Login.route) {
//                        popUpTo(Screen.Home.route) { inclusive = true }
//                    }
//                }
//            }
//        }
        //pager主页面
        HorizontalPager(
            pagerState,
            userScrollEnabled = false,
            beyondViewportPageCount = 3,
            modifier = Modifier
                .weight(1f)

        ) { page ->
            when (page) {
                0 -> MapScreen()
                1 -> CampusScreen()
                2 -> Column { Text("我") }
            }
        }
        BottomNav()

    }


}