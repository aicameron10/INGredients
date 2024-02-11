package com.recipe.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class RecipeInfoResponse(
    var id: Int? = 0,
    var title: String? = null,
    var image: String? = null,
    var imageType: String? = null,
    var servings: Int? = 0,
    var readyInMinutes: Int? = 0,
    var license: String? = null,
    var sourceName: String? = null,
    var sourceUrl: String? = null,
    var spoonacularScore: Double? = 0.0,
    var spoonacularSourceUrl: String? = null,
    var sustainable: Boolean? = null,
    var vegan: Boolean? = null,
    var vegetarian: Boolean? = null,
    var veryHealthy: Boolean? = null,
    var veryPopular: Boolean? = null,
    var dishTypes: List<String> = emptyList(),
    var extendedIngredients: List<ExtendedIngredients> = emptyList(),
    var analyzedInstructions: List<InstructionsList> = emptyList(),
    var summary: String? = null
)

@Serializable
data class ExtendedIngredients(
    var aisle: String? = null,
    var amount: Double? = 0.0,
    var consitency: String? = null,
    var id: Int? = 0,
    var image: String? = null,
    var measures: Measures? = null,
    var meta: List<String> = emptyList(),
    var name: String? = null,
    var original: String? = null,
    var originalName: String? = null,
    var unit: String? = null
)

@Serializable
data class Measures(
    var metric: Metric? = null,
    var us: Us? = null,
)

@Serializable
data class Metric(
    var amount: Double? = 0.0,
    var unitLong: String? = null,
    var unitShort: String? = null
)

@Serializable
data class Us(
    var amount: Double? = 0.0,
    var unitLong: String? = null,
    var unitShort: String? = null
)

@Serializable
data class InstructionsList(
    var name: String? = null,
    var steps: List<Steps> = emptyList(),
)

@Serializable
data class Steps(
    var equipment: List<Equipment> = emptyList(),
    var ingredients: List<Ingredients> = emptyList(),
    var number: Int? = 0,
    var step: String? = null
)

@Serializable
data class Equipment(
    var id: Int? = 0,
    var image: String? = null,
    var name: String? = null,
    var temperature: Temperature? = null
)

@Serializable
data class Temperature(
    var number: Double? = 0.0,
    var unit: String? = null
)

@Serializable
data class Ingredients(
    var id: Int? = 0,
    var image: String? = null,
    var name: String? = null
)
