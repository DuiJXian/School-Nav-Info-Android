package com.xz.schoolnavinfo.presentation.my

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.esafirm.imagepicker.features.ImagePicker
import com.esafirm.imagepicker.features.ImagePickerConfig
import com.esafirm.imagepicker.features.createImagePickerIntent
import com.xz.schoolnavinfo.R
import com.xz.schoolnavinfo.common.net.montageCompleteUrl
import com.xz.schoolnavinfo.presentation.campus.stuff.pub.PublishStuffEvent
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.common.viewmodel.NavEvent
import com.xz.schoolnavinfo.presentation.theme.AppColors
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MyScreen(
    myViewModel: MyViewModel = hiltViewModel(),
    commonViewModel: CommonViewModel
) {

    val appColors = AppColors.current
    val systemPadding = WindowInsets.systemBars.asPaddingValues()

    var showPwdDialog by remember { mutableStateOf(false) }
    var showLogOutDialog by remember { mutableStateOf(false) }
    var showNicknameDialog by remember { mutableStateOf(false) }
    val userInfo by commonViewModel.userInfo.collectAsState()

    val isSystemInDarkTheme = isSystemInDarkTheme()
    val context = LocalContext.current

    var errMessage by remember { mutableStateOf("") }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = ImagePicker.getImages(result.data)
            data?.let {
                myViewModel.onUpdateAvatar(it.first().path)
            }
        }
    }

    LaunchedEffect(Unit) {
        myViewModel.changePasswordFlow.collectLatest {
            if (it.code == "success") {
                showPwdDialog = false
                myViewModel.onLogOut()
                commonViewModel.onNavEvent(NavEvent.LoginOrRegister)
            } else {
                Log.e("TAG", "LaunchedEffect: $errMessage")
                errMessage = it.message
            }
        }
    }

    LaunchedEffect(Unit) {
        myViewModel.changeNicknameAndAvatarFlow.collectLatest {
            if (it.code == "success") {
                commonViewModel.getUserInfo()
            }
            Toast.makeText(
                context,
                it.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    if (showNicknameDialog) {
        ChangeNicknameDialog(
            onDismiss = { showNicknameDialog = false }
        ) {
            showNicknameDialog = false
            myViewModel.onUpdateNickname(it)
        }
    }

    if (showPwdDialog) {
        ChangePasswordDialog(
            errMessage = errMessage,
            onDismiss = {
                showPwdDialog = false
            }
        ) { oldPassword, newPassword ->
            myViewModel.onUpdatePassword(oldPassword, newPassword)
        }
    }
    if (showLogOutDialog) {
        ConfirmLogoutDialog(onDismiss = {
            showLogOutDialog = false
        }) {
            showLogOutDialog = false
            myViewModel.onLogOut()
            commonViewModel.onNavEvent(NavEvent.LoginOrRegister)
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appColors.bgScreen)
            .padding(systemPadding)
    ) {

        Column {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            val intent = createImagePickerIntent(
                                context, ImagePickerConfig(
                                    limit = 1,
                                    theme = if (isSystemInDarkTheme) R.style.ImagePickerThemeDark else R.style.ImagePickerThemeLight
                                )
                            )
                            imagePickerLauncher.launch(intent)
                        },
                    painter = if (userInfo.avatarUrl.isNullOrBlank())
                        painterResource(R.drawable.heard_image)
                    else rememberAsyncImagePainter(montageCompleteUrl(userInfo.avatarUrl)),
                    contentDescription = "头像",
                    contentScale = ContentScale.Crop
                )
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(appColors.bgPrimary)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .clickable(
                                interactionSource = null,
                                indication = null
                            ) {
                                showNicknameDialog = true
                            }
                    ) {
                        Text(
                            text = userInfo.nickname,
                            style = TextStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = appColors.fontPrimary
                            )
                        )
                        Icon(
                            modifier = Modifier
                                .size(12.dp),
                            imageVector = Icons.Default.Edit,
                            contentDescription = null
                        )
                    }
                    Spacer(
                        modifier = Modifier
                            .padding(top = 3.dp, bottom = 2.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .width(100.dp)
                            .height(3.dp)
                            .background(appColors.greyMedium.copy(.5f))
                    )
                    Text(
                        text = userInfo.username,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = appColors.greyHeavy
                        )
                    )
                }

            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(appColors.bgPrimary)
                    .padding(10.dp),
            ) {
                Row(
                    modifier = Modifier
                        .clickable {
                            showPwdDialog = true
                        }
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = "修改密码",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = appColors.fontPrimary
                        )
                    )

                }

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 10.dp)
                        .height(1.dp)
                        .background(appColors.greyMedium.copy(.5f))
                )
                Row(
                    modifier = Modifier
                        .clickable {
                            showLogOutDialog = true
                        }
                        .fillMaxWidth(),
                ) {
                    Text(
                        text = "退出登陆",
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = appColors.fontPrimary
                        )
                    )

                }
            }
        }
    }
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    errMessage: String,
    onConfirm: (oldPassword: String, newPassword: String) -> Unit
) {
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf(errMessage) }

    LaunchedEffect(errMessage) {
        errorText = errMessage
    }
    AlertDialog(
        onDismissRequest = {},
        title = { Text("修改密码") },
        text = {
            Column {
                OutlinedTextField(
                    value = oldPassword,
                    onValueChange = { oldPassword = it },
                    label = { Text("当前密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("新密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("确认新密码") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                if (errorText.isNotBlank()) {
                    Text(
                        text = errorText,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (newPassword != confirmPassword) {
                    errorText = "两次输入的新密码不一致"
                } else if (oldPassword.isBlank() || newPassword.isBlank()) {
                    errorText = "请填写完整信息"
                } else {
                    onConfirm(oldPassword, newPassword)
                }
            }) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun ChangeNicknameDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var nickname by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {},
        title = { Text("修改昵称") },
        text = {
            Column {
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { nickname = it },
                    label = { Text("当前昵称") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(nickname)
            }) {
                Text("确认")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
fun ConfirmLogoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("确认退出登录？") },
        text = { Text("退出登录后需要重新输入账号密码，是否确定退出？") },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
            }) {
                Text("确定", color = MaterialTheme.colorScheme.error)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

