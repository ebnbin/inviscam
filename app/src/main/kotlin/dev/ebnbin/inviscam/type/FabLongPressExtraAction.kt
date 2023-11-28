package dev.ebnbin.inviscam.type

import android.content.Context
import androidx.annotation.StringRes
import dev.ebnbin.android.core.Entry
import dev.ebnbin.inviscam.R
import dev.ebnbin.inviscam.service.InvisCamServiceCallback
import dev.ebnbin.inviscam.util.AnalyticsHelper

enum class FabLongPressExtraAction(
    val id: String,
    @StringRes val titleId: Int,
    private val internalDownAct: (callback: InvisCamServiceCallback) -> Unit,
    private val internalUpAct: (callback: InvisCamServiceCallback) -> Unit,
) : Entry<String> {
    NONE(
        id = "none",
        titleId = R.string.fab_long_press_extra_action_none,
        internalDownAct = {},
        internalUpAct = {},
    ),
    HIDE_FAB(
        id = "hide_fab",
        titleId = R.string.fab_long_press_extra_action_hide_fab,
        internalDownAct = { callback ->
            callback.fab.hide(true)
        },
        internalUpAct = { callback ->
            callback.fab.hide(false)
        },
    ),
    KEEP_AWAKE(
        id = "keep_awake",
        titleId = R.string.fab_long_press_extra_action_keep_awake,
        internalDownAct = { callback ->
            callback.camera.keepAwake(true)
        },
        internalUpAct = { callback ->
            callback.camera.keepAwake(false)
        },
    ),
    HIDE_FAB_AND_KEEP_AWAKE(
        id = "hide_fab_and_keep_awake",
        titleId = R.string.fab_long_press_extra_action_hide_fab_and_keep_awake,
        internalDownAct = { callback ->
            callback.fab.hide(true)
            callback.camera.keepAwake(true)
        },
        internalUpAct = { callback ->
            callback.fab.hide(false)
            callback.camera.keepAwake(false)
        },
    ),
    ;

    fun downAct(callback: InvisCamServiceCallback) {
        internalDownAct(callback)
        AnalyticsHelper.fabLongPressExtraActionDown(
            profile = callback.profile,
            fabLongPressExtraAction = this,
        )
    }

    fun upAct(callback: InvisCamServiceCallback) {
        internalUpAct(callback)
        AnalyticsHelper.fabLongPressExtraActionUp(
            profile = callback.profile,
            fabLongPressExtraAction = this,
        )
    }

    override val entryValue: String
        get() = id

    override fun entryTitle(context: Context): CharSequence {
        return context.getString(titleId)
    }

    companion object {
        fun of(id: String): FabLongPressExtraAction {
            return entries.single { it.id == id }
        }
    }
}
