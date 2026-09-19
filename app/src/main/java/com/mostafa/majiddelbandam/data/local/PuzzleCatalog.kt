package com.mostafa.majiddelbandam.data.local

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.mostafa.majiddelbandam.domain.Difficulty
import com.mostafa.majiddelbandam.domain.Puzzle
import org.json.JSONArray
import java.io.FileOutputStream

class PuzzleCatalog(context: Context) {
    private val appContext = context.applicationContext
    private val db: SQLiteDatabase
    val dictionary: Set<String>

    init {
        val file = appContext.getDatabasePath(DB_NAME)
        if (!file.exists() || file.length() == 0L) {
            file.parentFile?.mkdirs()
            appContext.assets.open(DB_NAME).use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
        }
        db = SQLiteDatabase.openDatabase(
            file.path,
            null,
            SQLiteDatabase.OPEN_READONLY or SQLiteDatabase.NO_LOCALIZED_COLLATORS
        )
        dictionary = db.rawQuery("SELECT word FROM words", null).use { cursor ->
            buildSet {
                while (cursor.moveToNext()) add(cursor.getString(0))
            }
        }
    }

    fun puzzle(id: Int): Puzzle? {
        return db.rawQuery(
            "SELECT id, start_word, end_word, steps, path, difficulty, is_daily FROM puzzles WHERE id = ?",
            arrayOf(id.toString())
        ).use { cursor ->
            if (!cursor.moveToFirst()) null else cursor.toPuzzle()
        }
    }

    fun dailyPuzzleForDay(epochDay: Int): Puzzle? {
        val dailies = db.rawQuery(
            "SELECT id, start_word, end_word, steps, path, difficulty, is_daily FROM puzzles WHERE is_daily = 1 ORDER BY id",
            null
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(cursor.toPuzzle())
            }
        }
        if (dailies.isEmpty()) return null
        return dailies[Math.floorMod(epochDay, dailies.size)]
    }

    private fun android.database.Cursor.toPuzzle(): Puzzle {
        val pathJson = getString(4)
        val path = JSONArray(pathJson).let { arr ->
            List(arr.length()) { arr.getString(it) }
        }
        return Puzzle(
            id = getInt(0),
            startWord = getString(1),
            endWord = getString(2),
            steps = getInt(3),
            path = path,
            difficulty = Difficulty.fromRaw(getString(5)),
            isDaily = getInt(6) == 1
        )
    }

    companion object {
        private const val DB_NAME = "word_ladders.sqlite"
    }
}
