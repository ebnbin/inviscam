package dev.ebnbin.inviscam.type

import android.content.Context
import androidx.annotation.StringRes
import dev.ebnbin.android.core.Entry
import dev.ebnbin.inviscam.R

enum class PreviewMode(
    val id: String,
    @StringRes val titleId: Int,
) : Entry<String> {
    PREVIEW_AND_CAPTURE(
        id = "preview_and_capture",
        titleId = R.string.preview_mode_preview_and_capture,
    ),
    PREVIEW_ONLY(
        id = "preview_only",
        titleId = R.string.preview_mode_preview_only,
    ),
    CAPTURE_ONLY(
        id = "capture_only",
        titleId = R.string.preview_mode_capture_only,
    ),
    ;

    override val entryValue: String
        get() = id

    override fun entryTitle(context: Context): CharSequence {
        return context.getString(titleId)
    }

    companion object {
        fun of(id: String): PreviewMode {
            return entries.single { it.id == id }
        }
    }
}
