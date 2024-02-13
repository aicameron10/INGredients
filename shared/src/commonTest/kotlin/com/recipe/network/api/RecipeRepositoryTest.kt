package com.recipe.network.api

import com.recipe.network.Response
import com.recipe.network.model.request.RecipeRequest
import com.recipe.utils.BaseApiMockEngine
import com.recipe.utils.PlatformManager
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RecipeRepositoryTest : KoinTest {

    companion object {
        const val ID = "id"
    }

    @BeforeTest
    fun setUp() {
        stopKoin()
        // Start Koin with test modules
        startKoin {
            modules(module {
                single { PlatformManager() }
                single { RecipeApi() }
                single { RecipeApiMockEngine() }
                single {
                    val mockEngine: RecipeApiMockEngine = get()
                    mockEngine.httpMockClient
                }
                single { RecipeRepository() }
            })
        }
    }

    private val recipeRepository: RecipeRepository by inject()
    private val recipeApiMockEngine: RecipeApiMockEngine by inject()

    @AfterTest
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun testGetRecipesFailure() = runTest {
        val request = RecipeRequest(
            query = "pasta",
            sort = "calories"
        )

        recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.HTTP_ERROR_CODE

        val response = recipeRepository.getRecipes(request)

        assertEquals(
            HttpStatusCode.InternalServerError.value,
            response.httpStatusErrorCode
        )
    }

    @Test
    fun testGetRecipesSuccess() = runTest {
        val request = RecipeRequest(
            query = "pasta",
            sort = "calories"
        )
        recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.SUCCESS

        val response = recipeRepository.getRecipes(request)

        assertNotNull(response.data)
    }

    @Test
    fun testGetRecipesNotConnected() = runTest {
        val request = RecipeRequest(
            query = "pasta",
            sort = "calories"
        )
        recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.CONNECTION_ERROR

        val response = recipeRepository.getRecipes(request)

        assertTrue(response is Response.ConnectionError)
    }


    @Test
    fun testGetAutoCompleteFailure() = runTest {
        val request = RecipeRequest(
            query = "pasta",
            sort = "calories"
        )

        recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.HTTP_ERROR_CODE

        val response = recipeRepository.getAutoComplete(request)

        assertEquals(
            HttpStatusCode.InternalServerError.value,
            response.httpStatusErrorCode
        )
    }

    @Test
    fun testGetAutoCompleteSuccess() = runTest {
        val request = RecipeRequest(
            query = "pasta",
            sort = "calories"
        )
        recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.SUCCESS

        val response = recipeRepository.getAutoComplete(request)

        assertNotNull(response.data)
    }

    @Test
    fun testGetAutoCompleteNotConnected() = runTest {
        val request = RecipeRequest(
            query = "pasta",
            sort = "calories"
        )
        recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.CONNECTION_ERROR

        val response = recipeRepository.getAutoComplete(request)

        assertTrue(response is Response.ConnectionError)
    }


    @Test
    fun testGetRecipeInformationFailure() = runTest {
        val request = RecipeRequest(
            id = ID,
            query = "pasta",
            sort = "calories"
        )

        recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.HTTP_ERROR_CODE

        val response = recipeRepository.getRecipeInformation(request)

        assertEquals(
            HttpStatusCode.InternalServerError.value,
            response.httpStatusErrorCode
        )
    }

    @Test
    fun testGetRecipeInformationSuccess() = runTest {
        val request = RecipeRequest(
            id = ID,
            query = "pasta",
            sort = "calories"
        )
        recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.SUCCESS

        val response = recipeRepository.getRecipeInformation(request)

        assertNotNull(response.data)
    }

    @Test
    fun testGetRecipeInformationNotConnected() = runTest {
        val request = RecipeRequest(
            id = ID,
            query = "pasta",
            sort = "calories"
        )
        recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.CONNECTION_ERROR

        val response = recipeRepository.getRecipeInformation(request)

        assertTrue(response is Response.ConnectionError)
    }
}

