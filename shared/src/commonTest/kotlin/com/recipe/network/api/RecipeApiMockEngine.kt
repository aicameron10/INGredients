package com.recipe.network.api

import com.recipe.network.api.RecipeRepositoryTest.Companion.ID
import com.recipe.utils.BaseApiMockEngine
import io.ktor.client.*
import io.ktor.client.engine.mock.*

class RecipeApiMockEngine : BaseApiMockEngine() {

    override val httpMockClient = HttpClient(MockEngine) {
        engine {
            addHandler { request ->

                val statusCode = getStatusCode()

                when (request.url.encodedPath) {
                    RecipeApi.RECIPES -> {
                        respond("{}", statusCode, responseHeaders)
                    }
                    RecipeApi.AUTO_COMPLETE -> {
                        respond("[]", statusCode, responseHeaders)
                    }
                    RecipeApi.RECIPES_INFO + ID + RecipeApi.INFO -> {
                        respond("{}", statusCode, responseHeaders)
                    }
                    else -> {
                        error("Unhandled: ${request.url.encodedPath}")
                    }
                }
            }
        }
        addSerializer()
    }
}
