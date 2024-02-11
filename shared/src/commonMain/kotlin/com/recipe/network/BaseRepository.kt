package com.recipe.network

import com.recipe.network.model.response.APIError
import com.recipe.utils.NetworkDispatcher
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.charsets.Charsets
import io.ktor.utils.io.errors.IOException
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

abstract class BaseRepository {

    companion object {
        const val NOT_CONNECTED = -1
    }

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun <T> processResponse(apiToBeCalled: suspend () -> T, decodeApiError: Boolean = false): Response<T> {
        return withContext(NetworkDispatcher) {
            try {
                val response = apiToBeCalled()
                Response.Success(response)
            } catch (ex: ResponseException) {
                if (decodeApiError) {
                    try {
                        val content = ex.response.bodyAsText(Charsets.UTF_8)
                        val apiError = Json.decodeFromString<List<APIError>>(content)
                        Response.RecipeAPIError(apiError)
                    } catch (decodingException: Exception) {
                        decodingException.printStackTrace()
                        Response.HttpError(ex.response.status.value)
                    }
                }  else {
                    Response.HttpError(ex.response.status.value)
                }
            } catch (ex: IOException) {
                Response.ConnectionError(NOT_CONNECTED)
            } catch (ex: SerializationException) {
                Response.SerializationError()
            }  catch (ex: Exception) {
                Response.UnknownError()
            }
        }
    }

    suspend fun processHttpResponseCode(apiToBeCalled: suspend () -> HttpResponse): Response<Int> {
        return withContext(NetworkDispatcher) {
            try {
                val response = apiToBeCalled()
                Response.Success(response.status.value)
            } catch (ex: ResponseException) {
                Response.HttpError(ex.response.status.value)
            } catch (ex: Exception) {
                Response.HttpError(NOT_CONNECTED)
            }
        }
    }
}
