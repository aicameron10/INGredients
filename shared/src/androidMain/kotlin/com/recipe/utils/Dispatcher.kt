package com.recipe.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val NetworkDispatcher: CoroutineDispatcher = Dispatchers.IO
