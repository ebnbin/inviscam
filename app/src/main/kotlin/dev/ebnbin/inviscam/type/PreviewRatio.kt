package dev.ebnbin.inviscam.type

import android.content.Context
import androidx.annotation.StringRes
import dev.ebnbin.android.core.Entry
import dev.ebnbin.inviscam.R

enum class PreviewRatio(
    val id: String,
    @StringRes val titleId: Int,
) : Entry<String> {
    MATCH_SCREEN(
        id = "match_screen",
        titleId = R.string.preview_ratio_match_screen,
    ),
    RATIO_4_3(
        id = "ratio_4_3",
        titleId = R.string.preview_ratio_4_3,
    ),
    RATIO_16_9(
        id = "ratio_16_9",
        titleId = R.string.preview_ratio_16_9,
    ),
    RATIO_1_1(
        id = "ratio_1_1",
        titleId = R.string.preview_ratio_1_1,
    ),
    ;

    override val entryValue: String
        get() = id

    override fun entryTitle(context: Context): CharSequence {
        return context.getString(titleId)
    }

    companion object {
        fun of(id: String): PreviewRatio {
            return entries.single { it.id == id }
        }
    }
}
