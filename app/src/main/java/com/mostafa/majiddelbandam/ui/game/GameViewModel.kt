package com.mostafa.majiddelbandam.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mostafa.majiddelbandam.data.repository.GameRepository
import com.mostafa.majiddelbandam.data.repository.HelperType
import com.mostafa.majiddelbandam.domain.Helpers
import com.mostafa.majiddelbandam.domain.MajidQuotes
import com.mostafa.majiddelbandam.domain.PersianLetters
import com.mostafa.majiddelbandam.domain.Puzzle
import com.mostafa.majiddelbandam.domain.Victory
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GameUiState(
    val puzzle: Puzzle? = null,
    val committed: List<String> = emptyList(),
    val draft: String = "",
    val selectedIndex: Int = 0,
    val quote: String = "",
    val hintIndex: Int? = null,
    val hintLetter: Char? = null,
    val dimmedKeys: Set<Char> = emptySet(),
    val message: String? = null,
    val recommendation: String? = null,
    val victory: Victory? = null,
    val elapsedMs: Long = 0L,
    val helpers: Helpers = Helpers(0, 0, 0),
    val ashrafi: Int = 0,
    val usedHelp: Boolean = false
)

class GameViewModel(
    private val repository: GameRepository,
    private val puzzleId: Int
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private var timer: Job? = null
    private var startedAt = 0L

    init {
        val puzzle = if (puzzleId == DAILY_ID) repository.dailyPuzzle() else repository.puzzle(puzzleId)
        if (puzzle != null) {
            _state.value = GameUiState(
                puzzle = puzzle,
                committed = listOf(puzzle.startWord),
                draft = "",
                selectedIndex = 0,
                quote = MajidQuotes.playing(puzzle.id)
            )
        }
        viewModelScope.launch {
            repository.progress.collect { progress ->
                _state.update {
                    it.copy(
                        helpers = repository.helpersOf(progress),
                        ashrafi = progress.ashrafi
                    )
                }
            }
        }
    }

    fun selectIndex(index: Int) {
        _state.update { it.copy(selectedIndex = index.coerceIn(0, (it.draft.length - 1).coerceAtLeast(0))) }
    }

    fun type(letter: Char) {
        val current = _state.value
        if (current.victory != null || current.committed.isEmpty()) return
        if (letter in current.dimmedKeys) return
        val limit = current.puzzle?.startWord?.length ?: return
        if (current.draft.length >= limit) return
        ensureTimer()
        _state.update {
            it.copy(
                draft = it.draft + letter,
                message = null,
                hintIndex = null,
                hintLetter = null
            )
        }
    }

    fun backspace() {
        val current = _state.value
        if (current.victory != null) return
        if (current.draft.isEmpty()) return
        _state.update { it.copy(draft = it.draft.dropLast(1), message = null) }
    }

    fun undoStep() {
        val current = _state.value
        if (current.committed.size <= 1 || current.victory != null) return
        val next = current.committed.dropLast(1)
        _state.update {
            it.copy(
                committed = next,
                draft = "",
                dimmedKeys = emptySet(),
                hintIndex = null,
                hintLetter = null,
                message = null
            )
        }
    }

    fun clearMessage() {
        _state.update { it.copy(message = null) }
    }

    fun submit() {
        val current = _state.value
        if (current.victory != null) return
        val last = current.committed.lastOrNull() ?: return
        val draft = current.draft.trim()
        when {
            draft.isEmpty() -> _state.update { it.copy(message = "empty") }
            draft == last -> _state.update { it.copy(message = "same") }
            draft in current.committed -> _state.update { it.copy(message = "used") }
            draft.length != last.length -> _state.update { it.copy(message = "length") }
            draft !in repository.dictionary -> _state.update { it.copy(message = "dict") }
            PersianLetters.hamming(last, draft) != 1 -> _state.update { it.copy(message = "one") }
            else -> acceptWord(draft)
        }
    }

    private fun acceptWord(word: String) {
        val current = _state.value
        val puzzle = current.puzzle ?: return
        val last = current.committed.lastOrNull() ?: return
        if (word == last || PersianLetters.hamming(last, word) != 1) return
        if (word !in repository.dictionary) return
        val committed = current.committed + word
        if (word == puzzle.endWord) {
            timer?.cancel()
            viewModelScope.launch {
                val victory = repository.completeLevel(
                    puzzle = puzzle,
                    stepsTaken = committed.size - 1,
                    elapsedMs = _state.value.elapsedMs,
                    usedHelp = current.usedHelp,
                    wordCount = committed.size
                )
                _state.update { it.copy(committed = committed, draft = word, victory = victory, message = null) }
            }
        } else {
            _state.update {
                it.copy(
                    committed = committed,
                    draft = "",
                    dimmedKeys = emptySet(),
                    hintIndex = null,
                    hintLetter = null,
                    recommendation = null,
                    quote = MajidQuotes.playing(puzzle.id + committed.size),
                    message = null
                )
            }
        }
    }

    fun useScroll() {
        val current = _state.value
        val puzzle = current.puzzle ?: return
        viewModelScope.launch {
            val used = if (current.helpers.scrolls > 0) {
                repository.consume(HelperType.SCROLL)
            } else {
                repository.spendOnHelper(SCROLL_PRICE, HelperType.SCROLL) &&
                    repository.consume(HelperType.SCROLL)
            }
            if (!used) {
                _state.update { it.copy(message = "coins") }
                return@launch
            }
            val next = repository.nextOnPath(puzzle, current.committed.last()) ?: return@launch
            val last = current.committed.last()
            val index = last.indices.firstOrNull { last[it] != next[it] } ?: 0
            _state.update {
                it.copy(
                    hintIndex = index,
                    hintLetter = next[index],
                    selectedIndex = index,
                    usedHelp = true,
                    message = null
                )
            }
        }
    }

    fun useCandle() {
        val current = _state.value
        val puzzle = current.puzzle ?: return
        viewModelScope.launch {
            val used = if (current.helpers.candles > 0) {
                repository.consume(HelperType.CANDLE)
            } else {
                repository.spendOnHelper(CANDLE_PRICE, HelperType.CANDLE) &&
                    repository.consume(HelperType.CANDLE)
            }
            if (!used) {
                _state.update { it.copy(message = "coins") }
                return@launch
            }
            val next = repository.nextOnPath(puzzle, current.committed.last())
            _state.update {
                it.copy(
                    recommendation = next,
                    usedHelp = true,
                    message = if (next == null) "dict" else null
                )
            }
        }
    }

    fun useFal() {
        val current = _state.value
        val puzzle = current.puzzle ?: return
        viewModelScope.launch {
            val used = if (current.helpers.fals > 0) {
                repository.consume(HelperType.FAL)
            } else {
                repository.spendOnHelper(FAL_PRICE, HelperType.FAL) &&
                    repository.consume(HelperType.FAL)
            }
            if (!used) {
                _state.update { it.copy(message = "coins") }
                return@launch
            }
            val next = repository.nextOnPath(puzzle, current.committed.last()) ?: return@launch
            _state.update { it.copy(draft = next, usedHelp = true, message = null) }
            submit()
        }
    }

    fun nearbyWords(): List<String> {
        val last = _state.value.committed.lastOrNull() ?: return emptyList()
        return PersianLetters.neighbors(last, repository.dictionary)
    }

    private fun ensureTimer() {
        if (timer != null) return
        startedAt = System.currentTimeMillis()
        timer = viewModelScope.launch {
            while (isActive) {
                _state.update { it.copy(elapsedMs = System.currentTimeMillis() - startedAt) }
                delay(250)
            }
        }
    }

    companion object {
        const val DAILY_ID = 0
        const val SCROLL_PRICE = 50
        const val CANDLE_PRICE = 20
        const val FAL_PRICE = 40
    }
}

class GameViewModelFactory(
    private val repository: GameRepository,
    private val puzzleId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GameViewModel(repository, puzzleId) as T
    }
}
