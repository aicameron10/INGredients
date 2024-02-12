package com.recipe.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.recipe.database.DatabaseRepository
import com.recipe.network.Response
import com.recipe.network.api.RecipeRepository
import com.recipe.network.model.request.RecipeRequest
import com.recipe.network.model.response.Equipment
import com.recipe.network.model.response.ExtendedIngredients
import com.recipe.network.model.response.Ingredients
import com.recipe.network.model.response.InstructionsList
import com.recipe.network.model.response.RecipeInfoResponse
import com.recipe.network.model.response.Steps
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

    var networkErrorMessage = ""
    var recipeInfo by mutableStateOf<RecipeInfoResponse?>(null)

    private val _recipeInfo: MutableStateFlow<Response<RecipeInfoResponse>?> =
        MutableStateFlow(null)
    val recipeInfoObserver: StateFlow<Response<RecipeInfoResponse>?> = _recipeInfo

    val _isFavourite = MutableStateFlow(0) // 0 = not favourite, 1 = favourite
    val isFavourite: StateFlow<Int> = _isFavourite

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    val _isError = MutableStateFlow(false)
    val isNetworkError: StateFlow<Boolean> get() = _isError

    fun toggleFavourite(recipeId: Long?) {
        _isFavourite.value = if (_isFavourite.value == 0) 1 else 0
        recipeId?.let {
            databaseRepository.database.recipesQueries.updateFavourite(
                favourite = _isFavourite.value.toLong(),
                id = it
            )
        }
    }

    fun saveRecipe(
        data: RecipeInfoResponse?,
        recipeId: Int,
        recipeTitle: String,
        recipeImage: String
    ) {
        try {
            data?.also { recipeInfo ->
                databaseRepository.database.recipesQueries.insertRecipeItems(
                    id = recipeId.toLong(),
                    title = recipeTitle,
                    image = recipeImage,
                    servings = recipeInfo.servings?.toLong() ?: 0,
                    readyInMinutes = recipeInfo.readyInMinutes?.toLong() ?: 0,
                    sourceName = recipeInfo.sourceName,
                    summary = recipeInfo.summary,
                    sourceUrl = recipeInfo.sourceUrl,
                    spoonacularScore = recipeInfo.spoonacularScore?.toLong() ?: 0,
                    favourite = 0
                )

                recipeInfo.analyzedInstructions.getOrNull(0)?.steps?.forEach { step ->
                    databaseRepository.database.recipesQueries.insertInstructionsItems(
                        number = step.number?.toLong() ?: 0,
                        step = step.step,
                        ingredients = step.ingredients.joinToString { it.name.orEmpty() },
                        equipment = step.equipment.joinToString { it.name.orEmpty() },
                        recipeId = recipeId.toLong()
                    )
                }

                recipeInfo.extendedIngredients.forEach { ingredient ->
                    databaseRepository.database.recipesQueries.insertIngredientsItems(
                        amount = ingredient.amount?.toLong() ?: 0,
                        originalName = ingredient.original,
                        unit = ingredient.unit,
                        recipeId = recipeId.toLong()
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDatabaseList(recipeId: Int): RecipeInfoResponse {
        val recipeInfoResponse = RecipeInfoResponse()

        try {
            recipeInfoResponse.apply {
                val recipeIdLong = recipeId.toLong()

                val databaseList = databaseRepository.database.recipesQueries.selectAllRecipes(recipeIdLong).executeAsList()
                if (databaseList.isNotEmpty()) {
                    val recipe = databaseList.first()
                    id = recipe.id.toInt()
                    image = recipe.image
                    readyInMinutes = recipe.readyInMinutes?.toInt()
                    servings = recipe.servings?.toInt()
                    sourceName = recipe.sourceName
                    spoonacularScore = recipe.spoonacularScore?.toDouble()
                    summary = recipe.summary
                    title = recipe.title
                    sourceUrl = recipe.sourceUrl
                    favourite = recipe.favourite?.toInt()
                    extendedIngredients = mapIngredients(recipeIdLong)
                    analyzedInstructions = mapInstructions(recipeIdLong)
                }
            }
            _isLoading.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return recipeInfoResponse
    }

    private fun mapIngredients(recipeId: Long): ArrayList<ExtendedIngredients> =
        databaseRepository.database.recipesQueries.selectAllIngredients(recipeId).executeAsList().map {
            ExtendedIngredients(
                originalName = it.originalName,
                amount = it.amount?.toDouble(),
                unit = it.unit
            )
        }.toCollection(ArrayList())

    private fun mapInstructions(recipeId: Long): ArrayList<InstructionsList> {
        val instructionList = databaseRepository.database.recipesQueries.selectAllInstructions(recipeId).executeAsList()
        val instructions = InstructionsList()
        instructions.steps = instructionList.flatMap { instruction ->
            val equipment = instruction.equipment?.split(", ")?.map { Equipment(name = it) } ?: emptyList()
            val ingredients = instruction.ingredients?.split(", ")?.map { Ingredients(name = it) } ?: emptyList()
            listOf(
                Steps(
                    step = instruction.step,
                    equipment = ArrayList(equipment),
                    ingredients = ArrayList(ingredients),
                    number = instruction.number?.toInt()
                )
            )
        }.toCollection(ArrayList())
        return arrayListOf(instructions)
    }

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
