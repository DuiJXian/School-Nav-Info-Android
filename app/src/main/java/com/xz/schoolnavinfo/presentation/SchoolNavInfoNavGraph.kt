package com.xz.schoolnavinfo.presentation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.xz.schoolnavinfo.presentation.nav.CreateNavGraph
import com.xz.schoolnavinfo.presentation.nav.NavController
import com.xz.schoolnavinfo.presentation.nav.RouteWithArgs
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


sealed class MyRoutes: RouteWithArgs {
    data object Home: MyRoutes()

    data class ArticlePublish(val articleType: ArticleType): MyRoutes()

    data class ArticleDetail(val articleDTO: ArticleDTO): MyRoutes()

    data class ImagePreview(val urls: List<String>, val startIndex: Int): MyRoutes()

    data object LoginRegister: MyRoutes()

    data object LocationSelect: MyRoutes()

    data object StuffPublish: MyRoutes()

    data class StuffDetail(val id: String): MyRoutes()
}


val LocalNavController = staticCompositionLocalOf<NavController> {
    error("No user provided")
}

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun SchoolNavInfoNavGraph(
    navController: NavController,
    commonViewModel: CommonViewModel,
    publishArticleViewModel: PublishArticleViewModel
) {

    CompositionLocalProvider(LocalNavController provides navController) {
        CreateNavGraph(
            startRoute = MyRoutes.Home,
            navController = navController
        ) {
            navScreen<MyRoutes.ArticlePublish> {
                val args = it as MyRoutes.ArticlePublish
                DiscussPublishScreen(
                    publishArticleViewModel = publishArticleViewModel,
                    articleType = args.articleType
                )
            }

            navScreen<MyRoutes.ImagePreview> {
                val imagePreview = it as MyRoutes.ImagePreview
                ImagePreview(
                    imageList = imagePreview.urls,
                    startIndex = imagePreview.startIndex,
                )
            }

            navScreen<MyRoutes.ArticleDetail> {
                val arg = it as MyRoutes.ArticleDetail
                ArticleDetailScreen(
                    articleDTO = arg.articleDTO,
                    commonViewModel = commonViewModel
                )

            }

            navScreen<MyRoutes.StuffDetail> {
                val arg = it as MyRoutes.StuffDetail
                StuffDetailScreen(
                    id = arg.id,
                    commonViewModel = commonViewModel
                )
            }

            navScreen<MyRoutes.LoginRegister> {
                LoginRegisterScreen()
            }

            navScreen<MyRoutes.Home> {
                HomeScreen(commonViewModel = commonViewModel)
            }
            navScreen<MyRoutes.LocationSelect> {
                LocationSelectScreen()
            }
            navScreen<MyRoutes.StuffPublish> {
                StuffPublishScreen()
            }
        }
    }


}