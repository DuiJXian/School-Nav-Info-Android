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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.net.getImagesUrl
import com.xz.schoolnavinfo.common.utils.DensityUtil
import com.xz.schoolnavinfo.common.utils.TimeUtils
import com.xz.schoolnavinfo.domain.data.entity.Stuff
import com.xz.schoolnavinfo.domain.data.entity.UserInfo
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.ceil

@Composable
fun StuffDetailScreen(
    id: String,
    commonViewModel: CommonViewModel,
    stuffDetailViewModel: StuffDetailViewModel = hiltViewModel(),
) {
    val stuffDTO by stuffDetailViewModel.stuffDTO.collectAsStateWithLifecycle()
    val userInfo by commonViewModel.userInfo.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        stuffDetailViewModel.getStuffById(id)
    }

    StuffDetailContent(
        stuff = stuffDTO?.stuff,
        stuffUserInfo = stuffDTO?.userInfo,
        myUserInfo = userInfo,
        onBack = { commonViewModel.onNavEvent(NavEvent.BackPage) },
        onDelete = {
            stuffDetailViewModel.deleteStuff(id)
            commonViewModel.onNavEvent(NavEvent.BackPage)
        },
        onUpdateStatus = {
            stuffDetailViewModel.updateStatus(id)
            stuffDetailViewModel.getStuffById(id)
        }
    )
}

@Composable
fun StuffDetailContent(
    stuff: Stuff?,
    stuffUserInfo: UserInfo?,
    myUserInfo: UserInfo?,
    onDelete: () -> Unit,
    onUpdateStatus: () -> Unit,
    onBack: () -> Unit
) {
    if (stuff == null) {
        return
    }

    var dialogType by remember { mutableIntStateOf(1) } //0删除 1更新状态
    val appColors = AppColors.current
    val statusBarPadding = WindowInsets.systemBars.asPaddingValues()
    val (showDialog, updateShowDialog) = rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    DeleteDialog(
        isShow = showDialog,
        stuff = stuff,
        dialogType = dialogType,
        updateShow = updateShowDialog,
        onConfirm = {
            if (dialogType == 0) {
                onDelete()
            } else {
                onUpdateStatus()
            }
            coroutineScope.launch {
                delay(300)
                updateShowDialog(false)
            }
        }
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(appColors.bgPrimary)
            .padding(statusBarPadding)
    ) {
        //顶部
        StuffDetailTopBar(
            nickname = stuffUserInfo!!.nickname,
            avatarUrl = stuffUserInfo.avatarUrl,
            createTime = stuff.createTime,
            isMy = stuff.publisherId == myUserInfo!!.id,
            onBack = onBack,
            onDelete = {
                dialogType = 0
                updateShowDialog(true)
            },
            status = stuff.status,
            onFinish = {
                dialogType = 1
                updateShowDialog(true)
            }
        )

        StuffDetailBody(stuff)
    }
}

@Composable
fun StuffDetailBody(
    stuff: Stuff
) {
    val context = LocalContext.current
    var imageWidth by remember { mutableFloatStateOf(1000f) }
    var imageHeight by remember { mutableFloatStateOf(2000f) }
    val scrollState = rememberScrollState()
    val appColors = AppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
    ) {
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
        //地址
        Row(
            modifier = Modifier
                .padding(start = 10.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(appColors.greyMedium.copy(.5f)),
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
        Spacer(Modifier.height(10.dp))
        Row(Modifier.padding(horizontal = 10.dp)) {
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
        StuffProgress(
            stuffStatus = stuff.status,
            stuffType = stuff.type,
            happenTime = stuff.happenTime,
            createTime = stuff.createTime!!,
            finishTime = stuff.finishTime
        )
    }
}

@Composable
private fun StuffProgress(
    stuffStatus: Boolean,
    stuffType: Boolean,
    happenTime: String,
    createTime: String,
    finishTime: String?
) {

    val appColors = AppColors.current
    val barHeight = 20.dp
    val barWidth = 6.dp
    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp),
    ) {
        Row {
            Box(
                Modifier
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
            Spacer(Modifier.width(5.dp))
            Column {
                Text(
                    text = if (stuffType) "丢失" else "拾取",
                    style = TextStyle(
                        color = appColors.fontPrimary,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = happenTime,
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
                .width(barWidth)
                .height(barHeight)
                .background(appColors.primary)
        )
        //寻找or认领
        Row {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (stuffStatus) appColors.primary.copy(.3f) else appColors.primary),
                contentAlignment = Alignment.Center
            ) {
                if (stuffStatus) {
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
            Spacer(Modifier.width(5.dp))
            Column {
                Text(
                    text = if (stuffType) "寻找中" else "认领中",
                    style = TextStyle(
                        color = appColors.fontPrimary,
                        fontSize = 14.sp
                    )
                )
                Text(
                    text = createTime,
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
                .width(barWidth)
                .height(barHeight)
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
                    .background(if (stuffStatus) appColors.primary.copy(.3f) else appColors.greyLight)
                    .then(
                        if (!stuffStatus) Modifier.border(
                            1.dp,
                            appColors.primary.copy(.3f),
                            RoundedCornerShape(36.dp)
                        ) else Modifier
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (stuffStatus) {
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
            Spacer(Modifier.width(5.dp))
            Column {
                Text(
                    text = "完成",
                    style = TextStyle(
                        color = appColors.fontPrimary,
                        fontSize = 14.sp
                    )
                )
                if (finishTime != null) {
                    Text(
                        text = finishTime,
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

@Composable
fun StuffDetailTopBar(
    nickname: String,
    avatarUrl: String,
    createTime: String?,
    isMy: Boolean,
    status: Boolean,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onFinish: () -> Unit
) {
    val appColors = AppColors.current
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
                modifier = Modifier.clip(CircleShape).clickable  { onBack() },
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = appColors.fontPrimary
            )
            Image(
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(40.dp)
                    .clip(CircleShape),
                painter = if (avatarUrl.isBlank())
                    painterResource(R.drawable.heard_image) else
                    rememberAsyncImagePainter(getImagesUrl(avatarUrl)),
                contentDescription = "头像",
                contentScale = ContentScale.Crop
            )
            Column {
                Text(
                    modifier = Modifier
                        .padding(start = 5.dp),
                    text = nickname,
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = appColors.fontPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    modifier = Modifier.padding(start = 5.dp),
                    text = TimeUtils.formatTimeDifference(createTime!!),
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = appColors.greyMedium,
                    )
                )
            }
        }
        if (isMy) {
            Row(Modifier.padding(end = 10.dp)) {
                Box(
                    modifier = Modifier
                        .padding(end = 5.dp)
                        .shadow(3.dp, RoundedCornerShape(10.dp))
                        .background(appColors.err)
                        .clip(RoundedCornerShape(10.dp))
                        .width(60.dp)
                        .height(30.dp)
                        .clickable { onDelete() },
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
                if (!status) {
                    Box(
                        modifier = Modifier
                            .shadow(3.dp, RoundedCornerShape(10.dp))
                            .background(appColors.primary)
                            .clip(RoundedCornerShape(10.dp))
                            .width(60.dp)
                            .height(30.dp)
                            .clickable { onFinish() },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteDialog(
    isShow: Boolean,
    stuff: Stuff?,
    dialogType: Int,
    updateShow: (Boolean) -> Unit,
    onConfirm: () -> Unit
) {
    val appColors = AppColors.current
    if (isShow) {
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
                    val typeText = if (stuff!!.type) "已寻到" else "已被拾取"
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
                            updateShow(true)
                        }) {
                            Text(
                                text = "取消",
                                style = TextStyle(
                                    fontSize = 14.sp,
                                )
                            )
                        }
                        TextButton(onClick = onConfirm) {
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
}
