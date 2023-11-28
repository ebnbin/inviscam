package dev.ebnbin.inviscam.type

import android.content.Context
import androidx.annotation.StringRes
import dev.ebnbin.android.core.Entry
import dev.ebnbin.inviscam.R

enum class CaptureMode(
    val id: String,
    @StringRes val titleId: Int,
) : Entry<String> {
    PHOTO(
        id = "photo",
        titleId = R.string.capture_mode_photo,
    ),
    VIDEO(
        id = "video",
        titleId = R.string.capture_mode_video,
    ),
    PHOTO_AND_VIDEO(
        id = "photo_and_video",
        titleId = R.string.capture_mode_photo_and_video,
    ),
    ;

    override val entryValue: String
        get() = id

    override fun entryTitle(context: Context): CharSequence {
        return context.getString(titleId)
    }

    companion object {
        fun of(id: String): CaptureMode {
            return entries.single { it.id == id }
        }
    }
}
