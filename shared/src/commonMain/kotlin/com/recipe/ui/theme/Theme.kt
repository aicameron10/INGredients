package com.recipe.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.tnex.bookchoice.ui.theme.Shapes

private val DarkColorPalette = darkColors(
    primary = blue5,
    primaryVariant = white,
    secondary = blue5,
    onSurface = grey5,
    background = grey9
)

private val LightColorPalette = lightColors(
    primary = blue5,
    primaryVariant = black,
    secondary = blue5,
    onSurface = grey5,
    background = white

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun AppTheme(
    darkTheme: Boolean? = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme == true) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
