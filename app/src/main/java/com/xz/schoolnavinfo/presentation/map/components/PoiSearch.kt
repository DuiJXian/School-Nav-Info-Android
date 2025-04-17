package com.xz.schoolnavinfo.presentation.map.components

import com.xz.schoolnavinfo.presentation.theme.AppColors
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baidu.mapapi.search.core.PoiInfo
import com.xz.schoolnavinfo.common.utils.LocateUtils
import com.xz.schoolnavinfo.presentation.map.PoiEvent
import com.xz.schoolnavinfo.presentation.map.MapViewModel

@Composable
fun PoiSearch(
    onTextChange: (String) -> Unit,
    onClose: () -> Unit,
    onClickItem: (PoiInfo) -> Unit
) {
    var isShowSearchPoi by remember { mutableStateOf(false) }
    val corner = 20.dp
    Column(
        modifier = Modifier
            .padding(vertical = 46.dp, horizontal = 20.dp)
            .shadow(5.dp, RoundedCornerShape(corner))
    ) {
        SearchTextField(
            corner = corner,
            onTextChange = onTextChange,
            onClose = onClose,
            isShowSearchPoi = isShowSearchPoi
        )
        SearchResult(
            corner = corner,
            onClickItem = onClickItem,
            isShowSearchPoi = {
                isShowSearchPoi = it
            }
        )
    }
}

//搜索框
@Composable
fun SearchTextField(
    mapViewModel: MapViewModel = hiltViewModel(),
    isShowSearchPoi: Boolean = false,
    corner: Dp,
    onTextChange: (text: String) -> Unit,
    onClose: () -> Unit,
) {
    val appColors = AppColors.current
    val focusManager = LocalFocusManager.current

    val poiState by mapViewModel.poiState.collectAsState()

    val shape = if (isShowSearchPoi) RoundedCornerShape(
        topStart = corner,
        topEnd = corner
    ) else RoundedCornerShape(corner)

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .onFocusChanged {
                mapViewModel.onPoiEvent(PoiEvent.FocusedChange(it.isFocused))
            },
        value = poiState.searchText,
        textStyle = TextStyle(
            color = appColors.fontPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        ),
        onValueChange = {
            onTextChange(it)
        },
        placeholder = {
            Text("搜地点", style = TextStyle(color = appColors.fontSecondary))
        },
        shape = shape,
        colors = TextFieldDefaults.colors(
            cursorColor = appColors.primary,

            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,

            unfocusedContainerColor = appColors.bgPrimary,
            focusedContainerColor = appColors.bgPrimary,
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = "Search Icon",
                tint = Color.Gray,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
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
    mapViewModel: MapViewModel = hiltViewModel(),
    corner: Dp,
    isShowSearchPoi: (Boolean) -> Unit,
) {
    val appColors = AppColors.current
    val poiState by mapViewModel.poiState.collectAsState()
    val focusManager = LocalFocusManager.current
    if (poiState.poiInfoList.isNotEmpty() && poiState.isFocused) {
        isShowSearchPoi(true)
        LazyColumn(
            modifier = Modifier
                .clip(RoundedCornerShape(bottomStart = corner, bottomEnd = corner))
                .background(appColors.bgPrimary)
                .heightIn(max = 45.dp * 5)
        ) {
            items(poiState.poiInfoList) { item ->
                Column(
                    modifier = Modifier
                        .height(45.dp)
                        .clickable {
                            onClickItem(item)
                            focusManager.clearFocus()
                        }
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(appColors.greyMedium.copy(.3f))
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        verticalAlignment = CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                item.name,
                                modifier = Modifier.padding(start = 20.dp),
                                style = TextStyle(
                                    color = appColors.fontSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                item.address,
                                modifier = Modifier.padding(start = 20.dp),
                                style = TextStyle(
                                    color = appColors.greyMedium,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Text(
                            text = LocateUtils.metersToKilometers(item.distance),
                            modifier = Modifier.padding(end = 20.dp),
                            style = TextStyle(
                                color = appColors.fontSecondary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }


    } else {
        isShowSearchPoi(false)
    }
}
