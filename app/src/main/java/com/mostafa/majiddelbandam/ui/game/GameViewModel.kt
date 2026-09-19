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
                draft = puzzle.startWord,
                selectedIndex = firstDiff(puzzle.startWord, puzzle.endWord),
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
        ensureTimer()
        val last = current.committed.last()
        val i = current.selectedIndex.coerceIn(0, last.lastIndex)
        val draft = last.replaceRange(i, i + 1, letter.toString())
        if (draft == last) {
            _state.update { it.copy(draft = last, message = null) }
            return
        }
        if (draft in repository.dictionary) {
            acceptWord(draft)
        } else {
            _state.update {
                it.copy(
                    draft = draft,
                    selectedIndex = i,
                    message = "dict",
                    hintIndex = null,
                    hintLetter = null
                )
            }
        }
    }

    fun backspace() {
        val current = _state.value
        if (current.victory != null || current.puzzle == null) return
        val last = current.committed.last()
        val draft = current.draft
        if (draft.isEmpty() || last.length != draft.length) {
            _state.update { it.copy(draft = last, message = null) }
            return
        }
        val i = current.selectedIndex.coerceIn(0, draft.lastIndex)
        if (draft[i] != last[i]) {
            _state.update {
                it.copy(draft = draft.replaceRange(i, i + 1, last[i].toString()), message = null)
            }
        } else {
            val prev = (i - 1).coerceAtLeast(0)
            _state.update {
                it.copy(
                    draft = draft.replaceRange(prev, prev + 1, last[prev].toString()),
                    selectedIndex = prev,
                    message = null
                )
            }
        }
    }

    fun undoStep() {
        val current = _state.value
        if (current.committed.size <= 1 || current.victory != null) return
        val next = current.committed.dropLast(1)
        val restored = next.last()
        val end = current.puzzle?.endWord ?: restored
        _state.update {
            it.copy(
                committed = next,
                draft = restored,
                selectedIndex = firstDiff(restored, end),
                dimmedKeys = emptySet(),
                hintIndex = null,
                hintLetter = null,
                message = null
            )
        }
    }

    fun submit() {
        val current = _state.value
        if (current.victory != null) return
        val last = current.committed.lastOrNull() ?: return
        val draft = current.draft
        when {
            draft == last -> _state.update { it.copy(message = "same") }
            PersianLetters.hamming(last, draft) != 1 -> _state.update { it.copy(message = "one") }
            draft !in repository.dictionary -> _state.update { it.copy(message = "dict") }
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
                    draft = word,
                    selectedIndex = firstDiff(word, puzzle.endWord),
                    dimmedKeys = emptySet(),
                    hintIndex = null,
                    hintLetter = null,
                    quote = MajidQuotes.playing(puzzle.id + committed.size),
                    message = "next"
                )
            }
        }
    }

    private fun firstDiff(from: String, to: String): Int =
        from.indices.firstOrNull { it < to.length && from[it] != to[it] } ?: 0

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
            val last = current.committed.last()
            val useful = PersianLetters.neighbors(last, repository.dictionary)
                .flatMap { word -> word.filterIndexed { i, c -> last[i] != c }.toList() }
                .toSet()
            val dimmed = PersianLetters.ALPHABET.filterNot { it in useful }.toSet()
            _state.update { it.copy(dimmedKeys = dimmed, usedHelp = true, message = null) }
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
            _state.update { it.copy(draft = next, selectedIndex = 0, usedHelp = true, message = null) }
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
