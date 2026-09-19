package com.mostafa.majiddelbandam.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mostafa.majiddelbandam.di.LocalAppContainer
import com.mostafa.majiddelbandam.domain.PlayerProgress
import com.mostafa.majiddelbandam.ui.game.GameViewModel
import com.mostafa.majiddelbandam.ui.screens.game.GameScreen
import com.mostafa.majiddelbandam.ui.screens.map.NeighborhoodMapScreen
import com.mostafa.majiddelbandam.ui.screens.shop.ShopScreen
import com.mostafa.majiddelbandam.ui.screens.wheel.FortuneWheelScreen

@Composable
fun MajidNavGraph() {
    val navController = rememberNavController()
    val repository = LocalAppContainer.current.repository
    val progress by repository.progress.collectAsStateWithLifecycle(initialValue = PlayerProgress())

    NavHost(navController = navController, startDestination = Routes.MAP) {
        composable(Routes.MAP) {
            NeighborhoodMapScreen(
                progress = progress,
                repository = repository,
                onPlay = { id -> navController.navigate(Routes.game(id)) },
                onShop = { navController.navigate(Routes.SHOP) },
                onWheel = { navController.navigate(Routes.WHEEL) },
                onDaily = { navController.navigate(Routes.game(GameViewModel.DAILY_ID)) }
            )
        }
        composable(
            route = Routes.GAME,
            arguments = listOf(navArgument("puzzleId") { type = NavType.IntType })
        ) { entry ->
            val id = entry.arguments?.getInt("puzzleId") ?: 1
            GameScreen(
                puzzleId = id,
                onBack = { navController.popBackStack() },
                onMap = {
                    navController.navigate(Routes.MAP) {
                        popUpTo(Routes.MAP) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onNext = { nextId ->
                    navController.navigate(Routes.game(nextId)) {
                        popUpTo(Routes.GAME) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.SHOP) {
            ShopScreen(
                progress = progress,
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.WHEEL) {
            FortuneWheelScreen(
                progress = progress,
                repository = repository,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
