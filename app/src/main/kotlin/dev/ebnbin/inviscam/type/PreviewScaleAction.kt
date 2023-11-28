package dev.ebnbin.inviscam.type

import android.content.Context
import androidx.annotation.StringRes
import dev.ebnbin.android.core.Entry
import dev.ebnbin.inviscam.R

enum class PreviewScaleAction(
    val id: String,
    @StringRes val titleId: Int,
) : Entry<String> {
    WINDOW_SIZE(
        id = "window_size",
        titleId = R.string.preview_scale_action_window_size,
    ),
    CAMERA_ZOOM(
        id = "camera_zoom",
        titleId = R.string.preview_scale_action_camera_zoom,
    ),
    NONE(
        id = "none",
        titleId = R.string.preview_scale_action_none,
    ),
    ;

    override val entryValue: String
        get() = id

    override fun entryTitle(context: Context): CharSequence {
        return context.getString(titleId)
    }

    companion object {
        fun of(id: String): PreviewScaleAction {
            return entries.single { it.id == id }
        }
    }
}
