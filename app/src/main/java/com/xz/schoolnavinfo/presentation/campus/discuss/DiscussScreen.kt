package com.xz.schoolnavinfo.presentation.campus.discuss

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xz.schoolnavinfo.presentation.theme.AppColors

@Composable
fun DiscussScreen(
    discussViewModel: DiscussViewModel = hiltViewModel()
) {
    val articles by discussViewModel.articleInfo

    val appColors = AppColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
    ) {
        LazyColumn{
            items(articles){ item ->
                Box(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                ) {
                    DiscussCard(item)
                }
            }
        }
    }
}