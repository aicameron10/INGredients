package com.recipe.screens.components

sealed class OnBoarding(
    val json: String,
    val title: String,
    val text: String
){
    data object First: OnBoarding(
        json = "first.json",
        title = "Passion",
        text = "Do you want to look like a 'Pro' in the kitchen. Be the Master chef!"
    )
    data object Second: OnBoarding(
        json = "second.json",
        title = "Quality",
        text = "Cook with the best ingredients, and find healthy (but tasty) options."
    )
    data object Third: OnBoarding(
        json = "third.json",
        title = "Ingredients",
        text = "One place to find all the ingredients you have ever wondered what goes into creating your very own master piece."
    )
}
