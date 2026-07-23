package com.islamic.tasbihcounter.util

/** Converts Western digits in a string to Bangla numerals for display. */
object BanglaNumber {
    private val digits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    fun format(value: Int): String = format(value.toString())

    fun format(text: String): String = buildString {
        for (c in text) {
            if (c in '0'..'9') append(digits[c - '0']) else append(c)
        }
    }
}

/** Convenience extension: `42.bn()` -> "৪২". */
fun Int.bn(): String = BanglaNumber.format(this)
