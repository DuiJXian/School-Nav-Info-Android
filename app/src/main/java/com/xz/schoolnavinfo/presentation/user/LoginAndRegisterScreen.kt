package com.xz.schoolnavinfo.presentation.user

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    authViewModel: UserViewModel = hiltViewModel(),
    navController: NavController
) {
    val loginOrRegister by authViewModel.loginOrRegister
    val authState by authViewModel.loginOrRegisterState
    val loginRes by authViewModel.loginRes
    val appColors = AppColors.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(loginRes) {
        if (loginRes.code == "fail") {
            snackbarHostState.showSnackbar(loginRes.message, duration = SnackbarDuration.Short)
        } else if (loginRes.code == "success") {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }



    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) {
        Column(
            modifier = Modifier
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
                        .clickable { authViewModel.onEvent(UserEvent.ChangeLoginRegister) },
                    text = "登录",
                    style = TextStyle(
                        color = loginColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = loginSize
                    )
                )
                Text(
                    modifier = Modifier
                        .clickable { authViewModel.onEvent(UserEvent.ChangeLoginRegister) },
                    text = "注册",
                    style = TextStyle(
                        color = registerColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = registerSize
                    )
                )
            }

            OutlinedTextField(
                value = authState.username,
                onValueChange = {
                    authViewModel.onEvent(UserEvent.ChangeUsername(it))
                },
                label = { Text("账号") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = appColors.primary,
                    focusedContainerColor = appColors.bgPrimary,
                    unfocusedContainerColor = appColors.bgPrimary,
                    focusedLabelColor = appColors.primary,
                    unfocusedIndicatorColor = appColors.fontSecondary
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = authState.password,
                onValueChange = { authViewModel.onEvent(UserEvent.ChangePassword(it)) },
                label = { Text("密码") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = appColors.primary,
                    focusedContainerColor = appColors.bgPrimary,
                    unfocusedContainerColor = appColors.bgPrimary,
                    focusedLabelColor = appColors.primary,
                    unfocusedIndicatorColor = appColors.fontSecondary
                ),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation()
            )

            if (loginOrRegister == 1) {
                OutlinedTextField(
                    value = authState.password,
                    onValueChange = { authViewModel.onEvent(UserEvent.ChangePassword(it)) },
                    label = { Text("确认密码") },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = appColors.primary,
                        focusedContainerColor = appColors.bgPrimary,
                        unfocusedContainerColor = appColors.bgPrimary,
                        focusedLabelColor = appColors.primary,
                        unfocusedIndicatorColor = appColors.fontSecondary
                    ),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation()
                )
            }

            Button(
                modifier = Modifier
                    .padding(top = 10.dp),
                onClick = {
                    authViewModel.onEvent(UserEvent.Login)
                },
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = appColors.primary
                )
            ) {
                Text(if (loginOrRegister == 0) "登录" else "注册")
            }

        }
    }

}

@Preview
@Composable
fun Preview() {
//    LoginOrRegisterScreen()
}