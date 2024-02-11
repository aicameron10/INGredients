package com.recipe.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.recipe.viewmodels.SharedViewModel
import com.recipe.screens.RecipeScreen
import compose.icons.FeatherIcons
import compose.icons.feathericons.Bookmark
import compose.icons.feathericons.Heart
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

internal object RecipeTab : Tab, KoinComponent {

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(FeatherIcons.Heart)

            return remember {
                TabOptions(
                    index = 1u,
                    title = "Favourites",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val viewModel = get<SharedViewModel>()
        Navigator(RecipeScreen(),onBackPressed = {
            viewModel.showBackIcon.value = false
            true
        })
    }
}