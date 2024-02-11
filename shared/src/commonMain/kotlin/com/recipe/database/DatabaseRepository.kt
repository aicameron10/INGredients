package com.recipe.database

import com.recipe.Database
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DatabaseRepository : KoinComponent {
    private val databaseDriverFactory: DriverFactory by inject()
    val database = Database(databaseDriverFactory.createDriver())
}
