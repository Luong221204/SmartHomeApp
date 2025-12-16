package com.example.myhome.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

val LocalTypography = staticCompositionLocalOf {
    AppTypo()
}

val LocalColor = staticCompositionLocalOf {
    AppColor()
}

val LocalRoundedCorner = staticCompositionLocalOf {
    RoundedCorner()
}


val LocalDimen = staticCompositionLocalOf {
    Dimen()
}

val LocalSpacer = staticCompositionLocalOf {
    Spacer()
}
val LocalPadding = staticCompositionLocalOf {
    Padding()
}
@Composable
fun AppTheme(
    content: @Composable () -> Unit
){
    CompositionLocalProvider(
        LocalTypography provides AppTypo(),
        LocalColor provides AppColor(),
        LocalRoundedCorner provides RoundedCorner(),
        LocalDimen provides Dimen(),
        LocalSpacer provides Spacer(),
        LocalPadding provides Padding()
    ) {
        content.invoke()
    }
}

object AppTheme {
    val typography: AppTypo
        @Composable
        get() = LocalTypography.current
    val color: AppColor
        @Composable
        get() = LocalColor.current
    val corner: RoundedCorner
        @Composable
        get() = LocalRoundedCorner.current
    val dimen: Dimen
        @Composable
        get() = LocalDimen.current
    val spacer: Spacer
        @Composable
        get() = LocalSpacer.current
    val padding: Padding
        @Composable
        get() = LocalPadding.current

}