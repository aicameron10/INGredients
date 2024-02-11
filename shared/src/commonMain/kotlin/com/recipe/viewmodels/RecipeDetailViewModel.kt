package com.recipe.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.recipe.database.DatabaseRepository
import com.recipe.network.Response
import com.recipe.network.api.RecipeRepository
import com.recipe.network.model.request.RecipeRequest
import com.recipe.network.model.response.RecipeInfoResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeDetailViewModel(
    private val recipeRepository: RecipeRepository,
    private val databaseRepository: DatabaseRepository,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) {

    var recipeInfo by mutableStateOf<RecipeInfoResponse?>(null)

    private val _recipeInfo: MutableStateFlow<Response<RecipeInfoResponse>?> =
        MutableStateFlow(null)
    val recipeInfoObserver: StateFlow<Response<RecipeInfoResponse>?> = _recipeInfo

    val _isFavourite = MutableStateFlow(0) // 0 = not favourite, 1 = favourite
    val isFavourite: StateFlow<Int> = _isFavourite

    fun toggleFavourite(recipeId: Long?) {
        _isFavourite.value = if (_isFavourite.value == 0) 1 else 0
        recipeId?.let {
            databaseRepository.database.recipesQueries.updateFavourite(
                favourite = _isFavourite.value.toLong(),
                id = it
            )
        }
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    val _isError = MutableStateFlow(false)
    val isNetworkError: StateFlow<Boolean> get() = _isError
    var networkErrorMessage = ""

    fun getRecipeInformation(
        recipeRequest: RecipeRequest
    ) {
        _isLoading.value = true
        CoroutineScope(mainDispatcher).launch {
            val response = recipeRepository.getRecipeInformation(recipeRequest)
            _recipeInfo.value = response
            _isLoading.value = false
        }
    }
}
