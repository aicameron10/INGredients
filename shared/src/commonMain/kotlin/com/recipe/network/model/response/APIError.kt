package com.recipe.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class APIError(
    val code: String,
    val description: String?,
    val detailedErrorMessage: String?,
    val additionalData: String?,
    val displayMode: String?
)