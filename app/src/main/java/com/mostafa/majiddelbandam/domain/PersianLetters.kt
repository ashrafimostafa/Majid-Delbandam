package com.mostafa.majiddelbandam.domain

object PersianLetters {
    const val ALPHABET = "ابپتثجچحخدذرزژسشصضطظعغفقکگلمنوهیآ"

    val rows: List<String> = listOf(
        "ضصثقفغعهخحجچ",
        "شسیبلاتنمکگ",
        "ظطزژرذدپوآ"
    )

    fun hamming(a: String, b: String): Int {
        if (a.length != b.length) return Int.MAX_VALUE
        var n = 0
        for (i in a.indices) if (a[i] != b[i]) n++
        return n
    }

    fun changedIndex(from: String, to: String): Int? {
        if (from.length != to.length) return null
        return from.indices.firstOrNull { from[it] != to[it] }
    }

    fun neighbors(word: String, dictionary: Set<String>): List<String> {
        val found = ArrayList<String>(16)
        for (i in word.indices) {
            for (ch in ALPHABET) {
                if (ch == word[i]) continue
                val next = word.substring(0, i) + ch + word.substring(i + 1)
                if (next in dictionary) found += next
            }
        }
        return found
    }

    fun toPersianDigits(value: Int): String = toPersianDigits(value.toString())

    fun toPersianGrouped(value: Int): String {
        val grouped = "%,d".format(value)
        return toPersianDigits(grouped)
    }

    fun toPersianDigits(raw: String): String = buildString(raw.length) {
        for (ch in raw) {
            append(
                when (ch) {
                    '0' -> '۰'
                    '1' -> '۱'
                    '2' -> '۲'
                    '3' -> '۳'
                    '4' -> '۴'
                    '5' -> '۵'
                    '6' -> '۶'
                    '7' -> '۷'
                    '8' -> '۸'
                    '9' -> '۹'
                    else -> ch
                }
            )
        }
    }

    fun formatDuration(ms: Long): String {
        val totalSec = (ms / 1000).coerceAtLeast(0)
        val m = totalSec / 60
        val s = totalSec % 60
        return "${toPersianDigits(m.toInt())}:${toPersianDigits(s.toString().padStart(2, '0'))}"
    }

    fun formatSeconds(ms: Long): String {
        val totalSec = (ms / 1000).coerceAtLeast(0)
        return if (totalSec < 60) {
            "${toPersianDigits(totalSec.toInt())} ثانیه"
        } else {
            formatDuration(ms)
        }
    }

    fun localEpochDay(): Int {
        val cal = java.util.Calendar.getInstance()
        return cal.get(java.util.Calendar.YEAR) * 400 + cal.get(java.util.Calendar.DAY_OF_YEAR)
    }
}
