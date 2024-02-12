package com.recipe

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Typeface
import androidx.compose.ui.window.ComposeUIViewController
import org.jetbrains.skia.FontStyle
import org.jetbrains.skia.Typeface
import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

fun MainViewController() = ComposeUIViewController { App() }
actual fun getAppVersion(): String {
    val version = NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String
        ?: "Unknown"
    val build = NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion") as? String ?: "Unknown"
    return "$version ($build)"
}

actual val msFontFamily: FontFamily = FontFamily(
    Typeface(loadCustomFont("ms_sans_serif_regular"))
)

actual val dmSansFontFamily: FontFamily = FontFamily(
    Typeface(loadCustomFont("dm_sans"))
)

private fun loadCustomFont(name: String): Typeface {
    return Typeface.makeFromName(name, FontStyle.NORMAL)
}

actual fun switchToLightMode() {}

actual fun switchToDarkMode() {}

actual fun isConnected(): Boolean {
    return true
}

actual fun openBrowser(url: String) {
    val nsUrl = NSURL(string = url)
    nsUrl.let {
        if (UIApplication.sharedApplication.canOpenURL(it)) {
            UIApplication.sharedApplication.openURL(it)
        }
    }
}

actual fun renderHtml(htmlString: String): String {
    return htmlString
}