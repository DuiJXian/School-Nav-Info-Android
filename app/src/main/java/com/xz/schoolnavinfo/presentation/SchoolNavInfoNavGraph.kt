package com.xz.schoolnavinfo.presentation

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.xz.schoolnavinfo.domain.data.dto.ArticleDTO
import com.xz.schoolnavinfo.domain.data.type.ArticleType
import com.xz.schoolnavinfo.presentation.campus.article.ArticleDetailScreen
import com.xz.schoolnavinfo.presentation.campus.publish.DiscussPublishScreen
import com.xz.schoolnavinfo.presentation.campus.publish.PublishArticleViewModel
import com.xz.schoolnavinfo.presentation.campus.stuff.detail.StuffDetailScreen
import com.xz.schoolnavinfo.presentation.campus.stuff.pub.StuffPublishScreen
import com.xz.schoolnavinfo.presentation.common.baidu.select.LocationSelectScreen
import com.xz.schoolnavinfo.presentation.common.components.ImagePreview
import com.xz.schoolnavinfo.presentation.common.viewmodel.CommonViewModel
import com.xz.schoolnavinfo.presentation.home.HomeScreen
import com.xz.schoolnavinfo.presentation.user.LoginRegisterScreen
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.serializer


@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T : @Serializable Any> NavGraphBuilder.animatedComposable(
    noinline content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    val name = T::class.simpleName
    val descriptor = serializer<T>().descriptor
    val routeStr =
        if (descriptor.elementsCount == 0 && descriptor.kind == StructureKind.OBJECT) name else "$name?data={data}"
    this.composable(
        route = routeStr!!,
        arguments = listOf(navArgument("data") { defaultValue = "" }),
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
        },
        exitTransition = null,
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            )
        }, popExitTransition = null,
        content = content
    )
}

sealed class Routes {
    @Serializable
    object Home

    @Serializable
    data class ArticlePublish(val articleType: ArticleType)

    @Serializable
    data class ArticleDetail(val articleDTO: ArticleDTO)

    @Serializable
    data class ImagePreview(val urls: List<String>, val startIndex: Int)

    @Serializable
    object LoginRegister

    @Serializable
    object LocationSelect

    @Serializable
    object StuffPublish

    @Serializable
    data class StuffDetail(val id: String)
}


@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun SchoolNavInfoNavGraph(
    navController: NavHostController,
    commonViewModel: CommonViewModel,
    publishArticleViewModel: PublishArticleViewModel
) {

    NavHost(
        navController = navController,
        startDestination = Routes.Home::class.simpleName!!
    ) {
        animatedComposable<Routes.ArticlePublish> {
            val arg = it.navArgs<Routes.ArticlePublish>()
            DiscussPublishScreen(
                publishArticleViewModel = publishArticleViewModel,
                articleType = arg.articleType
            )
        }
        animatedComposable<Routes.ImagePreview> {
            val imagePreview = it.navArgs<Routes.ImagePreview>()
            ImagePreview(
                imageList = imagePreview.urls,
                startIndex = imagePreview.startIndex,
            )
        }

        animatedComposable<Routes.ArticleDetail> {
            val arg = it.navArgs<Routes.ArticleDetail>()
            ArticleDetailScreen(
                articleDTO = arg.articleDTO,
                commonViewModel = commonViewModel
            )

        }

        animatedComposable<Routes.StuffDetail> {
            val arg = it.navArgs<Routes.StuffDetail>()
            StuffDetailScreen(
                id = arg.id,
                commonViewModel = commonViewModel
            )
        }

        animatedComposable<Routes.LoginRegister> {
            LoginRegisterScreen()
        }

        animatedComposable<Routes.Home> {
            HomeScreen(commonViewModel = commonViewModel)
        }
        animatedComposable<Routes.LocationSelect> {
            LocationSelectScreen()
        }
        animatedComposable<Routes.StuffPublish> {
            StuffPublishScreen()
        }
    }
}