package com.recipe.viewmodels

import com.recipe.database.DatabaseRepository
import com.recipe.network.api.RecipeRepository
import com.recipe.network.model.response.RecipeInfoResponse
import comrecipe.RecipeInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class RecipeDetailViewModelTest {

    private lateinit var viewModel: RecipeDetailViewModel
    private val recipeRepository: RecipeRepository = mockk(relaxed = true)
    private val databaseRepository: DatabaseRepository = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        viewModel = RecipeDetailViewModel(recipeRepository, databaseRepository, Dispatchers.Main)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleFavourite updates favourite status correctly`() {
        val recipeId: Long = 1
        // Setup initial favourite status as not favourite
        viewModel.isFavoured.value = 0

        // Simulate database operation
        coEvery { databaseRepository.database.recipesQueries.updateFavourite(any(), any()) } just runs

        viewModel.toggleFavourite(recipeId)

        // Assert the favourite status is toggled
        assert(viewModel.isFavoured.value == 1)

        // Verify database operation was called
        coVerify { databaseRepository.database.recipesQueries.updateFavourite(1, recipeId) }
    }

    @Test
    fun `isFavourite returns correct favourite status`() {
        val recipeId: Long = 1
        val expectedFavouriteStatus = 1

        // Mock database response
        coEvery { databaseRepository.database.recipesQueries.selectFavouriteInfo(recipeId).executeAsOneOrNull()?.favourite } returns expectedFavouriteStatus.toLong()

        val favouriteStatus = viewModel.isFavourite(recipeId)

        // Assert the returned status matches expected
        assert(favouriteStatus == expectedFavouriteStatus)
    }

    @Test
    fun `saveRecipe saves recipe information correctly`() {
        val recipeInfoResponse = RecipeInfoResponse(id = 123, title = "Test Recipe", image = "image_url")
        val recipeId = 123
        val recipeTitle = "Test Recipe"
        val recipeImage = "image_url"

        // Setup mock to just run without any operation for insert queries
        coEvery { databaseRepository.database.recipesQueries.insertRecipeItems(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } just runs

        viewModel.saveRecipe(recipeInfoResponse, recipeId, recipeTitle, recipeImage)

        // Verify insert operations were called.
        coVerify { databaseRepository.database.recipesQueries.insertRecipeItems(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `getDatabaseList retrieves and sets recipe information correctly`() {
        val recipeId = 1
        // Mock database response with expected data
        coEvery { databaseRepository.database.recipesQueries.selectAllRecipes(any()).executeAsList() } returns listOf(  RecipeInfo(
            1,
            "Pasta",
            "image",
            2,
            30,
            "source",
            "summary",
            "www.website.com",
            80,
            1
        ),
            RecipeInfo(
                2,
                "Soup",
                "image",
                2,
                45,
                "source",
                "summary",
                "www.website.com",
                60,
                1
            ))

        val result = viewModel.getDatabaseList(recipeId)

        // Assertions to verify the retrieved data matches expectations
        assert(result.id == recipeId)

        // Verify database access
        coVerify { databaseRepository.database.recipesQueries.selectAllRecipes(recipeId.toLong()) }
    }
}