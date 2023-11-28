package dev.ebnbin.inviscam.type

import android.content.Context
import androidx.annotation.StringRes
import dev.ebnbin.android.core.Entry
import dev.ebnbin.inviscam.R

enum class Gesture(
    val id: String,
    @StringRes val titleId: Int,
) : Entry<String> {
    SINGLE_TAP(
        id = "single_tap",
        titleId = R.string.gesture_single_tap,
    ),
    DOUBLE_TAP(
        id = "double_tap",
        titleId = R.string.gesture_double_tap,
    ),
    LONG_PRESS(
        id = "long_press",
        titleId = R.string.gesture_long_press,
    ),
    LONG_PRESS_UP(
        id = "long_press_up",
        titleId = R.string.gesture_long_press_up,
    ),
    DOUBLE_LONG_PRESS(
        id = "double_long_press",
        titleId = R.string.gesture_double_long_press,
    ),
    DOUBLE_LONG_PRESS_UP(
        id = "double_long_press_up",
        titleId = R.string.gesture_double_long_press_up,
    ),
    ;

    override val entryValue: String
        get() = id

    override fun entryTitle(context: Context): CharSequence {
        return context.getString(titleId)
    }
}
