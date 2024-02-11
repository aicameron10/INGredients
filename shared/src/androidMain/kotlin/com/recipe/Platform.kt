package com.recipe

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.core.text.HtmlCompat
import com.recipe.common.R


class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

@Composable
fun MainView() {
    App()
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getAppVersion(): String {
    val packageInfo: PackageInfo = RecipeApp.appContext.packageManager.getPackageInfo(
        RecipeApp.appContext.packageName,
        0
    )
    return packageInfo.versionName + " (" + packageInfo.versionCode.toString() + ")"
}

actual val msFontFamily: FontFamily = FontFamily(
    Font(R.font.ms_sans_serif_regular)
)

actual val dmSansFontFamily: FontFamily = FontFamily(
    Font(R.font.dm_sans)
)

actual fun switchToLightMode() {
    if (Build.VERSION.SDK_INT <= 30) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    } else {
        val uiManager =
            RecipeApp.appContext.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        uiManager.nightMode = UiModeManager.MODE_NIGHT_NO
    }
}

actual fun switchToDarkMode() {
    if (Build.VERSION.SDK_INT <= 30) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    } else {
        val uiManager =
            RecipeApp.appContext.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
        uiManager.nightMode = UiModeManager.MODE_NIGHT_YES
    }
}

actual fun isConnected(): Boolean {

    val connectivityManager =
        RecipeApp.appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
}

actual fun openBrowser(url: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(url)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        RecipeApp.appContext.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

actual fun renderHtml(htmlString: String): String {
    return HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
}