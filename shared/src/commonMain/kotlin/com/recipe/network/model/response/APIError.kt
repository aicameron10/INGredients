package com.recipe.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class APIError(
    val code: Int,
    val status: String?,
    val message: String?,
)