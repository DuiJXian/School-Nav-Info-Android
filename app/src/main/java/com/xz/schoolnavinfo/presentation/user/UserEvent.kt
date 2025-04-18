package com.xz.schoolnavinfo.presentation.user

sealed class UserEvent {
    data object ChangeLoginRegister : UserEvent()
    data class ChangeUsername(val username: String) : UserEvent()
    data class ChangePassword(val password: String) : UserEvent()
    data object Login: UserEvent()
    data object Register: UserEvent()
}