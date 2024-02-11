package com.recipe.utils

data class PlatformManager(
    var configVariant: ConfigVariant? = null
) {

    fun getBaseUrl(): String {
        return when (configVariant) {
            ConfigVariant.PROD -> {
                "https://api.spoonacular.com"
            }
            ConfigVariant.DEV -> {
                "https://api.spoonacular.com"
            }
            else -> {
                "https://api.spoonacular.com"
            }
        }
    }
}
