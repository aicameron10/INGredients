package com.recipe.viewmodels

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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SharedViewModel(
    private val databaseRepository: DatabaseRepository,
    private val recipeRepository: RecipeRepository,
    private val sessionManager: SessionManager,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) {

    var hasFetchedRecipe = false
    var lastSearchedText = ""

    val showBackIcon = mutableStateOf(false)
    val topBarTitle = mutableStateOf("INGredients")

    var recipeList by mutableStateOf<List<ResultData>?>(null)

    private val _getRecipeResponse: MutableStateFlow<Response<RecipeResponse>?> = MutableStateFlow(null)
    val recipeObserver: StateFlow<Response<RecipeResponse>?> = _getRecipeResponse

    private val _getAutoCompleteResponse: MutableStateFlow<Response<List<AutoCompleteResponse>>?> = MutableStateFlow(null)
    val autoCompleteObserver: StateFlow<Response<List<AutoCompleteResponse>>?> = _getAutoCompleteResponse

    fun fetchRecipeIfNeeded(request: RecipeRequest) {
        if (!hasFetchedRecipe) {
            getRecipes(request)
            hasFetchedRecipe = true
        }
    }

    fun getRecipes(
        recipeRequest: RecipeRequest
    ) {
        CoroutineScope(mainDispatcher).launch {
            val response = recipeRepository.getRecipes(recipeRequest)
            _getRecipeResponse.value = response
        }
    }

    fun getAutoComplete(
        recipeRequest: RecipeRequest
    ) {
        CoroutineScope(mainDispatcher).launch {
            val response = recipeRepository.getAutoComplete(recipeRequest)
            _getAutoCompleteResponse.value = response
        }
    }
}
