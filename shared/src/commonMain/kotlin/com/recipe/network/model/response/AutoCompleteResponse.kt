package com.recipe.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AutoCompleteResponse(
    var id: Int? = 0,
    var title: String? = null,
    var imageType: String? = null
)