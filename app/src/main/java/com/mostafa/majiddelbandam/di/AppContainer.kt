package com.mostafa.majiddelbandam.di

import android.content.Context
import androidx.compose.runtime.staticCompositionLocalOf
import com.mostafa.majiddelbandam.data.local.PlayerStore
import com.mostafa.majiddelbandam.data.local.PuzzleCatalog
import com.mostafa.majiddelbandam.data.repository.GameRepository

class AppContainer(context: Context) {
    private val catalog = PuzzleCatalog(context)
    private val playerStore = PlayerStore(context)
    val repository = GameRepository(catalog, playerStore)
}

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer not provided")
}
