package com.xz.schoolnavinfo.presentation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder


val LocalAppNavigator = staticCompositionLocalOf<Navigator> {
    error("No Navigator provided")
}


val json = Json { encodeDefaults = true; ignoreUnknownKeys = true }

inline fun <reified T : @Serializable Any> NavController.navigateTo(
    data: T,
    route: String = T::class.simpleName ?: "unknown"
) {
    val encoded = URLEncoder.encode(json.encodeToString(data), Charsets.UTF_8.name())
    this.navigate("$route?data=$encoded") {
        launchSingleTop = true
        restoreState = true
    }
}

inline fun <reified T : @Serializable Any> NavBackStackEntry.navArgs(): T {
    val raw = arguments?.getString("data")
        ?: error("Missing nav argument `data` for ${T::class.simpleName}")
    val decoded = URLDecoder.decode(raw, Charsets.UTF_8.name())
    return json.decodeFromString(decoded)
}

class Navigator(val navController: NavHostController) {

    inline fun <reified T : Any> navigate(route: T) {
        navController.navigateTo(route)
    }


    fun popBack() {
        navController.popBackStack()
    }

    fun popTo(route: String, inclusive: Boolean = false) {
        navController.popBackStack(route, inclusive)
    }

    fun navigateAndPopUp(destination: String, popUpToRoute: String, inclusive: Boolean = false) {
        navController.navigate(destination) {
            popUpTo(popUpToRoute) {
                this.inclusive = inclusive
            }
        }
    }

    fun navigateIfNotCurrent(destination: String) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (currentRoute != destination) {
            navController.navigate(destination)
        }
    }

    fun saveLocationData(data: String) {
        navController.previousBackStackEntry?.savedStateHandle?.set("location", data)
    }

    fun getLocationData(): String? {
        return navController.currentBackStackEntry
            ?.savedStateHandle?.get<String>("location")
    }
}
