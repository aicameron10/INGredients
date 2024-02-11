package com.recipe.database.di

import com.recipe.database.DriverFactory
import com.recipe.database.DatabaseRepository
import org.koin.dsl.module

actual val kmmDatabaseModule = module {
    single {
        DriverFactory()
    }
    single {
        DatabaseRepository()
    }
}
