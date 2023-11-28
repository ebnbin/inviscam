package dev.ebnbin.inviscam.type

import android.content.Context
import androidx.annotation.StringRes
import dev.ebnbin.android.core.Entry
import dev.ebnbin.inviscam.R

enum class FabIdleTimeout(
    val value: Long,
    @StringRes val titleId: Int,
) : Entry<Long> {
    IMMEDIATELY(
        value = 0L,
        titleId = R.string.fab_idle_timeout_immediately,
    ),
    SECOND_1(
        value = 1000L,
        titleId = R.string.fab_idle_timeout_second_1,
    ),
    SECOND_2(
        value = 2000L,
        titleId = R.string.fab_idle_timeout_second_2,
    ),
    SECOND_3(
        value = 3000L,
        titleId = R.string.fab_idle_timeout_second_3,
    ),
    SECOND_5(
        value = 5000L,
        titleId = R.string.fab_idle_timeout_second_5,
    ),
    SECOND_10(
        value = 10000L,
        titleId = R.string.fab_idle_timeout_second_10,
    ),
    SECOND_20(
        value = 20000L,
        titleId = R.string.fab_idle_timeout_second_20,
    ),
    SECOND_30(
        value = 30000L,
        titleId = R.string.fab_idle_timeout_second_30,
    ),
    MINUTE_1(
        value = 60000L,
        titleId = R.string.fab_idle_timeout_minute_1,
    ),
    MINUTE_2(
        value = 120000L,
        titleId = R.string.fab_idle_timeout_minute_2,
    ),
    MINUTE_3(
        value = 180000L,
        titleId = R.string.fab_idle_timeout_minute_3,
    ),
    NEVER(
        value = -1L,
        titleId = R.string.fab_idle_timeout_never,
    ),
    ;

    override val entryValue: Long
        get() = value

    override fun entryTitle(context: Context): CharSequence {
        return context.getString(titleId)
    }

    companion object {
        fun of(value: Long): FabIdleTimeout {
            return entries.single { it.value == value }
        }
    }
}
