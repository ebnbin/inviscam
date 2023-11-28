package dev.ebnbin.inviscam.util

import androidx.camera.core.CameraSelector
import com.google.firebase.analytics.logEvent
import dev.ebnbin.android.core.firebaseAnalytics
import dev.ebnbin.inviscam.type.CaptureMode
import dev.ebnbin.inviscam.type.FabLongPressExtraAction
import dev.ebnbin.inviscam.type.GestureAction
import dev.ebnbin.inviscam.type.PreviewMode
import dev.ebnbin.inviscam.type.Profile

object AnalyticsHelper {
    enum class StartServiceWhere {
        MAIN,
        PROFILE_SETTINGS,
        FAB_MENU,
        NIGHT_MODE,
        ;

        val id: String
            get() = name.lowercase()
    }

    fun startService(
        profile: Profile,
        where: StartServiceWhere,
    ) {
        firebaseAnalytics.logEvent("start_service") {
            param("profile", profile.id)
            param("where", where.id)
        }
    }

    enum class StopServiceWhere {
        MAIN,
        FAB_MENU,
        ACTION,
        NOTIFICATION,
        ERROR,
        ;

        val id: String
            get() = name.lowercase()
    }

    fun stopService(
        where: StopServiceWhere,
        duration: Long,
    ) {
        firebaseAnalytics.logEvent("stop_service") {
            param("where", where.id)
            param("duration", duration)
        }
    }

    fun profile(
        profile: Profile,
        duration: Long,
    ) {
        firebaseAnalytics.logEvent("profile") {
            param("profile", profile.id)
            param("duration", duration)
        }
    }

    fun camera(
        lensFacing: Int,
        previewMode: PreviewMode,
        captureMode: CaptureMode,
        duration: Long,
    ) {
        firebaseAnalytics.logEvent("camera") {
            param("is_lens_facing_front", "${lensFacing == CameraSelector.LENS_FACING_FRONT}")
            param("preview_mode", previewMode.id)
            param("capture_mode", captureMode.id)
            param("duration", duration)
        }
    }

    fun takePicture(
        lensFacing: Int,
    ) {
        firebaseAnalytics.logEvent("take_picture") {
            param("is_lens_facing_front", "${lensFacing == CameraSelector.LENS_FACING_FRONT}")
        }
    }

    fun recordVideo(
        lensFacing: Int,
        duration: Long,
    ) {
        firebaseAnalytics.logEvent("record_video") {
            param("is_lens_facing_front", "${lensFacing == CameraSelector.LENS_FACING_FRONT}")
            param("duration", duration)
        }
    }

    fun sleepMode(
        duration: Long,
    ) {
        firebaseAnalytics.logEvent("sleep_mode") {
            param("duration", duration)
        }
    }

    fun openFabMenu(
        profile: Profile,
    ) {
        firebaseAnalytics.logEvent("open_fab_menu") {
            param("profile", profile.id)
        }
    }

    fun fabMenuToggleSleepMode(
        profile: Profile,
    ) {
        firebaseAnalytics.logEvent("fab_menu_toggle_sleep_mode") {
            param("profile", profile.id)
        }
    }

    fun gestureAction(
        profile: Profile,
        gestureAction: GestureAction,
    ) {
        firebaseAnalytics.logEvent("gesture_action") {
            param("profile", profile.id)
            param("gesture_action", gestureAction.id)
        }
    }

    fun fabLongPressExtraActionDown(
        profile: Profile,
        fabLongPressExtraAction: FabLongPressExtraAction,
    ) {
        firebaseAnalytics.logEvent("fab_long_press_extra_action_down") {
            param("profile", profile.id)
            param("fab_long_press_extra_action", fabLongPressExtraAction.id)
        }
    }

    fun fabLongPressExtraActionUp(
        profile: Profile,
        fabLongPressExtraAction: FabLongPressExtraAction,
    ) {
        firebaseAnalytics.logEvent("fab_long_press_extra_action_up") {
            param("profile", profile.id)
            param("fab_long_press_extra_action", fabLongPressExtraAction.id)
        }
    }
}
