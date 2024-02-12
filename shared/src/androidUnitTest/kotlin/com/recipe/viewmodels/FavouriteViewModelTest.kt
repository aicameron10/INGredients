package com.recipe.viewmodels

import com.recipe.database.DatabaseRepository
import comrecipe.RecipeInfo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

class FavouriteViewModelTest {

    private lateinit var databaseRepository: DatabaseRepository
    private lateinit var favouriteViewModel: FavouriteViewModel

    @Before
    fun setUp() {
        // Initialize MockK
        databaseRepository = mockk(relaxed = true)

        // Mock the database calls
        every { databaseRepository.database.recipesQueries.selectAllRecipeListFav().executeAsList() } returns listOf(
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
                "Soup",
                "image",
                2,
                45,
                "source",
                "summary",
                "www.website.com",
                60,
                1
            )
        )

        // Initialize the ViewModel with the mocked repository
        favouriteViewModel = FavouriteViewModel(databaseRepository)
    }

    @Test
    fun `loadFavourite loads favorite recipes into state`() {
        // Assert that the initial state is loaded with the favorite recipes
        assertEquals(2, favouriteViewModel.favouriteList.value.size)
        assertEquals("Soup", favouriteViewModel.favouriteList.value[0].title)
        assertEquals("Pasta", favouriteViewModel.favouriteList.value[1].title)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `updateFavourite updates the favorite status of a recipe`() = runBlockingTest {
        // Prepare the mock to simulate changing the favorite status
        every { databaseRepository.database.recipesQueries.updateFavourite(favourite = 0, id = 1) } answers { }
        every { databaseRepository.database.recipesQueries.selectAllRecipeListFav().executeAsList() } returns listOf(
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
                0
            ) // Assume recipe 1 is no longer a favorite
        )

        // Act: Update favorite status
        favouriteViewModel.updateFavourite(1)

        // Assert: Verify that the favorite list is updated
        assertEquals(1, favouriteViewModel.favouriteList.value.size)
        assertEquals("Soup", favouriteViewModel.favouriteList.value[0].title)

        // Verify that the database update function was called with the correct parameters
        verify { databaseRepository.database.recipesQueries.updateFavourite(favourite = 0, id = 1) }
    }
}

