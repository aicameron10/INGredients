package com.recipe.database.di

import com.recipe.database.DatabaseRepository
import com.recipe.database.DriverFactory
import org.koin.dsl.module

actual val kmmDatabaseModule = module {
    single {
        DriverFactory(get())
    }
    single {
        DatabaseRepository()
    }
}
