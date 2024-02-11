package com.recipe.di

import com.recipe.database.di.kmmDatabaseModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoinKmm(
    appDeclaration: KoinAppDeclaration = {}
) = startKoin {
    appDeclaration()
    modules(
        dataSourceModule(),
        kmmDatabaseModule,
        viewModelsModule
    )
}

