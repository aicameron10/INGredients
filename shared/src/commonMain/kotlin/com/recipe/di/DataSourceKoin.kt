package com.recipe.di

import com.recipe.multiplatformsettings.SessionManager
import com.recipe.network.HttpProvider
import com.recipe.network.api.RecipeApi
import com.recipe.network.api.RecipeRepository
import com.recipe.utils.ConfigVariant
import com.recipe.utils.PlatformManager
import com.russhwolf.settings.Settings
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun dataSourceModule() = module {
    single { RecipeRepository() }
    single { RecipeApi() }
    single { SessionManager(Settings()) }
    single { PlatformManager(ConfigVariant.PROD) } //ConfigVariant.DEV - ConfigVariant.PROD
    single { HttpProvider() }
    single {
        val provider: HttpProvider = get()
        provider.getHttpClient {
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
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }
        }
    }
}