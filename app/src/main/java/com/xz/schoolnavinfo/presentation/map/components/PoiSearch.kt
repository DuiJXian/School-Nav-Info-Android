package com.xz.schoolnavinfo.presentation.map.components

import AppColors
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baidu.mapapi.search.core.PoiInfo
import com.xz.schoolnavinfo.presentation.common.utils.LocateUtils
import com.xz.schoolnavinfo.presentation.map.PoiEvent
import com.xz.schoolnavinfo.presentation.map.MapViewModel

@Composable
fun PoiSearch(
    onTextChange: (text: String) -> Unit,
    onClose: () -> Unit,
    onClickItem: (poiInfo: PoiInfo) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 46.dp, start = 20.dp, end = 20.dp)
            .shadow(20.dp)
    ) {
        SearchTextField(
            onTextChange = onTextChange,
            onClose = onClose,
        )
        SearchResult(
            onClickItem = onClickItem
        )
    }
}

//搜索框
@Composable
fun SearchTextField(
    mapViewModel: MapViewModel = hiltViewModel(),
    onTextChange: (text: String) -> Unit,
    onClose: () -> Unit,
) {
    var customColors = AppColors.current
    val focusManager = LocalFocusManager.current

    val poiState by mapViewModel.poiState.collectAsState()


    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .onFocusChanged {
                mapViewModel.onPoiEvent(PoiEvent.FocusedChange(it.isFocused))
            },
        value = poiState.searchText,
        textStyle = TextStyle(
            fontSize = 16.sp,
            color = customColors.fontSecondary
        ),
        onValueChange = {
            onTextChange(it)
        },
        placeholder = {
            Text("搜地点", style = TextStyle(fontSize = 14.sp, color = customColors.unSelect))
        },
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        colors = TextFieldDefaults.colors(
            cursorColor = customColors.primary,
            focusedIndicatorColor = customColors.primary,
            unfocusedIndicatorColor = customColors.secondary,

            focusedContainerColor = customColors.bgSecondary,
            unfocusedContainerColor = customColors.bgSecondary
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray,
                modifier = Modifier.clickable {
                    focusManager.clearFocus()
                }
            )
        },
        trailingIcon = {
            if (poiState.searchText.isNotEmpty()) {
                IconButton(onClick = onClose) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear Icon",
                        tint = Color.Gray
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = androidx.compose.ui.text.input.ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onSearch = { },
            onDone = {
                focusManager.clearFocus()
            })
    )
}


//搜索结果
@Composable
fun SearchResult(
    onClickItem: (poiInfo: PoiInfo) -> Unit,
    mapViewModel: MapViewModel = hiltViewModel()
) {
    var appColors = AppColors.current
    val poiState by mapViewModel.poiState.collectAsState()
    val focusManager = LocalFocusManager.current
    if (poiState.poiInfoList.isNotEmpty() && poiState.isFocused) {
        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                .background(appColors.bgSecondary)
                .heightIn(max = 182.dp)
        ) {
            items(poiState.poiInfoList) { item ->
                Row(
                    verticalAlignment = CenterVertically,
                    modifier = Modifier
                        .height(36.dp)
                        .clickable {
                            onClickItem(item)
                            focusManager.clearFocus()
                        }
                ) {
                    Text(
                        item.name,
                        modifier = Modifier.padding(start = 20.dp),
                        style = TextStyle(color = appColors.fontSecondary)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        "${LocateUtils.metersToKilometers(item.distance)}km",
                        modifier = Modifier.padding(end = 20.dp),
                        style = TextStyle(color = appColors.fontSecondary)
                    )
                }
                if (item != poiState.poiInfoList.last()) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(0.5.dp)
                            .background(appColors.unSelectBorder)
                    )
                }

            }
        }


    }
}