package com.recipe.viewmodels

import com.recipe.database.DatabaseRepository
import comrecipe.RecipeInfo
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import io.mockk.junit5.MockKExtension

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
class FavouriteViewModelTest {

    private lateinit var databaseRepository: DatabaseRepository
    private lateinit var viewModel: FavouriteViewModel

    @BeforeEach
    fun setUp() {
        // Mock the DatabaseRepository
        databaseRepository = mockk(relaxed = true)

        // Prepare a fake list of RecipeInfo objects as a sample response
        val fakeFavouriteRecipes = listOf(
            RecipeInfo(1, "Pasta", "image", 2, 30, "source", "summary", "www.website.com", 80, 1),
            RecipeInfo(2, "Pasta", "image", 4, 45, "source", "summary", "www.website.com", 80, 1),
        )

        // Mocking the behavior of databaseRepository to return the fake list when queried
        coEvery {
            databaseRepository.database.recipesQueries.selectAllRecipeListFav().executeAsList()
                .reversed()
        } returns fakeFavouriteRecipes

        // Initialize FavouriteViewModel with the mocked databaseRepository
        viewModel = FavouriteViewModel(databaseRepository)
    }

    @Test
    @DisplayName("loadFavourite successfully updates favouriteList")
    fun testLoadFavouriteUpdatesListCorrectly() = runTest {
        // Trigger the action to load favourite recipes
        viewModel.loadFavourite()

        assertEquals(
            2,
            viewModel.favouriteList.value.size,
            "Favourite list should contain the expected number of items."
        )
        assertTrue(
            viewModel.favouriteList.value.containsAll(
                listOf(
                    RecipeInfo(
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
                        "Pasta",
                        "image",
                        4,
                        45,
                        "source",
                        "summary",
                        "www.website.com",
                        80,
                        1
                    ),
                )
            ), "Favourite list should match the expected recipes."
        )
    }
}

