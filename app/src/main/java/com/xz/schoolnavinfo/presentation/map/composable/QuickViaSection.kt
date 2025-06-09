package com.xz.schoolnavinfo.presentation.map.composable

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.utils.StringUtils
import com.xz.schoolnavinfo.domain.data.entity.LocalPoiInfo
import com.xz.schoolnavinfo.presentation.common.components.CustomTextFiled
import com.xz.schoolnavinfo.presentation.common.components.shake
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt


@Composable
fun QuickViaSection(
    showQuickBar: Boolean,
    showQuickEdit: Boolean,
    localPoiInfos: List<LocalPoiInfo>,
    localPoiInfo: LocalPoiInfo?,
    onClickItem: (uid: String) -> Unit,
    onLongClickItem: (uid: String) -> Unit,
    onDelete: () -> Unit,
    onConfirm: (LocalPoiInfo) -> Unit,
    onCancel: () -> Unit
) {
    if (!showQuickBar) {
        return
    }
    val bottomBoxHeightDp = 76.dp
    val bottomBoxHeightPx = with(LocalDensity.current) { bottomBoxHeightDp.toPx() }
    Box(Modifier.fillMaxSize()) {
        QuickViaDraggableBar(
            Modifier.align(Alignment.BottomCenter),
            bottomBoxHeightPx = bottomBoxHeightPx
        ) {
            QuickViaBarPoiSection(
                localPoiInfos = localPoiInfos,
                onClickItem = onClickItem,
                bottomBoxHeightDp = bottomBoxHeightDp,
                onLongClickItem = onLongClickItem
            )
        }

        QuickViaBarPoiEdit(
            modifier = Modifier.align(Alignment.Center),
            visible = showQuickEdit,
            localPoiInfo = localPoiInfo,
            onDelete = onDelete,
            onConfirm = onConfirm,
            onCancel = onCancel
        )
    }

}

@Composable
fun QuickViaDraggableBar(
    modifier: Modifier,
    bottomBoxHeightPx: Float,
    itemSection: @Composable () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val appColors = AppColors.current

    val animateOffset = remember { Animatable(0f) }
    val decay = rememberSplineBasedDecay<Float>()

    val draggableState = rememberDraggableState {
        coroutineScope.launch {
            val offsetY = (animateOffset.value + it).coerceIn(0f, bottomBoxHeightPx)
            animateOffset.snapTo(offsetY)
        }
    }
    Column(
        modifier
            .draggable(
                orientation = Orientation.Vertical,
                state = draggableState,
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val overOffset = decay.calculateTargetValue(animateOffset.value, velocity)
                        val finalPosition = if (overOffset < bottomBoxHeightPx / 2) 0f
                        else bottomBoxHeightPx
                        animateOffset.animateTo(
                            targetValue = finalPosition,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy)
                        )
                    }
                }
            )
            .offset { IntOffset(0, animateOffset.value.roundToInt()) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(88.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(appColors.greyMedium.copy(.5f))
        )
        Spacer(Modifier.height(5.dp))
        Spacer(
            Modifier
                .height(1.dp)
                .fillMaxWidth()
                .background(appColors.bgLight)
        )
        itemSection()
    }
}

@Composable
fun QuickViaBarPoiSection(
    modifier: Modifier = Modifier,
    localPoiInfos: List<LocalPoiInfo>,
    bottomBoxHeightDp: Dp,
    onClickItem: (uid: String) -> Unit,
    onLongClickItem: (uid: String) -> Unit,
) {
    val appColors = AppColors.current
    Box(
        Modifier
            .fillMaxWidth()
            .background(appColors.bgPrimary),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = modifier
                .horizontalScroll(rememberScrollState())
                .height(bottomBoxHeightDp)
                .fillMaxWidth()
        ) {
            for (item in localPoiInfos) {
                Column(
                    modifier = Modifier
                        .size(height = 76.dp, width = 66.dp)
                        .pointerInput(item.uid) {
                            detectTapGestures(
                                onTap = {
                                    onClickItem(item.uid)
                                },
                                onLongPress = {
                                    onLongClickItem(item.uid)
                                }
                            )
                        },
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        modifier = Modifier
                            .size(44.dp),
                        colors = CardDefaults.cardColors(containerColor = appColors.greyLight),
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Image(
                            modifier = Modifier
                                .size(44.dp),
                            contentScale = ContentScale.Crop,
                            painter = if (item.iconPic.isNotBlank()) rememberAsyncImagePainter(item.iconPic) else painterResource(
                                R.drawable.home
                            ),
                            contentDescription = null,
                        )

                    }
                    Text(
                        text = StringUtils.truncateText(item.name, 4),
                        maxLines = 1,
                        style = TextStyle(
                            color = appColors.fontPrimary,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }

}

@Composable
fun QuickViaBarPoiEdit(
    modifier: Modifier = Modifier,
    visible: Boolean,
    localPoiInfo: LocalPoiInfo?,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: (LocalPoiInfo) -> Unit
) {
    if (localPoiInfo == null) return
    val appColors = AppColors.current
    val context = LocalContext.current
    var imagePath by remember { mutableStateOf(localPoiInfo.iconPic) }
    var poiName by rememberSaveable { mutableStateOf(localPoiInfo.name) }

    LaunchedEffect(localPoiInfo) {
        imagePath = localPoiInfo.iconPic
        poiName = localPoiInfo.name
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            uri?.let {
                val savedImagePath = saveImageToInternalStorage(context, it)
                savedImagePath?.let { path ->
                    imagePath = path
                }
            }
        }
    )
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .shake(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = modifier
                    .border(1.dp, appColors.bgLight, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(appColors.bgPrimary)
                    .padding(20.dp),
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .size(110.dp)
                        .border(1.dp, appColors.bgLight, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = if (imagePath.isNotBlank()) rememberAsyncImagePainter(imagePath)
                        else painterResource(R.drawable.home),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(108.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { launcher.launch("image/*") }
                    )
                }
                Column(
                    modifier = Modifier
                        .width(210.dp)
                        .height(108.dp)
                        .padding(start = 10.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomTextFiled(
                        Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .border(1.dp, appColors.bgLight, RoundedCornerShape(10.dp)),
                        text = poiName,
                        textColor = appColors.fontPrimary,
                        leftSection = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .size(22.dp),
                                tint = appColors.greyMedium
                            )
                        }
                    ) {
                        poiName = it
                    }

                    QuickViaEditButtons(
                        onDelete = {
                            onDelete()
                        },
                        onCancel = onCancel,
                        onConfirm = {
                            onConfirm(
                                localPoiInfo.copy(iconPic = imagePath, name = poiName)
                            )
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun QuickViaEditButtons(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    val appColors = AppColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (3).dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Button(
            modifier = Modifier.width(70.dp),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                containerColor = appColors.err
            ),
            shape = RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp),
            onClick = {
                onDelete()
            }
        ) {
            Text("删除")
        }

        Button(
            modifier = Modifier.width(70.dp),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                containerColor = appColors.warn
            ),
            shape = RoundedCornerShape(0.dp),
            onClick = {
                onCancel()
            }
        ) {
            Text("取消")
        }

        Button(
            modifier = Modifier.width(70.dp),
            contentPadding = PaddingValues(),
            colors = ButtonDefaults.buttonColors(
                containerColor = appColors.primary
            ),
            shape = RoundedCornerShape(topEnd = 10.dp, bottomEnd = 10.dp),
            onClick = {
                onConfirm()
            }
        ) {
            Text("确定")
        }
    }
}

fun saveImageToInternalStorage(context: Context, uri: Uri): String? {
    val fileName = "${System.currentTimeMillis()}.jpg"
    val file = File(context.filesDir, fileName)
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        file.absolutePath // 返回保存的路径
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}