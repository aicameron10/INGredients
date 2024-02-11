package com.recipe

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.NavigatorDisposeBehavior
import com.recipe.ui.theme.AppTheme
import com.recipe.screens.SplashScreen

interface Platform {
    val name: String
}

@Composable
fun App() {
    AppTheme {
        Navigator(
            SplashScreen(), onBackPressed = { currentScreen ->
                true
            }, disposeBehavior = NavigatorDisposeBehavior(disposeNestedNavigators = false)
        )
    }
}

expect fun getPlatform(): Platform

expect fun getAppVersion(): String

expect val msFontFamily: FontFamily

expect val dmSansFontFamily: FontFamily

expect fun switchToLightMode()

expect fun switchToDarkMode()

expect fun isConnected(): Boolean

expect fun openBrowser(url: String)

expect fun renderHtml(htmlString: String): String
