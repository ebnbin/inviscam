package dev.ebnbin.inviscam.type

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.ebnbin.android.core.Entry
import dev.ebnbin.android.core.openApp
import dev.ebnbin.inviscam.R
import dev.ebnbin.inviscam.service.InvisCamService
import dev.ebnbin.inviscam.service.InvisCamServiceCallback
import dev.ebnbin.inviscam.util.AnalyticsHelper

enum class GestureAction(
    val id: String,
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int = 0,
    private val internalAct: (callback: InvisCamServiceCallback) -> Unit,
) : Entry<String> {
    TOGGLE_CAPTURE_MODE(
        id = "toggle_capture_mode",
        titleId = R.string.gesture_action_toggle_capture_mode,
        internalAct = { callback ->
            callback.camera.toggleCaptureMode()
        },
    ),
    TAKE_PICTURE(
        id = "take_picture",
        titleId = R.string.gesture_action_take_picture,
        internalAct = { callback ->
            callback.camera.takePicture()
        },
    ),
    START_RECORDING_VIDEO(
        id = "start_recording_video",
        titleId = R.string.gesture_action_start_recording_video,
        internalAct = { callback ->
            callback.camera.startRecordingVideo()
        },
    ),
    STOP_RECORDING_VIDEO(
        id = "stop_recording_video",
        titleId = R.string.gesture_action_stop_recording_video,
        internalAct = { callback ->
            callback.camera.stopRecordingVideo()
        },
    ),
    TOGGLE_RECORDING_VIDEO(
        id = "toggle_recording_video",
        titleId = R.string.gesture_action_toggle_recording_video,
        internalAct = { callback ->
            callback.camera.toggleRecordingVideo()
        },
    ),
    CAPTURE(
        id = "capture",
        titleId = R.string.gesture_action_capture,
        internalAct = { callback ->
            callback.camera.capture()
        },
    ),
    ENTER_SLEEP_MODE(
        id = "enter_sleep_mode",
        titleId = R.string.gesture_action_enter_sleep_mode,
        internalAct = { callback ->
            callback.camera.enterSleepMode()
        },
    ),
    EXIT_SLEEP_MODE(
        id = "exit_sleep_mode",
        titleId = R.string.gesture_action_exit_sleep_mode,
        internalAct = { callback ->
            callback.camera.exitSleepMode()
        },
    ),
    TOGGLE_SLEEP_MODE(
        id = "toggle_sleep_mode",
        titleId = R.string.gesture_action_toggle_sleep_mode,
        iconId = R.drawable.gesture_action_toggle_sleep_mode,
        internalAct = { callback ->
            callback.camera.toggleSleepMode()
        },
    ),
    OPEN_APP(
        id = "open_app",
        titleId = R.string.gesture_action_open_app,
        internalAct = { callback ->
            callback.context.openApp()
        },
    ),
    STOP_SERVICE(
        id = "stop_service",
        titleId = R.string.gesture_action_stop_service,
        iconId = R.drawable.gesture_action_stop_service,
        internalAct = { callback ->
            InvisCamService.stop(
                context = callback.context,
                where = AnalyticsHelper.StopServiceWhere.ACTION,
            )
        },
    ),
    NONE(
        id = "none",
        titleId = R.string.gesture_action_none,
        internalAct = {},
    )
    ;

    fun act(callback: InvisCamServiceCallback) {
        internalAct(callback)
        AnalyticsHelper.gestureAction(
            profile = callback.profile,
            gestureAction = this,
        )
    }

    override val entryValue: String
        get() = id

    override fun entryTitle(context: Context): CharSequence {
        return context.getString(titleId)
    }

    companion object {
        fun of(id: String): GestureAction {
            return entries.single { it.id == id }
        }
    }
}
