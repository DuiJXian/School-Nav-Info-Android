package com.xz.schoolnavinfo.presentation.user

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.xz.schoolnavinfo.presentation.common.Screen
import com.xz.schoolnavinfo.presentation.theme.AppColors

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginOrRegisterScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    navController: NavController
) {
    val loginOrRegister by userViewModel.loginOrRegister
    val loginOrRegisterState = userViewModel.loginOrRegisterState
    val loginRes by userViewModel.loginRes
    val appColors = AppColors.current

    var confirmPassword by remember { mutableStateOf("") }

    var errMessage = userViewModel.errMessage

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginRes) {
        if (loginRes.code == "fail") {
            Log.e("TAG", "LoginOrRegisterScreen: ")
            //snackbarHostState.showSnackbar(loginRes.message, duration = SnackbarDuration.Short)
        } else if (loginRes.code == "success") {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

//    LaunchedEffect(Unit) {
//        userViewModel.registerSucFlow.collectLatest {
//            userViewModel.onEvent(UserEvent.ChangeLoginRegister)
//        }
//    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) {
        Column(
            modifier = Modifier
                .background(appColors.bgPrimary)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                var loginColor = appColors.fontPrimary
                var loginSize = 25.sp
                var registerColor = appColors.greyMedium
                var registerSize = 16.sp
                if (loginOrRegister == 1) {
                    loginColor = appColors.greyMedium
                    loginSize = 16.sp
                    registerColor = appColors.fontPrimary
                    registerSize = 25.sp
                }
                Text(
                    modifier = Modifier
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            userViewModel.onEvent(UserEvent.ChangeLoginRegister)
                            errMessage = ""
                        },
                    text = "登录",
                    style = TextStyle(
                        color = loginColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = loginSize
                    )
                )
                Text(
                    modifier = Modifier
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            errMessage = ""
                            userViewModel.onEvent(UserEvent.ChangeLoginRegister)
                        },
                    text = "注册",
                    style = TextStyle(
                        color = registerColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = registerSize
                    )
                )
            }

            OutlinedTextField(
                value = loginOrRegisterState.username,
                onValueChange = { usernameText ->
                    val filteredText = usernameText.filter { it.isLetterOrDigit() && it.code < 128 }
                    userViewModel.onEvent(UserEvent.ChangeUsername(filteredText))
                },
                label = {
                    Text(
                        modifier = Modifier
                            .background(appColors.bgPrimary),
                        text = "账号",
                        style = TextStyle(
                            color = appColors.fontSecondary
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = appColors.primary,
                    focusedContainerColor = appColors.bgPrimary,
                    unfocusedContainerColor = appColors.bgPrimary,
                    focusedLabelColor = appColors.primary,
                    unfocusedIndicatorColor = appColors.fontSecondary,
                    focusedTextColor = appColors.primary,
                    unfocusedTextColor = appColors.fontPrimary,
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = loginOrRegisterState.password,
                onValueChange = { userViewModel.onEvent(UserEvent.ChangePassword(it)) },
                label = {
                    Text(
                        modifier = Modifier
                            .background(appColors.bgPrimary),
                        text = "密码",
                        style = TextStyle(
                            color = appColors.fontSecondary
                        )
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = appColors.primary,
                    focusedContainerColor = appColors.bgPrimary,
                    unfocusedContainerColor = appColors.bgPrimary,
                    focusedLabelColor = appColors.primary,
                    unfocusedIndicatorColor = appColors.fontSecondary,
                    focusedTextColor = appColors.primary,
                    unfocusedTextColor = appColors.fontPrimary,
                ),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            if (loginOrRegister == 1) {
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                    },
                    label = {
                        Text(
                            modifier = Modifier
                                .background(appColors.bgPrimary),
                            text = "确认密码",
                            style = TextStyle(
                                color = appColors.fontSecondary
                            )
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = appColors.primary,
                        focusedContainerColor = appColors.bgPrimary,
                        unfocusedContainerColor = appColors.bgPrimary,
                        focusedLabelColor = appColors.primary,
                        unfocusedIndicatorColor = appColors.fontSecondary,
                        focusedTextColor = appColors.primary,
                        unfocusedTextColor = appColors.fontPrimary,
                    ),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            }
            Log.e("TAG", "LoginOrRegisterScreen1: $errMessage")
            if (errMessage.isNotEmpty()) {
                Text(
                    text = errMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Button(
                modifier = Modifier
                    .padding(top = 10.dp),
                onClick = {
                    if (loginOrRegister == 0) {
                        userViewModel.onEvent(UserEvent.Login)
                    } else {
                        if (loginOrRegisterState.password != confirmPassword) {
                            userViewModel.onErrMessage("两次密码不一致")
                        } else {
                            userViewModel.onErrMessage("")
                            userViewModel.onEvent(UserEvent.Register)
                        }
                    }
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = appColors.primary,
                )
            ) {
                Text(
                    text = if (loginOrRegister == 0) "登录" else "注册",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

        }
    }

}

@Preview
@Composable
fun Preview() {
//    LoginOrRegisterScreen()
}