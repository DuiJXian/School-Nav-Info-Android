package com.xz.schoolnavinfo.presentation.campus

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.xz.schoolnavinfo.presentation.campus.activity.ActivityScreen
import com.xz.schoolnavinfo.presentation.campus.discuss.DiscussScreen
import com.xz.schoolnavinfo.presentation.campus.stuff.StuffScreen
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CampusScreen(
    commonViewModel: CommonViewModel
) {
    val tabTitles = listOf(CampusMenu.Activity, CampusMenu.Discuss, CampusMenu.Stuff)
    val pagerState = rememberPagerState(initialPage = 1) { tabTitles.size }
    val coroutineScope = rememberCoroutineScope()
    val appColors = AppColors.current
    val userInfo by commonViewModel.userInfo.collectAsState()

    val lifecycle = LocalLifecycleOwner.current

    val systemPadding = WindowInsets.systemBars

    DisposableEffect(lifecycle) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    Log.e("TAG", "CampusScreen: onGetUserInfoEvent")
                    commonViewModel.onGetUserInfoEvent()
                }
                else -> Unit
            }
        }
        lifecycle.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (pagerState.currentPage == 1 ||
                pagerState.currentPage == 2 ||
                (pagerState.currentPage == 0 && userInfo.role == "ADMIN")
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .size(46.dp),
                    onClick = {
                        if (pagerState.currentPage == 0 || pagerState.currentPage ==1){
                            commonViewModel.onNavEvent(NavEvent.PublishArticle(tabTitles[pagerState.currentPage]))
                        }else{
                            commonViewModel.onNavEvent(NavEvent.PublishStuff)
                        }
                    },
                    shape = CircleShape,
                    containerColor = appColors.bgPrimary
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp),
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Article",
                        tint = appColors.primary
                    )
                }
            }
        }

    ) {
        Column(
            modifier = Modifier
                .background(appColors.bgScreen)
                .fillMaxSize()
                .widthIn(max = 540.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(appColors.bgPrimary)
                    .padding(top = 20.dp)
                    .zIndex(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TabRow(
                    modifier = Modifier
                        .width(200.dp),
                    containerColor = appColors.bgPrimary,
                    selectedTabIndex = pagerState.currentPage,
                    indicator = { tabPositions ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(35.dp)
                                    .height(3.dp)
                                    .background(appColors.primary, shape = RoundedCornerShape(3.dp))
                            )
                        }

                    },
                    divider = {}

                ) {
                    tabTitles.forEachIndexed { index, menu ->
                        var textColor = appColors.fontSecondary
                        var fontWeight = FontWeight.Normal
                        if (pagerState.currentPage == index) {
                            textColor = appColors.primary
                            fontWeight = FontWeight.Bold
                        }
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            },
                            text = {
                                Text(
                                    modifier = Modifier
                                        .offset(y = (10).dp),
                                    text = menu.title,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = fontWeight,
                                        color = textColor
                                    )
                                )
                            }
                        )
                    }
                }
            }


            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize(),
                state = pagerState,
                beyondViewportPageCount = 2,
            ) { page ->
                when (page) {
                    0 -> ActivityScreen(commonViewModel = commonViewModel)
                    1 -> DiscussScreen(commonViewModel = commonViewModel)
                    2 -> StuffScreen(commonViewModel = commonViewModel)
                }
            }
        }
    }
}