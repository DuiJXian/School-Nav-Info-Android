import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.xz.schoolnavinfo.presentation.theme.DarkCustomColors
import com.xz.schoolnavinfo.presentation.theme.LightCustomColors

val AppColors = staticCompositionLocalOf { LightCustomColors }


@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // 根据亮色/暗色模式选择颜色方案
    val colors = if (darkTheme) DarkCustomColors else LightCustomColors

    // 提供自定义颜色
    CompositionLocalProvider(AppColors provides colors) {
        // 包裹内容
        content()
    }
}