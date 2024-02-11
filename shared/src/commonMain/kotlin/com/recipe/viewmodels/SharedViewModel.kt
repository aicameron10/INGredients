package com.recipe.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import com.recipe.database.DatabaseRepository
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
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class SharedViewModel(
    private val recipeRepository: RecipeRepository,
    private val databaseRepository: DatabaseRepository,
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main
) {

    var hasFetchedRecipe = false
    var lastSearchedText = ""
    var nav: Navigator? = null

    private val snackBarChannel = Channel<String>()

    val snackBarFlow = snackBarChannel.receiveAsFlow()
    fun showSnackBar(message: String) {
        // Use viewModelScope to launch coroutines in ViewModel
        CoroutineScope(mainDispatcher).launch {
            snackBarChannel.send(message)
        }
    }

    private val _recentList = mutableStateOf<List<RecipeInfo>>(listOf())
    val recentList: State<List<RecipeInfo>> = _recentList

    init {
        loadRecentViewed()
    }

    fun loadRecentViewed() {
        _recentList.value = databaseRepository.database.recipesQueries.selectAllRecipeList().executeAsList().reversed()
    }

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
