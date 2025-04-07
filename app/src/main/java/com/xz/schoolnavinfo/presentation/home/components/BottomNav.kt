package com.xz.schoolnavinfo.presentation.home.components

import com.xz.schoolnavinfo.presentation.theme.AppColors
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xz.schoolnavinfo.presentation.home.HomeEvent
import com.xz.schoolnavinfo.presentation.home.HomeViewModel
import com.xz.schoolnavinfo.presentation.home.MenuItems

@Composable
fun BottomNav(
    modifier: Modifier = Modifier,
) {
    val currentColor = AppColors.current
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
            .fillMaxWidth()
            .background(currentColor.bgPrimary)
    )
    {
        for (item in MenuItems.items) {
            BNTabItem(
                modifier = Modifier.weight(1f),
                menuItem = item,

                )
        }
    }
}

@Composable
fun BNTabItem(
    homeViewModel: HomeViewModel = hiltViewModel(),
    modifier: Modifier,
    menuItem: MenuItems.MenuItem,
) {
    val currentColor = AppColors.current
    val selectedIndex by homeViewModel.selectedBtMenuIndex
    val color =
        if (selectedIndex == menuItem.index) currentColor.primary else currentColor.greyMedium
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                homeViewModel.onEvent(HomeEvent.ChangeBTMenu(menuItem.index))
            }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 5.dp)
        ) {
            Image(
                painter = painterResource(menuItem.iconRes),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color)
            )
            Text(text = menuItem.name, color = color)
        }
    }
}