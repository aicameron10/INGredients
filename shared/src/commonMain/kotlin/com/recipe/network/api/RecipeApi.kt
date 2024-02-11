package com.recipe.network.api

import com.recipe.utils.PlatformManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RecipeApi : KoinComponent {

    companion object {
        const val RECIPES = "/recipes/complexSearch"
        const val RECIPES_INFO = "/recipes/"
        const val INFO = "/information"
        const val AUTO_COMPLETE = "/recipes/autocomplete"
    }

    private val platformManager: PlatformManager by inject()

    fun getRecipes(): String {
        return platformManager.getBaseUrl() + RECIPES
    }

    fun getRecipeInformation(id: String): String {
        return platformManager.getBaseUrl() + RECIPES_INFO + id + INFO
    }

    fun getAutoComplete(): String {
        return platformManager.getBaseUrl() + AUTO_COMPLETE
    }
}
