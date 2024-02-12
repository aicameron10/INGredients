package com.recipe.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.recipe.database.DatabaseRepository
import com.recipe.network.Response
import com.recipe.network.api.RecipeRepository
import com.recipe.network.model.request.RecipeRequest
import com.recipe.network.model.response.Equipment
import com.recipe.network.model.response.ExtendedIngredients
import com.recipe.network.model.response.Ingredients
import com.recipe.network.model.response.InstructionsList
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


    fun saveRecipe(
        data: RecipeInfoResponse?,
        recipeId: Int,
        recipeTitle: String,
        recipeImage: String
    ) {
        try {
            data.let {
                databaseRepository.database.recipesQueries.insertRecipeItems(
                    id = recipeId.toLong(),
                    title = recipeTitle,
                    image = recipeImage,
                    servings = it?.servings?.toLong(),
                    readyInMinutes = it?.readyInMinutes?.toLong(),
                    sourceName = it?.sourceName,
                    summary = it?.summary,
                    sourceUrl = it?.sourceUrl,
                    spoonacularScore = it?.spoonacularScore?.toLong(),
                    favourite = 0,
                )
            }

            data?.analyzedInstructions?.getOrNull(0)?.steps?.forEach { step ->
                databaseRepository.database.recipesQueries.insertInstructionsItems(
                    number = step.number?.toLong(),
                    step = step.step,
                    ingredients = step.ingredients.joinToString { it.name.toString() },
                    equipment = step.equipment.joinToString { it.name.toString() },
                    recipeId = recipeId.toLong()
                )
            }

            data?.extendedIngredients?.forEach {
                databaseRepository.database.recipesQueries.insertIngredientsItems(
                    amount = it.amount?.toLong(),
                    originalName = it.original,
                    unit = it.unit,
                    recipeId = recipeId.toLong()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getDataBaseList(recipeId: Int): RecipeInfoResponse {
        val newInfo = RecipeInfoResponse()
        try {
            val databaseList = databaseRepository.database.recipesQueries.selectAllRecipes(
                recipeId.toLong()
            ).executeAsList()

            val instructionList = databaseRepository.database.recipesQueries.selectAllInstructions(
                recipeId.toLong()
            ).executeAsList()

            val ingredientList = databaseRepository.database.recipesQueries.selectAllIngredients(
                recipeId.toLong()
            ).executeAsList()

            val newInstructions = InstructionsList()
            val step = newInstructions.steps.getOrNull(0)
            val ingredient = newInstructions.steps.getOrNull(0)?.ingredients
            val equipment = newInstructions.steps.getOrNull(0)?.equipment

            val newEquipment = ArrayList<Equipment>()
            equipment?.forEach {
                val newEquip = Equipment(name = it.name)
                newEquipment.add(newEquip)
            }
            val newIngredient = ArrayList<Ingredients>()
            ingredient?.forEach {
                val newIng = Ingredients(name = it.name)
                newIngredient.add(newIng)
            }

            instructionList.forEach {
                step?.step = it.step
                step?.equipment = newEquipment
                step?.ingredients = newIngredient
                step?.number = it.number?.toInt()
            }

            val newIngredients = ExtendedIngredients()
            ingredientList.forEach {
                newIngredients.originalName = it.originalName
                newIngredients.amount = it.amount?.toDouble()
                newIngredients.unit = it.unit
            }


            databaseList.forEach {
                newInfo.id = it.id.toInt()
                newInfo.image = it.image
                newInfo.readyInMinutes = it.readyInMinutes?.toInt()
                newInfo.servings = it.servings?.toInt()
                newInfo.sourceName = it.sourceName
                newInfo.spoonacularScore = it.spoonacularScore?.toDouble()
                newInfo.summary = it.summary
                newInfo.title = it.title
                newInfo.sourceUrl = it.sourceUrl
                newInfo.favourite = it.favourite?.toInt()
                //newInfo.analyzedInstructions = instructionList
                // newInfo.extendedIngredients = newIngredient
            }
            _isLoading.value = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return newInfo
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
