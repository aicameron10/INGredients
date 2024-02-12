package com.recipe.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.recipe.database.DatabaseRepository
import com.recipe.multiplatformsettings.SessionManager
import com.recipe.network.Response
import com.recipe.network.api.RecipeRepository
import com.recipe.network.model.request.RecipeRequest
import com.recipe.network.model.response.AutoCompleteResponse
import com.recipe.network.model.response.RecipeResponse
import com.recipe.network.model.response.ResultData
import comrecipe.RecipeInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavouriteViewModel(
    private val databaseRepository: DatabaseRepository
) {

    private val _favouriteList = mutableStateOf<List<RecipeInfo>>(listOf())
    val favouriteList: State<List<RecipeInfo>> = _favouriteList

    init {
        loadFavourite()
    }

    fun loadFavourite() {
        _favouriteList.value = databaseRepository.database.recipesQueries.selectAllRecipeListFav().executeAsList().reversed()
    }
}
