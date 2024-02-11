package com.recipe.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class RecipeResponse(
    var offset: Int? = 0,
    var number: Int? = 0,
    var results: List<ResultData> = emptyList(),
    var totalResults: Int? = 0
)
@Serializable
data class ResultData(
    var id: Int? = 0,
    var title: String? = null,
    var image: String? = null,
    var imageType: String? = null
)