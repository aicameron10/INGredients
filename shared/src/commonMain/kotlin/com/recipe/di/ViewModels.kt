package com.recipe.di

import com.recipe.viewmodels.RecipeDetailViewModel
import com.recipe.viewmodels.SharedViewModel
import org.koin.dsl.module

val viewModelsModule = module {
    single { SharedViewModel(get(), get(), get()) }
    single { RecipeDetailViewModel(get()) }
}
