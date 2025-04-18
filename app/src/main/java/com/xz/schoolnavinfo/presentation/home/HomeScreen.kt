package com.xz.schoolnavinfo.presentation.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.xz.schoolnavinfo.presentation.campus.CampusScreen
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.home.components.BottomNav
import com.xz.schoolnavinfo.presentation.map.MapScreen
import com.xz.schoolnavinfo.presentation.my.MyScreen
import kotlinx.coroutines.flow.collectLatest


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val selectBtMenuIndex by homeViewModel.selectedBtMenuIndex
    val pagerState = rememberPagerState(initialPage = 0) { HomeMenuItems.items.size }


    LaunchedEffect(selectBtMenuIndex) {
        pagerState.animateScrollToPage(selectBtMenuIndex)
    }

    LaunchedEffect(Unit) {
        commonViewModel.homePageFlow.collectLatest {
            pagerState.scrollToPage(it)
            homeViewModel.changeBTMenu(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        Column(
            modifier = Modifier.navigationBarsPadding()
        ) {
            //pager主页面
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
                    2 -> MyScreen(commonViewModel = commonViewModel)
                }
            }
            BottomNav(homeViewModel = homeViewModel)
        }
    }

}

