package com.recipe.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.recipe.screens.HomeScreen
import com.recipe.viewmodels.SharedViewModel
import compose.icons.FeatherIcons
import compose.icons.feathericons.Home
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal object HomeTab : Tab, KoinComponent {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(FeatherIcons.Home)

            return remember {
                TabOptions(
                    index = 0u,
                    title = "Home",
                    icon = icon

                )
            }
        }

    @Composable
    override fun Content() {
        val viewModel = get<SharedViewModel>()
        Navigator(HomeScreen(), onBackPressed = {
            viewModel.showBackIcon.value = false
            true
        })
    }
}