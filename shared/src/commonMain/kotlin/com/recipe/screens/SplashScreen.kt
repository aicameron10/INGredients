package com.recipe.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.recipe.multiplatformsettings.SessionManager
import com.recipe.ui.theme.orange
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SplashScreen : Screen, KoinComponent {

    @OptIn(ExperimentalResourceApi::class)
    @ExperimentalMaterialApi
    @Composable
    override fun Content() {

        val sessionManager = get<SessionManager>()
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect(true) {
            delay(3000)
            if (sessionManager.getOnBoarding()) {
                navigator.replaceAll(TabsScreen())
            } else {
                navigator.replaceAll(WelcomeScreen())
            }
        }

        Box(
            modifier = Modifier.fillMaxSize()
                .background(orange),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource("main_ing.png"),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
            )
        }
    }
}