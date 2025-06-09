package com.xz.schoolnavinfo.presentation.common.baidu.select

import android.util.Log
import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.baidu.mapapi.map.MapView
import com.baidu.mapapi.search.core.PoiInfo
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.utils.DensityUtil
import com.xz.schoolnavinfo.common.utils.JsonUtils
import com.xz.schoolnavinfo.common.utils.UnitCovertUtils
import com.xz.schoolnavinfo.presentation.LocalAppNavigator
import com.xz.schoolnavinfo.presentation.common.baidu.map.MapViewScreen
import com.xz.schoolnavinfo.presentation.common.components.ButtonType
import com.xz.schoolnavinfo.presentation.common.components.CustomTextFiled
import com.xz.schoolnavinfo.presentation.common.components.MyButton
import com.xz.schoolnavinfo.presentation.map.composable.ScrollMapIcon
import com.xz.schoolnavinfo.presentation.theme.AppColors


@Composable
fun LocationSelectScreen(
    locationSelectViewModel: LocationSelectViewModel = hiltViewModel(),
) {
    val navigator = LocalAppNavigator.current
    val uiState by locationSelectViewModel.uiState.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    if (uiState.centerLocation == null) return

    LocationSelectContent(
        mapView = locationSelectViewModel.mapView,
        poiInfos = uiState.poiInfos,
        selectedPoiInfo = uiState.selectedPoiInfo,
        searchText = uiState.searchText,
        onScrollMapView = { locationSelectViewModel.scrollMapViewToCenter() },
        onSelected = {
            locationSelectViewModel.setSelectedPoiInfo(it)
        },
        onCancel = {
            navigator.popBack()
        },
        onSearchTextChange = { locationSelectViewModel.setSearchText(it) },
        onConfirm = {
            if (uiState.selectedPoiInfo?.location != null) {
                navigator.saveLocationData(
                    JsonUtils.toJson(
                        LocationInfo(
                            name = uiState.selectedPoiInfo!!.name,
                            address = uiState.selectedPoiInfo!!.address,
                            location = uiState.selectedPoiInfo!!.location
                        )
                    )
                )
            }
            navigator.popBack()
        },
    )

    LaunchedEffect(Unit) {
        locationSelectViewModel.setMapStyle(isDark)
        locationSelectViewModel.scrollMapViewToCenter()
    }
}

@Composable
fun LocationSelectContent(
    mapView: MapView,
    poiInfos: List<PoiInfo>,
    selectedPoiInfo: PoiInfo?,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onScrollMapView: () -> Unit,
    onSelected: (PoiInfo) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    val systemPadding = WindowInsets.systemBars.asPaddingValues()
    val imeInsets = WindowInsets.ime
    val imeBottom = imeInsets.getBottom(LocalDensity.current)
    val offsetValue = if (imeBottom > 0) {
        (imeBottom - with(LocalDensity.current) {
            systemPadding.calculateBottomPadding().toPx()
        })
    } else 0f
    val animatedOffset by animateOffsetAsState(targetValue = Offset(0f, offsetValue))
    Column(
        modifier = Modifier
            .fillMaxSize()
            .offset { IntOffset(x = 0, y = -animatedOffset.y.toInt()) }
    ) {

        UpSection(
            modifier = Modifier.weight(.6f),
            mapView = mapView,
            scrollMapView = onScrollMapView,
            onCancel = onCancel,
            poiName = selectedPoiInfo?.name,
            onConfirm = onConfirm,
        )

        DownSection(
            modifier = Modifier.weight(.4f),
            poiInfos = poiInfos,
            searchText = searchText,
            selectedPoiInfo = selectedPoiInfo,
            onSelected = onSelected,
            onSearchTextChange = onSearchTextChange,
        )
    }
}

@Composable
private fun DownSection(
    modifier: Modifier,
    poiInfos: List<PoiInfo>?,
    selectedPoiInfo: PoiInfo?,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSelected: (PoiInfo) -> Unit,
) {
    val appColors = AppColors.current
    Column(
        modifier = modifier
            .background(appColors.bgPrimary)
    ) {
        if (poiInfos == null) {
            return
        }
        Spacer(
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(appColors.bgLight)
        )

        Spacer(Modifier.height(5.dp))

        Box(Modifier.padding(horizontal = 10.dp)) {
            CustomTextFiled(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clip(shape = CircleShape)
                    .background(appColors.bgLight),
                text = searchText,
                textColor = appColors.fontPrimary,
                brushColors = appColors.primary,
                leftSection = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(24.dp),
                        tint = appColors.greyMedium
                    )
                },
                onValueChange = {
                    onSearchTextChange(it)
                }
            )
        }
        Spacer(Modifier.height(5.dp))

        Spacer(
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(appColors.bgLight)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 10.dp)
        ) {
            items(poiInfos) { item ->
                Box(Modifier.clickable(interactionSource = null, indication = null) {
                    onSelected(item)
                }) {
                    AddressItem(item, selectedPoiInfo)
                }
            }
        }
    }
}

@Composable
private fun AddressItem(poiInfo: PoiInfo, selectedPoiInfo: PoiInfo?) {
    val appColors = AppColors.current
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = poiInfo.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.fontPrimary
                    )
                )
                Text(
                    text = "${UnitCovertUtils.metersToKilometers(poiInfo.distance)} | ${poiInfo.address}",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.greyMedium
                    )
                )
            }
            Box(
                Modifier
                    .fillMaxHeight()
                    .width(36.dp), contentAlignment = Alignment.CenterEnd
            ) {
                if (selectedPoiInfo != null && selectedPoiInfo == poiInfo)
                    Icon(
                        modifier = Modifier.size(36.dp),
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = appColors.primary
                    )
            }
        }
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(appColors.bgLight)
        )
    }
}

@Composable
private fun UpSection(
    modifier: Modifier,
    mapView: MapView,
    poiName: String?,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
    scrollMapView: () -> Unit
) {
    val context = LocalContext.current
    val appColors = AppColors.current
    val iconSizePx = remember { 72f }
    val topPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    var showMapScreen by remember { mutableStateOf(true) }
    Box(modifier) {
        if (showMapScreen) MapViewScreen(mapView = mapView)

        Icon(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = DensityUtil.pxToDip(context, iconSizePx))
                .size(DensityUtil.pxToDip(context, iconSizePx)),
            painter = painterResource(R.drawable.target),
            tint = appColors.primary,
            contentDescription = null
        )

        ScrollMapIcon(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 20.dp, end = 10.dp)
                .clickable(
                    interactionSource = null,
                    indication = null
                ) { scrollMapView() }
        )

        Row(Modifier.padding(start = 10.dp, end = 10.dp, top = topPadding + 10.dp)) {
            MyButton(text = "取消", type = ButtonType.WARNING) {
                showMapScreen = false
                onCancel()
            }
            Spacer(Modifier.weight(1f))
            MyButton(text = "确定") {
                onConfirm()
            }
        }

        if (poiName != null) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .align(Alignment.BottomCenter),
                text = poiName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = appColors.fontPrimary
                )
            )
        }
    }

}
