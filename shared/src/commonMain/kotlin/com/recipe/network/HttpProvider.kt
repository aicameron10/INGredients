package com.recipe.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

expect class HttpProvider() {
    internal fun getHttpClient(config: HttpClientConfig<*>.() -> Unit): HttpClient
}
