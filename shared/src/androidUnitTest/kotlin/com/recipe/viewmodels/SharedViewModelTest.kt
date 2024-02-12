package com.recipe.viewmodels

import com.recipe.database.DatabaseRepository
import com.recipe.network.api.RecipeRepository
import comrecipe.RecipeInfo
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SharedViewModelTest {

    private lateinit var viewModel: SharedViewModel
    private val recipeRepository = mockk<RecipeRepository>()
    private val databaseRepository = mockk<DatabaseRepository>()
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // Mock the database response for recent recipes
        every { databaseRepository.database.recipesQueries.selectAllRecipeList().executeAsList() } returns listOf(
            RecipeInfo(1, "Pasta", "image",2, 30, "source", "summary","www.website.com",80, 0),
            RecipeInfo(2, "Pasta", "image",4, 45, "source", "summary","www.website.com",80, 0),
            )

        viewModel = SharedViewModel(recipeRepository, databaseRepository, testDispatcher)
    }

    @Test
    fun `loadRecentViewed loads recipes from database`() = runTest {
        viewModel.loadRecentViewed()

        val expected = listOf(
            RecipeInfo(1, "Pasta", "image",2, 30, "source", "summary","www.website.com",80, 0),
            RecipeInfo(2, "Pasta", "image",4, 45, "source", "summary","www.website.com",80, 0),

            ).reversed() // Assuming the original function reverses the list

        assertEquals(expected, viewModel.recentList.value)
    }

}

