package com.xz.schoolnavinfo.presentation.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.presentation.campus.CampusScreen
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.home.components.BottomNav
import com.xz.schoolnavinfo.presentation.map.MapScreen
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.flow.collectLatest


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel
) {
    val snackBarHostState = remember { SnackbarHostState() }
    val selectBtMenuIndex by homeViewModel.selectedBtMenuIndex
    val pagerState = rememberPagerState(initialPage = 1) { MenuItems.items.size }


    LaunchedEffect(selectBtMenuIndex) {
        pagerState.animateScrollToPage(selectBtMenuIndex)
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
                    0 -> MapScreen()
                    1 -> CampusScreen(commonViewModel)
                    2 -> Column { Text("我") }
                }
            }
            BottomNav()

        }
    }

}

