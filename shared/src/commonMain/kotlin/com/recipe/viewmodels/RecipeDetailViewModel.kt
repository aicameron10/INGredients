package com.recipe.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) {

    var recipeInfo by mutableStateOf<RecipeInfoResponse?>(null)

    private val _recipeInfo: MutableStateFlow<Response<RecipeInfoResponse>?> =
        MutableStateFlow(null)
    val recipeInfoObserver: StateFlow<Response<RecipeInfoResponse>?> = _recipeInfo

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

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
