package com.xz.schoolnavinfo.presentation.user

import android.annotation.SuppressLint
import android.view.Gravity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.xz.schoolnavinfo.presentation.LocalNavController
import com.xz.schoolnavinfo.presentation.MyRoutes
import com.xz.schoolnavinfo.presentation.common.components.CustomTextFiled
import com.xz.schoolnavinfo.presentation.common.components.MyButton
import com.xz.schoolnavinfo.presentation.common.components.SliderButton
import com.xz.schoolnavinfo.presentation.theme.AppColors
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.flow.collectLatest


private val titles = listOf("登陆", "注册")


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginRegisterScreen(userViewModel: UserViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val uiSate by userViewModel.uiSate.collectAsStateWithLifecycle()
    val navigator = LocalNavController.current
    LaunchedEffect(uiSate.isSuccess) {
        if (uiSate.isSuccess) {
            navigator.navigate(MyRoutes.Home, MyRoutes.LoginRegister)
        }
    }

    LaunchedEffect(Unit) {
        userViewModel.errMsgEvent.collectLatest {
            StyleableToast.Builder(context).text(it)
                .textColor(Color.White.toArgb())
                .backgroundColor(Color(0xFF0091EA).toArgb()).cornerRadius(36)
                .gravity(Gravity.TOP).show()
        }
    }

    LoginOrRegisterContent(
        username = uiSate.username,
        password = uiSate.password,
        againPassword = uiSate.againPassword,
        errMsg = uiSate.errMessage,
        currentTitleIndex = uiSate.currentTitleIndex,
        onUsernameChange = { userViewModel.setUsername(it) },
        onPasswordChange = { userViewModel.setPassword(it) },
        onAgainPassword = { userViewModel.setAgainPassword(it) },
        onTypeChange = { userViewModel.setCurrentTitleIndex(it) },
        onLogin = { userViewModel.login() },
        onRegister = { userViewModel.register() }
    )
}

@Composable
fun LoginOrRegisterContent(
    username: String,
    password: String,
    againPassword: String,
    errMsg: String,
    currentTitleIndex: Int = 0,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onAgainPassword: (String) -> Unit,
    onTypeChange: (Int) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {
    val appColors = AppColors.current
    val focusManager = LocalFocusManager.current
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Column(
                Modifier
                    .width(260.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Bottom
            ) {
                SliderButton(
                    titles = titles,
                    width = 260.dp,
                    height = 42.dp,
                    padding = 3.dp,
                    backgroundColors = appColors.greyLight,
                    selectedColors = appColors.bgPrimary,
                    textStyle = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                ) { onTypeChange(it) }
                Spacer(Modifier.height(10.dp))
                CustomTextFiled(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(shape = CircleShape)
                        .background(appColors.greyLight),
                    text = username,
                    hintText = "账号",
                    leftSection = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(24.dp),
                            tint = appColors.greyMedium
                        )
                    },
                ) { text ->
                    val filteredText = text.filter { it.isLetterOrDigit() && it.code < 128 }
                    onUsernameChange(filteredText)
                }
                Spacer(Modifier.height(10.dp))
                CustomTextFiled(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .clip(shape = CircleShape)
                        .background(appColors.greyLight),
                    hintText = "密码",
                    isPassword = true,
                    text = password,
                    leftSection = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 10.dp)
                                .size(24.dp),
                            tint = appColors.greyMedium
                        )
                    },
                ) { onPasswordChange(it) }
            }

            Column(
                Modifier
                    .width(260.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Top
            ) {
                if (currentTitleIndex == 1) Spacer(Modifier.height(10.dp))
                AnimatedVisibility(
                    visible = currentTitleIndex == 1,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    CustomTextFiled(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .clip(shape = CircleShape)
                            .background(appColors.greyLight),
                        text = againPassword,
                        isPassword = true,
                        hintText = "确认密码",
                        leftSection = {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                                    .size(24.dp),
                                tint = appColors.greyMedium
                            )
                        },
                    ) { onAgainPassword(it) }
                }


                Row(
                    Modifier.height(if (errMsg.isNotEmpty()) 20.dp else 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedVisibility(
                        visible = errMsg.isNotEmpty(),
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(Modifier.width(260.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = errMsg,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                MyButton(
                    text = titles[currentTitleIndex],
                    shape = CircleShape,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    width = 260.dp,
                    height = 42.dp
                ) {
                    focusManager.clearFocus()
                    if (currentTitleIndex == 0) onLogin() else onRegister()
                }
            }
        }


    }
}