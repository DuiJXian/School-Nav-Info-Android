package com.xz.schoolnavinfo.presentation.common.baidu.select

import android.view.Gravity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener
import com.baidu.mapapi.map.MapStatus
import com.baidu.mapapi.utils.DistanceUtil
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.utils.DensityUtil
import com.xz.schoolnavinfo.common.utils.LocationUtils
import com.xz.schoolnavinfo.common.utils.UnitCovertUtils
import com.xz.schoolnavinfo.presentation.common.baidu.map.MapViewScreen
import com.xz.schoolnavinfo.presentation.common.baidu.map.scrollMapView
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.map.composable.ScrollMapIcon
import com.xz.schoolnavinfo.presentation.theme.AppColors
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@Composable
fun MapLocationSelectScreen(
    commonViewModel: CommonViewModel,
    mapSelectViewModel: MapSelectViewModel = hiltViewModel(),
    onConfirm: (LocationState?) -> Unit
) {
    val context = LocalContext.current
    val mapVew = mapSelectViewModel.mapView
    val appColors = AppColors.current
    val poiInfoList by mapSelectViewModel.poiInfoList.collectAsState()
    val selectLocationInfo by mapSelectViewModel.mapSelectState

    var debounceJob: Job? = null
    var locateText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    val point = remember {
        runBlocking {
            LocationUtils.loadLocation(context)
        }
    }

    LaunchedEffect(poiInfoList) {
        if (poiInfoList.isNotEmpty()) {
            val poiInfo = poiInfoList[0]
            mapSelectViewModel.onStateChangeEvent(
                LocationState(
                    poiInfo.name,
                    poiInfo.address,
                    poiInfo.location
                )
            )
            locateText = poiInfo.name
        }
    }

    var outerBoxHeightPx by remember { mutableIntStateOf(0) }
    LaunchedEffect(true) {
        mapVew.map.scrollMapView(point, animate = false)
        mapSelectViewModel.getPoiInfoListEvent(point)
        mapVew.map.setOnMapStatusChangeListener(object : OnMapStatusChangeListener {
            override fun onMapStatusChangeStart(status: MapStatus?) {
            }
            override fun onMapStatusChangeStart(p0: MapStatus?, reason: Int) {
            }
            override fun onMapStatusChange(status: MapStatus?) {
                debounceJob?.cancel()
                debounceJob = scope.launch {
                    delay(200)
                    status?.let {
                        mapSelectViewModel.getPoiInfoListEvent(it.target)
                    }
                }
            }
            override fun onMapStatusChangeFinish(status: MapStatus?) {
            }

        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned {
                outerBoxHeightPx = it.size.height
            }
    ) {
        //地图显示区域
        Box(
            modifier = Modifier
                .height(DensityUtil.px2dip(context, (outerBoxHeightPx * 0.6).toFloat())),
            contentAlignment = Alignment.Center
        ) {
            MapViewScreen(
                mapView = mapVew
            )
            Icon(
                modifier = Modifier
                    .padding(bottom = DensityUtil.px2dip(context, 56f))
                    .size(DensityUtil.px2dip(context, 56f)),
                painter = painterResource(R.drawable.target),
                tint = appColors.primary,
                contentDescription = null
            )
        }
        ScrollMapIcon(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    bottom = DensityUtil.px2dip(
                        context,
                        (outerBoxHeightPx * 0.41).toFloat()
                    ), end = 10.dp
                )
                .clickable(
                    interactionSource = null,
                    indication = null
                ) {
                    mapVew.map.scrollMapView(point)
                }
        )

        //Poi检索区域
        Column(
            modifier = Modifier
                .background(appColors.bgPrimary)
                .height(DensityUtil.px2dip(context, (outerBoxHeightPx * 0.4).toFloat()))
                .padding(start = 10.dp, end = 10.dp)
                .align(Alignment.BottomCenter)
        ) {
            //地址框
            val locationAreaHeight = 40.dp
            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .height(locationAreaHeight)
                    .background(appColors.greyMedium.copy(.3f))
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(appColors.warn)
                        .height(locationAreaHeight)
                        .width(76.dp)
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            commonViewModel.onNavEvent(NavEvent.BackPage)
                            mapSelectViewModel.onStateChangeEvent(null)
                        }
                ) {
                    Text(
                        text = "取消",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = appColors.onButtonColor
                        )
                    )
                }
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .weight(1f)
                        .onFocusChanged {
                        }
                        .fillMaxWidth(),
                    value = locateText,
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    onValueChange = {
                        locateText = it

                        mapSelectViewModel.onStateChangeEvent(
                            LocationState(
                                it,
                                selectLocationInfo!!.address,
                                selectLocationInfo!!.location
                            )
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(appColors.primary)
                        .height(locationAreaHeight)
                        .width(76.dp)
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            if (locateText.isBlank()) {
                                scope.launch {
                                    StyleableToast.Builder(context)
                                        .text("位置名不能为空")
                                        .textColor(Color.White.toArgb())
                                        .backgroundColor(Color(0xFF0091EA).toArgb())
                                        .cornerRadius(36)
                                        .gravity(Gravity.TOP)
                                        .show()
                                }
                            } else {
                                commonViewModel.onNavEvent(NavEvent.BackPage)
                                onConfirm(mapSelectViewModel.mapSelectState.value)
                            }

                        }
                ) {
                    Text(
                        text = "确定",
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = appColors.onButtonColor
                        )
                    )
                }
            }

            //地址列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 10.dp, end = 10.dp)
                    .navigationBarsPadding()
            ) {
                items(poiInfoList) { item ->
                    Column(
                        modifier = Modifier
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) {
                                locateText = item.name
                                mapVew.map.scrollMapView(item.location)
                                mapSelectViewModel.onStateChangeEvent(
                                    LocationState(
                                        item.name,
                                        item.address,
                                        item.location
                                    )
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = item.name,
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = appColors.fontPrimary
                                    )
                                )
                                Text(
                                    text = item.address,
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = appColors.greyMedium
                                    )
                                )
                            }
                            Text(
                                text = UnitCovertUtils.metersToKilometers(
                                    DistanceUtil.getDistance(
                                        point,
                                        item.location
                                    ).toInt()
                                ),
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = appColors.fontPrimary
                                )
                            )
                        }
                        Spacer(
                            Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(appColors.greyMedium.copy(alpha = .3f))
                        )
                    }
                }
            }
        }
    }
}