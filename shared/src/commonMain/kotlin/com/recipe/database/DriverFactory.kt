package com.recipe.database

import app.cash.sqldelight.db.SqlDriver
import com.recipe.Database
import comrecipe.RecipesQueries


expect class DriverFactory {
    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()

    return Database(driver)
}

fun doDatabaseThings(driver: SqlDriver) {
    val database = Database(driver)
    val recipeQueries: RecipesQueries = database.recipesQueries
}
