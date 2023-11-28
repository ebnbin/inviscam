package dev.ebnbin.android.core

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.window.layout.WindowMetricsCalculator

data class WindowSize(
    val rotation: Int,
    val outWidth: Int,
    val outHeight: Int,
    val inWidth: Int,
    val inHeight: Int,
) {
    companion object {
        fun create(
            context: Context,
            inWidth: Int? = null,
            inHeight: Int? = null,
        ): WindowSize {
            val rect = WindowMetricsCalculator.getOrCreate().computeMaximumWindowMetrics(context).bounds
            val width = rect.width()
            val height = rect.height()
            return WindowSize(
                rotation = ContextCompat.getDisplayOrDefault(context).rotation,
                outWidth = width,
                outHeight = height,
                inWidth = inWidth ?: width,
                inHeight = inHeight ?: height,
            )
        }
    }
}
