package com.recipe.viewmodels

import com.recipe.database.DatabaseRepository
import com.recipe.network.api.RecipeRepository
import com.recipe.network.model.response.RecipeInfoResponse
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RecipeDetailViewModelTest {

    private lateinit var viewModel: RecipeDetailViewModel
    private val recipeRepository = mockk<RecipeRepository>(relaxed = true)
    private val databaseRepository = mockk<DatabaseRepository>(relaxed = true)

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = RecipeDetailViewModel(recipeRepository, databaseRepository, Dispatchers.Main)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the main dispatcher to the original Main dispatcher
    }

    @Test
    fun `toggle favourite updates isFavourite state and database`() {
        viewModel.toggleFavourite(1L)

        assertEquals(1, viewModel.isFavourite.value)

        // Verify that the database update function is called with the correct arguments
        verify { databaseRepository.database.recipesQueries.updateFavourite(1, 1L) }
    }

    @Test
    fun `saveRecipe saves recipe information to database`() {
        // Assuming saveRecipe does some database operations
        val recipeInfo = mockk<RecipeInfoResponse>(relaxed = true)

        viewModel.saveRecipe(recipeInfo, 1, "Test Recipe", "image_url")

        // Verify that database operations were called
        verify(exactly = 1) { databaseRepository.database.recipesQueries.insertRecipeItems(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }
}


