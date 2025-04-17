package com.xz.schoolnavinfo.presentation.map.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.domain.data.entity.LocalPoiInfo
import com.xz.schoolnavinfo.presentation.common.compose.MyTextFiled
import com.xz.schoolnavinfo.presentation.theme.AppColors
import java.io.File
import java.io.FileOutputStream

@Preview
@Composable
fun TestPreview() {
    FavoriteItemEdit(
        mPoiInfo = LocalPoiInfo(
            "", "罗家坪", 1, "", "", ""
        ),
        onDelete = {},
        onCancel = {},
        onConfirm = {}
    )
}

@Composable
fun FavoriteItemEdit(
    modifier: Modifier = Modifier,
    mPoiInfo: LocalPoiInfo,
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onConfirm: (LocalPoiInfo) -> Unit
) {
    val appColors = AppColors.current
    val context = LocalContext.current
    var imagePath by remember { mutableStateOf(mPoiInfo.iconPic) } // 获取保存的图片路径
    var poiName by remember { mutableStateOf(mPoiInfo.name) }
    //var isEnableConfirm by remember { mutableStateOf(false) }
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

    //isEnableConfirm = imagePath != mPoiInfo.iconPic || poiName != mPoiInfo.name

    Box(
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(20.dp))
            .background(appColors.bgPrimary)
            .clip(RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier
                .background(appColors.bgPrimary),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .size(108.dp)
                    .border(1.dp, appColors.greyLight, RoundedCornerShape(10.dp))
            ) {
                if (imagePath.isNotBlank()) {
                    Image(
                        painter = rememberAsyncImagePainter(imagePath),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(108.dp)
                            .clickable {
                                launcher.launch("image/*")
                            }
                    )
                } else {
                    Image(
                        painter = painterResource(R.drawable.home),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(108.dp)
                            .clickable {
                                launcher.launch("image/*")
                            }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .width(210.dp)
                    .height(108.dp)
                    .padding(start = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                MyTextFiled(
                    borderColor = appColors.greyMedium,
                    containerColor = appColors.bgPrimary,
                    defaultText = mPoiInfo.name,
                    onFocusContainerColor = appColors.bgPrimary,
                    alpha = 0.5f,
                    height = 46.dp,
                    icon = Icons.Default.Edit
                ) {
                    poiName = it
                }
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
                            onConfirm(
                                mPoiInfo.copy(
                                    iconPic = imagePath,
                                    name = poiName
                                )
                            )
                        }
                    ) {
                        Text("确定")
                    }
                }
            }

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
