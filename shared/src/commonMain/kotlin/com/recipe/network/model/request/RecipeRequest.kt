package com.recipe.network.model.request

import com.recipe.utils.Constants.API_KEY
import kotlinx.serialization.Serializable

@Serializable
data class RecipeRequest(
    var authorization: String? = API_KEY,
    var query: String? = null,
    var type: String? = null,
    var sort: String? = null,
    var id: String? = null
)