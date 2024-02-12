package com.recipe.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.recipe.database.DatabaseRepository
import comrecipe.RecipeInfo

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

    fun updateFavourite(id: Long){
        databaseRepository.database.recipesQueries.updateFavourite(
            favourite = 0,
            id = id,
        )
        loadFavourite()
    }
}
