package com.xz.schoolnavinfo.presentation.map.composable

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.utils.StringUtils
import com.xz.schoolnavinfo.domain.data.entity.LocalPoiInfo
import com.xz.schoolnavinfo.presentation.common.components.CustomTextFiled
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlin.math.roundToInt


@Composable
fun QuickViaSection(
    showQuickBar: Boolean,
    showQuickEdit: Boolean,
    selectUid: String,
    localPoiInfos: List<LocalPoiInfo>,
    onClickItem: (uid: String) -> Unit,
    onLongClickItem: (uid: String) -> Unit,
    onDelete: (LocalPoiInfo) -> Unit,
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
            localPoiInfo = localPoiInfos.find { it.uid == selectUid },
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

    val menuPosition = remember { Animatable(0f) }
    val decay = rememberSplineBasedDecay<Float>()

    val draggableState = rememberDraggableState {
        coroutineScope.launch {
            val offsetY = (menuPosition.value + it).coerceIn(0f, bottomBoxHeightPx)
            menuPosition.snapTo(offsetY)
        }
    }
    Column(
        modifier
            .draggable(
                orientation = Orientation.Vertical,
                state = draggableState,
                onDragStopped = { velocity ->
                    coroutineScope.launch {
                        val targetPosition =
                            decay.calculateTargetValue(menuPosition.value, velocity)
                        val finalPosition = if (targetPosition < bottomBoxHeightPx / 2) 0f
                        else bottomBoxHeightPx
                        menuPosition.animateTo(finalPosition)
                    }
                }
            )
            .offset { IntOffset(0, menuPosition.value.roundToInt()) },
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
    Row(
        modifier = modifier
            .height(bottomBoxHeightDp)
            .background(appColors.bgPrimary)
            .padding(top = 10.dp)
            .fillMaxWidth()
    ) {
        for (item in localPoiInfos) {
            Box(
                modifier = Modifier
                    .size(66.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                onClickItem(item.uid)
                            },
                            onLongPress = {
                                onLongClickItem(item.uid)
                            }
                        )
                    },
            ) {
                Card(
                    modifier = Modifier
                        .size(46.dp)
                        .align(Alignment.TopCenter),
                    colors = CardDefaults.cardColors(containerColor = appColors.greyLight),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    if (item.iconPic.isNotBlank()) {
                        Image(
                            modifier = Modifier
                                .size(46.dp),
                            contentScale = ContentScale.Crop,
                            painter = rememberAsyncImagePainter(item.iconPic),
                            contentDescription = null,
                        )
                    } else {
                        Image(
                            modifier = Modifier
                                .size(46.dp),
                            painter = painterResource(R.drawable.home),
                            contentDescription = null,
                        )
                    }

                }
                Text(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    text = StringUtils.truncateText(item.name, 4),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(
                        color = appColors.fontPrimary
                    )
                )
            }
        }
    }
}

@Composable
fun QuickViaBarPoiEdit(
    modifier: Modifier = Modifier,
    visible: Boolean,
    localPoiInfo: LocalPoiInfo?,
    onDelete: (LocalPoiInfo) -> Unit,
    onCancel: () -> Unit,
    onConfirm: (LocalPoiInfo) -> Unit
) {
    if (localPoiInfo == null || !visible) {
        return
    }
    val appColors = AppColors.current
    val context = LocalContext.current
    var imagePath by remember { mutableStateOf(localPoiInfo.iconPic) }
    var poiName by remember { mutableStateOf(localPoiInfo.name) }
    var editText by remember { mutableStateOf(poiName) }
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
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = modifier
                .border(1.dp, appColors.greyMedium.copy(0.5f), RoundedCornerShape(20.dp))
                .clip(RoundedCornerShape(20.dp))
                .background(appColors.bgPrimary)
                .padding(20.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(108.dp)
                    .border(1.dp, appColors.greyMedium.copy(0.5f), RoundedCornerShape(10.dp))
            ) {
                Image(
                    painter = if (imagePath.isNotBlank()) rememberAsyncImagePainter(imagePath)
                    else painterResource(R.drawable.home),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(108.dp)
                        .clickable {
                            launcher.launch("image/*")
                        }
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
                        .border(1.dp, appColors.greyMedium.copy(.5f), RoundedCornerShape(10.dp)),
                    text = editText,
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
                    editText = it
                    poiName = it
                }

                QuickViaEditButtons(
                    onDelete = { onDelete(localPoiInfo) },
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