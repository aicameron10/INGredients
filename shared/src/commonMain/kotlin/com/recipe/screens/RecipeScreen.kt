package com.recipe.screens

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.recipe.database.DatabaseRepository
import com.recipe.multiplatformsettings.SessionManager
import com.recipe.viewmodels.SharedViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class RecipeScreen : Screen, KoinComponent {

    @ExperimentalMaterialApi
    @Composable
    override fun Content() {
        val viewModel = get<SharedViewModel>()
        val databaseRepository = get<DatabaseRepository>()
        val sessionManager = get<SessionManager>()
    }
}


