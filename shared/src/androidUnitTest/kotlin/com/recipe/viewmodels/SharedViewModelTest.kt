package com.recipe.viewmodels

import com.recipe.database.DatabaseRepository
import com.recipe.network.Response
import com.recipe.network.api.RecipeRepository
import com.recipe.network.model.request.RecipeRequest
import com.recipe.network.model.response.AutoCompleteResponse
import com.recipe.network.model.response.RecipeResponse
import comrecipe.RecipeInfo
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SharedViewModelTest {

    private lateinit var viewModel: SharedViewModel
    private val recipeRepository = mockk<RecipeRepository>(relaxed = true)
    private val databaseRepository = mockk<DatabaseRepository>(relaxed = true)

    @Before
    fun setUp() {
        // Use a test dispatcher for coroutines
        Dispatchers.setMain(TestCoroutineDispatcher())

        every {
            databaseRepository.database.recipesQueries.selectAllRecipeList().executeAsList()
        } returns listOf(
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
                0
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
                0
            )
        )

        viewModel = SharedViewModel(
            recipeRepository = recipeRepository,
            databaseRepository = databaseRepository,
            mainDispatcher = Dispatchers.Main
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getRecipes fetches data and updates state correctly`() {
        // Given
        val recipeRequest = RecipeRequest(
            authorization = "auth",
            query = "Pasta"
        )
        val expectedResponse = Response.Success(RecipeResponse())

        coEvery { recipeRepository.getRecipes(recipeRequest) } returns expectedResponse

        // When
        viewModel.getRecipes(recipeRequest)

        // Then
        coVerify(exactly = 1) { recipeRepository.getRecipes(recipeRequest) }
        assertEquals(expectedResponse, viewModel.recipeObserver.value)
    }

    @Test
    fun `getAutoComplete updates autoCompleteObserver with response`() = runBlockingTest {
        // Given
        val recipeRequest = RecipeRequest(
            authorization = "auth",
            query = "Pasta"
        )
        val expectedResponse =
            Response.Success(listOf(AutoCompleteResponse(), AutoCompleteResponse()))

        coEvery { recipeRepository.getAutoComplete(recipeRequest) } returns expectedResponse

        // When
        viewModel.getAutoComplete(recipeRequest)

        // Then
        coVerify(exactly = 1) { recipeRepository.getAutoComplete(recipeRequest) }
        assertEquals(expectedResponse, viewModel.autoCompleteObserver.value)
    }

    @Test
    fun `loadRecentViewed loads recent viewed recipes into state`() {
        // Assert that the initial state is loaded with the recent recipes
        assertEquals(2, viewModel.recentList.value.size)
        assertEquals("Soup", viewModel.recentList.value[0].title)
        assertEquals("Pasta", viewModel.recentList.value[1].title)
    }
}

