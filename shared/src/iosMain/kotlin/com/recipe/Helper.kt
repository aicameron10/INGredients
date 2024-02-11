package com.recipe

import com.recipe.database.di.kmmDatabaseModule
import com.recipe.di.dataSourceModule
import com.recipe.di.viewModelsModule
import org.koin.core.context.startKoin

fun initKoin(){
    startKoin {
        modules(dataSourceModule(), kmmDatabaseModule, viewModelsModule)
    }
}
