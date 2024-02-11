package com.recipe.network.api

import com.recipe.network.BaseRepository
import com.recipe.network.Response
import com.recipe.network.model.request.RecipeRequest
import com.recipe.network.model.response.AutoCompleteResponse
import com.recipe.network.model.response.RecipeInfoResponse
import com.recipe.network.model.response.RecipeResponse
import com.recipe.utils.KmmNetworkConstants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecipeRepository : BaseRepository(), KoinComponent {

    private val recipeApi: RecipeApi by inject()
    private val httpClient: HttpClient by inject()

    suspend fun getRecipes(
        recipeRequest: RecipeRequest
    ): Response<RecipeResponse> {
        return processResponse(
            apiToBeCalled = {
                httpClient.get(recipeApi.getRecipes()) {
                    formData {
                        parameter(KmmNetworkConstants.PARAM_QUERY, recipeRequest.query)
                        parameter(KmmNetworkConstants.PARAM_SORT, recipeRequest.sort)
                        parameter(KmmNetworkConstants.PARAM_API_KEY, recipeRequest.authorization)
                    }
                    headers {
                        append(KmmNetworkConstants.PARAM_CHARSET, KmmNetworkConstants.VALUE_UTF_8)
                    }
                    contentType(ContentType.Application.Json)
                }.body()
            }
        )
    }

    suspend fun getRecipeInformation(
        recipeRequest: RecipeRequest
    ): Response<RecipeInfoResponse> {
        return processResponse(
            apiToBeCalled = {
                httpClient.get(recipeApi.getRecipeInformation(recipeRequest.id.toString())) {
                    formData {
                        parameter(KmmNetworkConstants.PARAM_API_KEY, recipeRequest.authorization)
                    }
                    headers {
                        append(KmmNetworkConstants.PARAM_CHARSET, KmmNetworkConstants.VALUE_UTF_8)
                    }
                    contentType(ContentType.Application.Json)
                }.body()
            }
        )
    }

    suspend fun getAutoComplete(
        recipeRequest: RecipeRequest
    ): Response<List<AutoCompleteResponse>> {
        return processResponse(
            apiToBeCalled = {
                httpClient.get(recipeApi.getAutoComplete()) {
                    formData {
                        parameter(KmmNetworkConstants.PARAM_QUERY, recipeRequest.query)
                        parameter(KmmNetworkConstants.PARAM_API_KEY, recipeRequest.authorization)
                    }
                    headers {
                        append(KmmNetworkConstants.PARAM_CHARSET, KmmNetworkConstants.VALUE_UTF_8)
                    }
                    contentType(ContentType.Application.Json)
                }.body()
            }
        )
    }
}