package com.xz.schoolnavinfo.presentation.campus

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.presentation.LocalAppNavigator
import com.xz.schoolnavinfo.presentation.Routes
import com.xz.schoolnavinfo.presentation.campus.activity.ActivityScreen
import com.xz.schoolnavinfo.presentation.campus.discuss.DiscussScreen
import com.xz.schoolnavinfo.presentation.campus.stuff.StuffScreen
import com.xz.schoolnavinfo.presentation.common.components.CustomTabRow
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.theme.AppColors

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "WrongNavigateRouteType")
@Composable
fun CampusScreen(
    commonViewModel: CommonViewModel
) {
    val tabTitles = listOf(CampusMenu.Activity, CampusMenu.Discuss, CampusMenu.Stuff)
    val pagerState = rememberPagerState(initialPage = 1) { tabTitles.size }
    val appColors = AppColors.current
    val userInfo by commonViewModel.userInfo.collectAsState()
    var currentPage by remember { mutableIntStateOf(0) }
    val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val navigator = LocalAppNavigator.current
    val lifecycle = LocalLifecycleOwner.current

    DisposableEffect(lifecycle) {
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
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
            if (pagerState.currentPage != 0 ||
                (pagerState.currentPage == 0 && userInfo.role == "ADMIN")
            ) {
                FloatingActionButton(
                    modifier = Modifier
                        .size(46.dp),
                    onClick = {
                        when (pagerState.currentPage) {
                            0 -> {
                                navigator.navigate(Routes.ArticlePublish(ArticleType.ACTIVITY))
                            }

                            1 -> {
                                navigator.navigate(Routes.ArticlePublish(ArticleType.DISCUSS))
                            }

                            2 -> {
                                navigator.navigate(Routes.StuffPublish)
                            }
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
            Spacer(
                Modifier
                    .fillMaxWidth()
                    .height(topPadding)
                    .background(appColors.bgPrimary)
            )
            CustomTabRow(
                tabWidth = 72.dp,
                backgroundColor = appColors.bgPrimary,
                startPage = pagerState.currentPage,
                pagerState = pagerState,
                tabs = tabTitles.mapIndexed { index, menu ->
                    {
                        Text(
                            menu.title,
                            style = if (index == currentPage) TextStyle(
                                color = appColors.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ) else TextStyle(
                                color = appColors.fontPrimary,
                                fontSize = 16.sp
                            )
                        )
                    }
                },
                indicator = {
                    Box(
                        Modifier
                            .width(36.dp)
                            .height(3.dp)
                            .clip(CircleShape)
                            .background(appColors.primary)
                    )
                },
                onPageChange = {
                    currentPage = it
                }
            )
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize(),
                state = pagerState,
                beyondViewportPageCount = 3,
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