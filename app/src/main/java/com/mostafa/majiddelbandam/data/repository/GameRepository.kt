package com.mostafa.majiddelbandam.data.repository

import com.mostafa.majiddelbandam.data.local.PlayerStore
import com.mostafa.majiddelbandam.data.local.PuzzleCatalog
import com.mostafa.majiddelbandam.domain.Helpers
import com.mostafa.majiddelbandam.domain.PersianLetters
import com.mostafa.majiddelbandam.domain.PlayerProgress
import com.mostafa.majiddelbandam.domain.Puzzle
import com.mostafa.majiddelbandam.domain.Victory
import com.mostafa.majiddelbandam.domain.starsForSteps
import kotlinx.coroutines.flow.Flow

class GameRepository(
    private val catalog: PuzzleCatalog,
    private val playerStore: PlayerStore
) {
    val dictionary: Set<String> get() = catalog.dictionary
    val progress: Flow<PlayerProgress> = playerStore.progress

    fun puzzle(id: Int): Puzzle? = catalog.puzzle(id)

    fun dailyPuzzle(): Puzzle? =
        catalog.dailyPuzzleForDay(PersianLetters.localEpochDay())

    suspend fun completeLevel(
        puzzle: Puzzle,
        stepsTaken: Int,
        elapsedMs: Long,
        usedHelp: Boolean,
        wordCount: Int
    ): Victory {
        val optimal = puzzle.steps
        val stars = starsForSteps(stepsTaken, optimal)
        val coins = stars * 8 + if (stars == 3) 12 else 0
        playerStore.recordVictory(puzzle.id, stars, coins)
        if (puzzle.isDaily) {
            playerStore.markDailyPlayed(PersianLetters.localEpochDay())
        }
        return Victory(
            stars = stars,
            coins = coins,
            elapsedMs = elapsedMs,
            quote = com.mostafa.majiddelbandam.domain.MajidQuotes.winning(puzzle.id + stars),
            stepsTaken = stepsTaken,
            optimalSteps = optimal,
            startWord = puzzle.startWord,
            endWord = puzzle.endWord,
            usedHelp = usedHelp,
            wordCount = wordCount
        )
    }

    suspend fun spendOnHelper(price: Int, type: HelperType, count: Int = 1): Boolean {
        if (!playerStore.spendAshrafi(price)) return false
        when (type) {
            HelperType.SCROLL -> playerStore.addHelper(scrolls = count)
            HelperType.CANDLE -> playerStore.addHelper(candles = count)
            HelperType.FAL -> playerStore.addHelper(fals = count)
        }
        return true
    }

    suspend fun grantPack(coins: Int) = playerStore.addAshrafi(coins)

    suspend fun consume(type: HelperType): Boolean = when (type) {
        HelperType.SCROLL -> playerStore.consumeScroll()
        HelperType.CANDLE -> playerStore.consumeCandle()
        HelperType.FAL -> playerStore.consumeFal()
    }

    suspend fun applyWheelPrize(prize: WheelPrize) {
        playerStore.markWheelSpun(PersianLetters.localEpochDay())
        when (prize) {
            is WheelPrize.Coins -> playerStore.addAshrafi(prize.amount)
            is WheelPrize.Helper -> when (prize.type) {
                HelperType.SCROLL -> playerStore.addHelper(scrolls = 1)
                HelperType.CANDLE -> playerStore.addHelper(candles = 1)
                HelperType.FAL -> playerStore.addHelper(fals = 1)
            }
            WheelPrize.Empty -> Unit
        }
    }

    suspend fun setSoundEnabled(enabled: Boolean) = playerStore.setSoundEnabled(enabled)

    fun helpersOf(progress: PlayerProgress) = Helpers(
        scrolls = progress.scrolls,
        candles = progress.candles,
        fals = progress.fals
    )

    fun nextOnPath(puzzle: Puzzle, current: String): String? {
        val idx = puzzle.path.indexOf(current)
        if (idx >= 0 && idx < puzzle.path.lastIndex) return puzzle.path[idx + 1]
        return PersianLetters.neighbors(current, dictionary)
            .filter { PersianLetters.hamming(it, puzzle.endWord) < PersianLetters.hamming(current, puzzle.endWord) }
            .minByOrNull { PersianLetters.hamming(it, puzzle.endWord) }
    }
}

enum class HelperType { SCROLL, CANDLE, FAL }

sealed interface WheelPrize {
    data class Coins(val amount: Int, val label: String) : WheelPrize
    data class Helper(val type: HelperType, val label: String) : WheelPrize
    data object Empty : WheelPrize {
        const val label: String = "لبخند مجید"
    }
}
