package com.recipe.utils

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.utils.io.errors.*
import kotlinx.serialization.json.Json

abstract class BaseApiMockEngine {

    abstract val httpMockClient: HttpClient

    val responseHeaders =
        headersOf(HttpHeaders.ContentType to listOf(ContentType.Application.Json.toString()))

    var connectionType: ConnectionType? = null
        get() = field
            ?: throw IllegalStateException("BaseApiMockEngine has not beet initialized")

    enum class ConnectionType {
        SUCCESS,
        HTTP_ERROR_CODE,
        CONNECTION_ERROR
    }

    fun getStatusCode(): HttpStatusCode {
        val statusCode = when (connectionType) {
            ConnectionType.SUCCESS -> {
                HttpStatusCode.OK
            }
            ConnectionType.HTTP_ERROR_CODE -> {
                HttpStatusCode.InternalServerError
            }
            ConnectionType.CONNECTION_ERROR -> {
                throw IOException("Connection Issue")
            }
            null -> {
                throw IllegalStateException("BaseApiMockEngine connectionType has not beet initialized")
            }
        }
        return statusCode
    }

    fun HttpClientConfig<MockEngineConfig>.addSerializer() {
        expectSuccess = true
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    useAlternativeNames = false
                }
            )
        }
    }
}
