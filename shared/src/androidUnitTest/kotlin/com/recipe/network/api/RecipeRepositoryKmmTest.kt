package com.recipe.network.api

import com.recipe.network.Response
import com.recipe.network.model.request.RecipeRequest
import com.recipe.utils.BaseApiMockEngine
import com.recipe.utils.PlatformManager
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.core.component.inject
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeRepositoryKmmTest : KoinTest {

    companion object {
        const val ID = "id"
    }

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(
            module {
                single { PlatformManager() }
                single { RecipeApi() }
                single { RecipeApiMockEngine() }
                single {
                    val mockEngine: RecipeApiMockEngine = get()
                    mockEngine.httpMockClient
                }
                single { RecipeRepository() }
            }
        )
    }

    private val recipeRepository: RecipeRepository by inject()
    private val recipeApiMockEngine: RecipeApiMockEngine by inject()

    @Nested
    inner class GetRecipes {
        @Test
        fun testGetRecipesFailure() = runTest {
             val request = RecipeRequest(
                query = "pasta",
                sort = "calories"
            )

            recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.HTTP_ERROR_CODE

            val response = recipeRepository.getRecipes(request)

            Assertions.assertEquals(HttpStatusCode.InternalServerError.value, response.httpStatusErrorCode)
        }

        @Test
        fun testGetRecipesSuccess() = runTest {
            val request = RecipeRequest(
                query = "pasta",
                sort = "calories"
            )
            recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.SUCCESS

            val response = recipeRepository.getRecipes(request)

            Assertions.assertNotNull(response.data)
        }

        @Test
        fun testGetRecipesNotConnected() = runTest {
            val request = RecipeRequest(
                query = "pasta",
                sort = "calories"
            )
            recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.CONNECTION_ERROR

            val response = recipeRepository.getRecipes(request)

            MatcherAssert.assertThat(response, CoreMatchers.instanceOf(Response.ConnectionError::class.java))
        }
    }

    @Nested
    inner class GetAutoComplete {
        @Test
        fun testGetAutoCompleteFailure() = runTest {
            val request = RecipeRequest(
                query = "pasta",
                sort = "calories"
            )

            recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.HTTP_ERROR_CODE

            val response = recipeRepository.getAutoComplete(request)

            Assertions.assertEquals(HttpStatusCode.InternalServerError.value, response.httpStatusErrorCode)
        }

        @Test
        fun testGetAutoCompleteSuccess() = runTest {
            val request = RecipeRequest(
                query = "pasta",
                sort = "calories"
            )
            recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.SUCCESS

            val response = recipeRepository.getAutoComplete(request)

            Assertions.assertNotNull(response.data)
        }

        @Test
        fun testGetAutoCompleteNotConnected() = runTest {
            val request = RecipeRequest(
                query = "pasta",
                sort = "calories"
            )
            recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.CONNECTION_ERROR

            val response = recipeRepository.getAutoComplete(request)

            MatcherAssert.assertThat(response, CoreMatchers.instanceOf(Response.ConnectionError::class.java))
        }
    }

    @Nested
    inner class GetRecipeInformation {
        @Test
        fun testGetRecipeInformationFailure() = runTest {
            val request = RecipeRequest(
                id = ID,
                query = "pasta",
                sort = "calories"
            )

            recipeApiMockEngine.connectionType = BaseApiMockEngine.ConnectionType.HTTP_ERROR_CODE

            val response = recipeRepository.getRecipeInformation(request)

            Assertions.assertEquals(HttpStatusCode.InternalServerError.value, response.httpStatusErrorCode)
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

            Assertions.assertNotNull(response.data)
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

            MatcherAssert.assertThat(response, CoreMatchers.instanceOf(Response.ConnectionError::class.java))
        }
    }
}
