package com.xz.schoolnavinfo.presentation.campus.stuff.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.net.getImagesUrl
import com.xz.schoolnavinfo.common.utils.DensityUtil
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StuffDetailScreen(
    id: String,
    commonViewModel: CommonViewModel,
    stuffDetailViewModel: StuffDetailViewModel = hiltViewModel(),
) {
    val statusBarPadding = WindowInsets.systemBars.asPaddingValues()
    val appColors = AppColors.current
    val stuff = stuffDetailViewModel.stuffDTO?.stuff
    val userInfo = stuffDetailViewModel.stuffDTO?.userInfo
    val myUserInfo by commonViewModel.userInfo.collectAsState()
    val context = LocalContext.current


    var imageWidth by remember { mutableFloatStateOf(1000f) }
    var imageHeight by remember { mutableFloatStateOf(2000f) }

    val scrollState = rememberScrollState()

    var showDialog by remember { mutableStateOf(false) }
    var dialogType by remember { mutableIntStateOf(1) } //0删除 1更新状态

    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        stuffDetailViewModel.getStuffById(id)
    }

    if (stuff == null) return
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.bgPrimary)
            .padding(statusBarPadding)
    ) {
        if (showDialog) {
            BasicAlertDialog(onDismissRequest = {}) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    tonalElevation = 8.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .width(280.dp)
                    ) {
                        Text(
                            text = "提醒",
                            style = TextStyle(
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = appColors.primary
                            )
                        )
                        Spacer(Modifier.height(8.dp))
                        val typeText = if (stuff.type) "已寻到" else "已被拾取"
                        val dialogContent = if (dialogType == 0) "确认删除" else "确认$typeText"
                        Text(
                            text = dialogContent,
                            style = TextStyle(
                                fontSize = 16.sp,
                            )

                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = {
                                showDialog = false
                            }) {
                                Text(
                                    text = "取消",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                    )
                                )
                            }
                            TextButton(onClick = {
                                if (dialogType == 0) {
                                    stuffDetailViewModel.deleteStuff(id)
                                    commonViewModel.onNavEvent(NavEvent.BackPage)
                                } else {
                                    stuffDetailViewModel.updateStatus(id)
                                    stuffDetailViewModel.getStuffById(id);
                                }
                                scope.launch {
                                    delay(300)
                                    showDialog = false
                                }
                            }) {
                                Text(
                                    text = "确定",
                                    style = TextStyle(
                                        fontSize = 14.sp,
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            //顶部
            if (userInfo?.id != null && userInfo.id == stuff.publisherId) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            modifier = Modifier.clickable(
                                indication = null,
                                interactionSource = null
                            ) {
                                commonViewModel.onNavEvent(NavEvent.BackPage)
                            },
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = appColors.fontPrimary
                        )
                        Image(
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(40.dp)
                                .clip(CircleShape),
                            painter = if (userInfo.avatarUrl.isNullOrBlank())
                                painterResource(R.drawable.heard_image) else
                                rememberAsyncImagePainter(getImagesUrl(userInfo.avatarUrl)),
                            contentDescription = "头像",
                            contentScale = ContentScale.Crop
                        )
                        Column {
                            Text(
                                modifier = Modifier
                                    .padding(start = 5.dp),
                                text = userInfo.nickname,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = appColors.fontPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                modifier = Modifier
                                    .padding(start = 5.dp),
                                text = if (stuff.createTime != null)
                                    TimeUtils.formatTimeDifference(stuff.createTime) else
                                    "err",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    color = appColors.greyMedium,
                                )
                            )
                        }
                    }
                    if (stuff.publisherId == myUserInfo.id){
                        Row(Modifier.padding(end = 10.dp)) {
                            Box(
                                modifier = Modifier
                                    .padding(end = 5.dp)
                                    .shadow(3.dp, RoundedCornerShape(10.dp))
                                    .background(appColors.err)
                                    .clip(RoundedCornerShape(10.dp))
                                    .width(60.dp)
                                    .height(30.dp)
                                    .clickable {
                                        if (stuff.id != null) {
                                            dialogType = 0
                                            showDialog = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "删除",
                                    style = TextStyle(
                                        color = appColors.onButtonColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            if (!stuff.status) {
                                Box(
                                    modifier = Modifier
                                        .shadow(3.dp, RoundedCornerShape(10.dp))
                                        .background(appColors.primary)
                                        .clip(RoundedCornerShape(10.dp))
                                        .width(60.dp)
                                        .height(30.dp)
                                        .clickable {
                                            if (stuff.id != null) {
                                                dialogType = 1
                                                showDialog = true
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "完成",
                                        style = TextStyle(
                                            color = appColors.onButtonColor,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
            //图片
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 300.dp)
                    .padding(10.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(imageWidth.dp, imageHeight.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(getImagesUrl(stuff.imageUrl))
                            .listener(
                                onSuccess = { _, result ->
                                    val drawable = result.drawable
                                    val width = ceil(
                                        DensityUtil.px2dip(
                                            context,
                                            drawable.intrinsicWidth.toFloat()
                                        ).value
                                    )
                                    val height = (DensityUtil.px2dip(
                                        context,
                                        drawable.intrinsicHeight.toFloat()
                                    ).value)
                                    imageWidth = width
                                    imageHeight = height
                                }
                            )
                            .build()
                    ),
                    contentDescription = null
                )
            }
            Row(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(appColors.greyMedium.copy(.5f))
                    .clickable(
                        interactionSource = null,
                        indication = null
                    ) {

                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .size(20.dp),
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = appColors.fontSecondary
                )
                Row {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .padding(end = 10.dp),
                        text = stuff.address,
                        maxLines = 1,
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = appColors.greyHeavy
                        )
                    )
                }
            }
            //描述
            Row(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp)
            ) {
                Text(
                    text = stuff.desc,
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = appColors.fontPrimary
                    )
                )
            }
            //状态
            Column(
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 10.dp),
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(appColors.primary.copy(.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null,
                            tint = appColors.primary
                        )
                    }
                    Column {
                        Text(
                            text = if (stuff.type) "丢失" else "拾取",
                            style = TextStyle(
                                color = appColors.fontPrimary,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = stuff.happenTime,
                            style = TextStyle(
                                color = appColors.greyHeavy,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .width(6.dp)
                        .height(16.dp)
                        .background(appColors.primary)
                )
                //寻找or认领
                Row {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (stuff.status) appColors.primary.copy(.3f) else appColors.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        if (stuff.status) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                tint = appColors.primary
                            )
                        } else {
                            Text(
                                text = "2",
                                style = TextStyle(
                                    color = appColors.bgScreen,
                                    fontSize = 16.sp
                                )
                            )
                        }

                    }
                    Column {
                        Text(
                            text = if (stuff.type) "寻找中" else "认领中",
                            style = TextStyle(
                                color = appColors.fontPrimary,
                                fontSize = 14.sp
                            )
                        )
                        Text(
                            text = stuff.createTime ?: "err",
                            style = TextStyle(
                                color = appColors.greyHeavy,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .padding(start = 15.dp)
                        .clip(RoundedCornerShape(1.dp))
                        .width(6.dp)
                        .height(16.dp)
                        .background(appColors.primary)
                )
                //完成
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (stuff.status) appColors.primary.copy(.3f) else appColors.greyLight)
                            .then(
                                if (!stuff.status) Modifier.border(
                                    1.dp,
                                    appColors.primary.copy(.3f),
                                    RoundedCornerShape(36.dp)
                                ) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (stuff.status) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = null,
                                tint = appColors.primary
                            )
                        } else {
                            Text(
                                text = "3",
                                style = TextStyle(
                                    color = appColors.greyHeavy,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "完成",
                            style = TextStyle(
                                color = appColors.fontPrimary,
                                fontSize = 14.sp
                            )
                        )
                        if (stuff.finishTime != null) {
                            Text(
                                text = stuff.finishTime,
                                style = TextStyle(
                                    color = appColors.greyHeavy,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}