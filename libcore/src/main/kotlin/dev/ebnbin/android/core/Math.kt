package dev.ebnbin.android.core

import kotlin.math.roundToInt

fun Float.fromPercentage(min: Int = 0, max: Int = 100): Int {
    return (this * 100).roundToInt().coerceIn(min, max)
}

fun Int.toPercentage(min: Float = 0f, max: Float = 1f): Float {
    return (this / 100f).coerceIn(min, max)
}
