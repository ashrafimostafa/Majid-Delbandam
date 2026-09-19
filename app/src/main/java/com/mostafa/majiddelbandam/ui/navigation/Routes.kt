package com.mostafa.majiddelbandam.ui.navigation

object Routes {
    const val MAP = "map"
    const val GAME = "game/{puzzleId}"
    const val SHOP = "shop"
    const val WHEEL = "wheel"

    fun game(puzzleId: Int) = "game/$puzzleId"
}
