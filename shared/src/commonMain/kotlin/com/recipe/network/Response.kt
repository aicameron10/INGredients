package com.recipe.network

import com.recipe.network.model.response.APIError

sealed class Response<T>(
    var data: T? = null,
    val httpStatusErrorCode: Int? = null,
    val apiError: List<APIError>? = null,
) {

    /**
     * Represents successful api calls and parsing.
     */
    class Success<T>(data: T) : Response<T>(data = data)

    /**
     * Represents server (50x) and client (40x) errors.
     */
    class HttpError<T>(errorCode: Int? = null) : Response<T>(httpStatusErrorCode = errorCode)

    /**
     * Represent SerializationExceptions.
     */
    class SerializationError<T> : Response<T>()

    /**
     * Represent IOExceptions and connectivity issues.
     */
    class ConnectionError<T>(errorCode: Int? = null) : Response<T>(httpStatusErrorCode = errorCode)

    /**
     * Represent API Error thrown from service
     */
    class RecipeAPIError<T>(apiError: List<APIError>) : Response<T>(apiError = apiError)

    /**
     * Represent IOExceptions and connectivity issues.
     */
    class UnknownError<T> : Response<T>()

}
