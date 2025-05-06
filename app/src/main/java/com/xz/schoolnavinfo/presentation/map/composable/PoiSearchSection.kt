package com.xz.schoolnavinfo.presentation.map.composable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.baidu.mapapi.model.LatLng
import com.baidu.mapapi.search.core.PoiInfo
import com.baidu.mapapi.utils.DistanceUtil
import com.xz.schoolnavinfo.common.utils.UnitCovertUtils
import com.xz.schoolnavinfo.presentation.theme.AppColors


@Composable
fun PoiSearchSection(
    onTextChange: (String, LatLng) -> Unit,
    onClearSearchText: () -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onClickItem: (location:LatLng,uid: String) -> Unit,
    searchText: String,
    showTextField: Boolean,
    showSearchRes: Boolean,
    deviceLocation: LatLng,
    searchPoiInfos: List<PoiInfo>
) {
    val appColors = AppColors.current
    Column(
        modifier = Modifier
            .padding(vertical = 46.dp, horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, appColors.greyMedium.copy(.5f), RoundedCornerShape(16.dp))
    ) {
        if (showTextField){
            SearchTextField(
                searchText = searchText,
                onTextChange = {
                    onTextChange(it, deviceLocation)
                },
                onClearSearchText = {
                    onClearSearchText()
                },
                onFocusChange = {
                    onFocusChange(it)
                }
            )
        }

        AnimatedVisibility(
            visible = showSearchRes && searchPoiInfos.isNotEmpty(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            SearchResult(
                onClickItem = {
                    onClickItem(deviceLocation,it.uid)
                },
                poiInfoList = searchPoiInfos,
                centerPoint = deviceLocation
            )
        }
    }
}

//搜索框
@Composable
fun SearchTextField(
    searchText: String,
    onTextChange: (text: String) -> Unit,
    onClearSearchText: () -> Unit,
    onFocusChange: (Boolean)->Unit
) {
    val appColors = AppColors.current
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .onFocusChanged {
                onFocusChange(it.isFocused)
            },
        value = searchText,
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
                tint = appColors.greyHeavy,
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    focusManager.clearFocus()
                }
            )
        },
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                IconButton(onClick = onClearSearchText) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "Clear Icon",
                        tint = Color.Gray
                    )
                }
            }
        },
        singleLine = true
    )
}


//搜索结果
@Composable
fun SearchResult(
    onClickItem: (poiInfo: PoiInfo) -> Unit,
    poiInfoList: List<PoiInfo> = emptyList(),
    centerPoint: LatLng,
) {
    val appColors = AppColors.current

    val focusManager = LocalFocusManager.current
    if (poiInfoList.isNotEmpty()) {
        LazyColumn(
            modifier = Modifier
                .background(appColors.bgPrimary)
                .heightIn(max = 45.dp * 5)
        ) {
            items(poiInfoList) { poiInfo ->
                Column(
                    modifier = Modifier
                        .height(45.dp)
                        .clickable {
                            onClickItem(poiInfo)
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
                                poiInfo.name,
                                modifier = Modifier.padding(start = 20.dp),
                                style = TextStyle(
                                    color = appColors.fontSecondary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                poiInfo.address,
                                modifier = Modifier.padding(start = 20.dp),
                                style = TextStyle(
                                    color = appColors.greyMedium,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Text(
                            text = UnitCovertUtils.metersToKilometers(DistanceUtil.getDistance(poiInfo.location, centerPoint).toInt()),
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


    }
}
