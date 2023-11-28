package dev.ebnbin.inviscam.type

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.camera.core.CameraSelector
import dev.ebnbin.android.core.combine
import dev.ebnbin.android.core.fromPercentage
import dev.ebnbin.android.core.get
import dev.ebnbin.android.core.map
import dev.ebnbin.android.core.preference.CategoryUIPref
import dev.ebnbin.android.core.preference.CheckBoxUIPref
import dev.ebnbin.android.core.preference.PreferenceUIPref
import dev.ebnbin.android.core.preference.PreferenceUIPref.Companion.singleChoiceDialog
import dev.ebnbin.android.core.preference.SeekBarUIPref
import dev.ebnbin.android.core.preference.SeekBarUIPref.Companion.minMax
import dev.ebnbin.android.core.preference.SwitchUIPref
import dev.ebnbin.android.core.preference.TwoStateUIPref.Companion.icon
import dev.ebnbin.android.core.preference.TwoStateUIPref.Companion.summary
import dev.ebnbin.android.core.preference.UIPref
import dev.ebnbin.android.core.preference.UIPref.Companion.enabled
import dev.ebnbin.android.core.preference.UIPref.Companion.icon
import dev.ebnbin.android.core.preference.UIPref.Companion.summary
import dev.ebnbin.android.core.preference.UIPref.Companion.title
import dev.ebnbin.android.core.preference.UIPref.Companion.visible
import dev.ebnbin.android.core.toPercentage
import dev.ebnbin.inviscam.R
import dev.ebnbin.inviscam.util.CameraHelper

enum class Profile(
    val id: String,
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int,
    @StringRes val summaryId: Int,
    val defaultValues: DefaultValues,
) {
    PICTURE_IN_PICTURE(
        id = "picture_in_picture",
        titleId = R.string.profile_picture_in_picture,
        iconId = R.drawable.profile_picture_in_picture,
        summaryId = R.string.profile_picture_in_picture_summary,
        defaultValues = DefaultValues(
            fabSingleOrDoubleTapAction = GestureAction.NONE enabled true,
            fabLongPressAction = GestureAction.TOGGLE_RECORDING_VIDEO enabled true,
            fabLongPressUpAction = GestureAction.NONE enabled true,
            fabLongPressExtraAction = FabLongPressExtraAction.NONE enabled false,
            fabDoubleLongPressAction = GestureAction.NONE enabled true,
            fabDoubleLongPressUpAction = GestureAction.NONE enabled true,
            fabDoubleLongPressExtraAction = FabLongPressExtraAction.NONE enabled false,
            lensFacing = CameraSelector.LENS_FACING_BACK enabled true,
            zoom = 0f enabled true,
            previewMode = PreviewMode.PREVIEW_AND_CAPTURE enabled false,
            captureMode = CaptureMode.VIDEO enabled true,
            sleepModeTimeout = SleepModeTimeout.NEVER enabled false,
            previewRatio = PreviewRatio.RATIO_4_3 enabled true,
            previewEnableOut = false enabled false,
            previewSize = 0.5f enabled true,
            previewX = 0.5f enabled true,
            previewY = 0.5f enabled true,
            previewAlpha = 1f enabled true,
            previewEnableTouch = true enabled false,
            previewSingleTapAction = GestureAction.NONE enabled true,
            previewDoubleTapAction = GestureAction.NONE enabled true,
            previewLongPressAction = GestureAction.TOGGLE_RECORDING_VIDEO enabled true,
            previewLongPressUpAction = GestureAction.NONE enabled true,
            previewDoubleLongPressAction = GestureAction.NONE enabled true,
            previewDoubleLongPressUpAction = GestureAction.NONE enabled true,
            previewEnableMove = true enabled true,
            previewScaleAction = PreviewScaleAction.NONE enabled true,
        ),
    ),
    WALLPAPER(
        id = "wallpaper",
        titleId = R.string.profile_wallpaper,
        iconId = R.drawable.profile_wallpaper,
        summaryId = R.string.profile_wallpaper_summary,
        defaultValues = DefaultValues(
            fabSingleOrDoubleTapAction = GestureAction.TOGGLE_SLEEP_MODE enabled false,
            fabLongPressAction = GestureAction.NONE enabled false,
            fabLongPressUpAction = GestureAction.NONE enabled false,
            fabLongPressExtraAction = FabLongPressExtraAction.NONE enabled false,
            fabDoubleLongPressAction = GestureAction.NONE enabled false,
            fabDoubleLongPressUpAction = GestureAction.NONE enabled false,
            fabDoubleLongPressExtraAction = FabLongPressExtraAction.NONE enabled false,
            lensFacing = CameraSelector.LENS_FACING_BACK enabled false,
            zoom = -1f enabled true,
            previewMode = PreviewMode.PREVIEW_ONLY enabled false,
            captureMode = CaptureMode.PHOTO enabled false,
            sleepModeTimeout = SleepModeTimeout.NEVER enabled false,
            previewRatio = PreviewRatio.MATCH_SCREEN enabled false,
            previewEnableOut = true enabled false,
            previewSize = 1f enabled false,
            previewX = 0.5f enabled false,
            previewY = 0.5f enabled false,
            previewAlpha = 0.2f enabled true,
            previewEnableTouch = false enabled false,
            previewSingleTapAction = GestureAction.NONE enabled false,
            previewDoubleTapAction = GestureAction.NONE enabled false,
            previewLongPressAction = GestureAction.NONE enabled false,
            previewLongPressUpAction = GestureAction.NONE enabled false,
            previewDoubleLongPressAction = GestureAction.NONE enabled false,
            previewDoubleLongPressUpAction = GestureAction.NONE enabled false,
            previewEnableMove = false enabled false,
            previewScaleAction = PreviewScaleAction.NONE enabled false,
        ),
    ),
    MIRROR(
        id = "mirror",
        titleId = R.string.profile_mirror,
        iconId = R.drawable.profile_mirror,
        summaryId = R.string.profile_mirror_summary,
        defaultValues = DefaultValues(
            fabSingleOrDoubleTapAction = GestureAction.TOGGLE_SLEEP_MODE enabled false,
            fabLongPressAction = GestureAction.NONE enabled false,
            fabLongPressUpAction = GestureAction.NONE enabled false,
            fabLongPressExtraAction = FabLongPressExtraAction.HIDE_FAB_AND_KEEP_AWAKE enabled false,
            fabDoubleLongPressAction = GestureAction.NONE enabled false,
            fabDoubleLongPressUpAction = GestureAction.NONE enabled false,
            fabDoubleLongPressExtraAction = FabLongPressExtraAction.HIDE_FAB_AND_KEEP_AWAKE enabled false,
            lensFacing = CameraSelector.LENS_FACING_FRONT enabled false,
            zoom = 0f enabled false,
            previewMode = PreviewMode.PREVIEW_ONLY enabled false,
            captureMode = CaptureMode.PHOTO enabled false,
            sleepModeTimeout = SleepModeTimeout.IMMEDIATELY enabled true,
            previewRatio = PreviewRatio.MATCH_SCREEN enabled false,
            previewEnableOut = true enabled false,
            previewSize = 1f enabled false,
            previewX = 0.5f enabled false,
            previewY = 0.5f enabled false,
            previewAlpha = 1f enabled false,
            previewEnableTouch = true enabled false,
            previewSingleTapAction = GestureAction.NONE enabled false,
            previewDoubleTapAction = GestureAction.NONE enabled false,
            previewLongPressAction = GestureAction.NONE enabled false,
            previewLongPressUpAction = GestureAction.NONE enabled false,
            previewDoubleLongPressAction = GestureAction.NONE enabled false,
            previewDoubleLongPressUpAction = GestureAction.NONE enabled false,
            previewEnableMove = false enabled false,
            previewScaleAction = PreviewScaleAction.NONE enabled false,
        ),
    ),
    MAGNIFIER(
        id = "magnifier",
        titleId = R.string.profile_magnifier,
        iconId = R.drawable.profile_magnifier,
        summaryId = R.string.profile_magnifier_summary,
        defaultValues = DefaultValues(
            fabSingleOrDoubleTapAction = GestureAction.TOGGLE_SLEEP_MODE enabled false,
            fabLongPressAction = GestureAction.NONE enabled false,
            fabLongPressUpAction = GestureAction.NONE enabled false,
            fabLongPressExtraAction = FabLongPressExtraAction.NONE enabled false,
            fabDoubleLongPressAction = GestureAction.NONE enabled false,
            fabDoubleLongPressUpAction = GestureAction.NONE enabled false,
            fabDoubleLongPressExtraAction = FabLongPressExtraAction.NONE enabled false,
            lensFacing = CameraSelector.LENS_FACING_BACK enabled false,
            zoom = 1f enabled true,
            previewMode = PreviewMode.PREVIEW_ONLY enabled false,
            captureMode = CaptureMode.PHOTO enabled false,
            sleepModeTimeout = SleepModeTimeout.NEVER enabled false,
            previewRatio = PreviewRatio.MATCH_SCREEN enabled false,
            previewEnableOut = true enabled false,
            previewSize = 1f enabled false,
            previewX = 0.5f enabled false,
            previewY = 0.5f enabled false,
            previewAlpha = 1f enabled false,
            previewEnableTouch = true enabled false,
            previewSingleTapAction = GestureAction.NONE enabled false,
            previewDoubleTapAction = GestureAction.NONE enabled false,
            previewLongPressAction = GestureAction.NONE enabled false,
            previewLongPressUpAction = GestureAction.NONE enabled false,
            previewDoubleLongPressAction = GestureAction.NONE enabled false,
            previewDoubleLongPressUpAction = GestureAction.NONE enabled false,
            previewEnableMove = false enabled false,
            previewScaleAction = PreviewScaleAction.CAMERA_ZOOM enabled false,
        ),
    ),
    CANDID(
        id = "candid",
        titleId = R.string.profile_candid,
        iconId = R.drawable.profile_candid,
        summaryId = R.string.profile_candid_summary,
        defaultValues = DefaultValues(
            fabSingleOrDoubleTapAction = GestureAction.NONE enabled true,
            fabLongPressAction = GestureAction.NONE enabled true,
            fabLongPressUpAction = GestureAction.TAKE_PICTURE enabled true,
            fabLongPressExtraAction = FabLongPressExtraAction.HIDE_FAB_AND_KEEP_AWAKE enabled false,
            fabDoubleLongPressAction = GestureAction.NONE enabled true,
            fabDoubleLongPressUpAction = GestureAction.NONE enabled true,
            fabDoubleLongPressExtraAction = FabLongPressExtraAction.HIDE_FAB_AND_KEEP_AWAKE enabled false,
            lensFacing = CameraSelector.LENS_FACING_BACK enabled true,
            zoom = 0f enabled true,
            previewMode = PreviewMode.PREVIEW_AND_CAPTURE enabled false,
            captureMode = CaptureMode.PHOTO enabled true,
            sleepModeTimeout = SleepModeTimeout.IMMEDIATELY enabled false,
            previewRatio = PreviewRatio.MATCH_SCREEN enabled true,
            previewEnableOut = true enabled true,
            previewSize = 1f enabled true,
            previewX = 0.5f enabled true,
            previewY = 0.5f enabled true,
            previewAlpha = 1f enabled true,
            previewEnableTouch = true enabled true,
            previewSingleTapAction = GestureAction.NONE enabled true,
            previewDoubleTapAction = GestureAction.NONE enabled true,
            previewLongPressAction = GestureAction.NONE enabled true,
            previewLongPressUpAction = GestureAction.NONE enabled true,
            previewDoubleLongPressAction = GestureAction.NONE enabled true,
            previewDoubleLongPressUpAction = GestureAction.NONE enabled true,
            previewEnableMove = true enabled true,
            previewScaleAction = PreviewScaleAction.NONE enabled true,
        ),
    ),
    CUSTOM(
        id = "custom",
        titleId = R.string.profile_custom,
        iconId = R.drawable.profile_custom,
        summaryId = R.string.profile_custom_summary,
        defaultValues = DefaultValues(
            fabSingleOrDoubleTapAction = GestureAction.NONE enabled true,
            fabLongPressAction = GestureAction.NONE enabled true,
            fabLongPressUpAction = GestureAction.NONE enabled true,
            fabLongPressExtraAction = FabLongPressExtraAction.NONE enabled true,
            fabDoubleLongPressAction = GestureAction.NONE enabled true,
            fabDoubleLongPressUpAction = GestureAction.NONE enabled true,
            fabDoubleLongPressExtraAction = FabLongPressExtraAction.NONE enabled true,
            lensFacing = CameraSelector.LENS_FACING_BACK enabled true,
            zoom = 0f enabled true,
            previewMode = PreviewMode.PREVIEW_AND_CAPTURE enabled true,
            captureMode = CaptureMode.PHOTO enabled true,
            sleepModeTimeout = SleepModeTimeout.NEVER enabled true,
            previewRatio = PreviewRatio.RATIO_4_3 enabled true,
            previewEnableOut = false enabled true,
            previewSize = 0.5f enabled true,
            previewX = 0.5f enabled true,
            previewY = 0.5f enabled true,
            previewAlpha = 1f enabled true,
            previewEnableTouch = true enabled true,
            previewSingleTapAction = GestureAction.NONE enabled true,
            previewDoubleTapAction = GestureAction.NONE enabled true,
            previewLongPressAction = GestureAction.NONE enabled true,
            previewLongPressUpAction = GestureAction.NONE enabled true,
            previewDoubleLongPressAction = GestureAction.NONE enabled true,
            previewDoubleLongPressUpAction = GestureAction.NONE enabled true,
            previewEnableMove = true enabled true,
            previewScaleAction = PreviewScaleAction.NONE enabled true,
        ),
    ),
    ;

    data class DefaultValues(
        val fabSingleOrDoubleTapAction: Item<GestureAction>,
        val fabLongPressAction: Item<GestureAction>,
        val fabLongPressUpAction: Item<GestureAction>,
        val fabLongPressExtraAction: Item<FabLongPressExtraAction>,
        val fabDoubleLongPressAction: Item<GestureAction>,
        val fabDoubleLongPressUpAction: Item<GestureAction>,
        val fabDoubleLongPressExtraAction: Item<FabLongPressExtraAction>,
        val lensFacing: Item<Int>,
        val zoom: Item<Float>,
        val previewMode: Item<PreviewMode>,
        val captureMode: Item<CaptureMode>,
        val sleepModeTimeout: Item<SleepModeTimeout>,
        val previewRatio: Item<PreviewRatio>,
        val previewEnableOut: Item<Boolean>,
        val previewSize: Item<Float>,
        val previewX: Item<Float>,
        val previewY: Item<Float>,
        val previewAlpha: Item<Float>,
        val previewEnableTouch: Item<Boolean>,
        val previewSingleTapAction: Item<GestureAction>,
        val previewDoubleTapAction: Item<GestureAction>,
        val previewLongPressAction: Item<GestureAction>,
        val previewLongPressUpAction: Item<GestureAction>,
        val previewDoubleLongPressAction: Item<GestureAction>,
        val previewDoubleLongPressUpAction: Item<GestureAction>,
        val previewEnableMove: Item<Boolean>,
        val previewScaleAction: Item<PreviewScaleAction>,
    ) {
        data class Item<T>(
            val value: T,
            val enabled: Boolean,
        )
    }

    val pref: Pref = Pref(
        sharedPreferencesName = "profile_$id",
        defaultValues = defaultValues,
        profileSummaryId = summaryId,
    )

    class Pref(
        private val sharedPreferencesName: String,
        defaultValues: DefaultValues,
        @StringRes profileSummaryId: Int,
    ) {
        private val fabProfileSettingsCategory: CategoryUIPref = CategoryUIPref(
            name = sharedPreferencesName,
            key = "fab_profile_settings_category",
        ) {
            title(R.string.pref_fab_profile_settings_category)
        }

        val fabSingleOrDoubleTapAction: PreferenceUIPref<GestureAction, String> = PreferenceUIPref(
            name = sharedPreferencesName,
            key = "fab_single_or_double_tap_action",
            defaultValue = { defaultValues.fabSingleOrDoubleTapAction.value },
            valueToStoredValue = { it.id },
            storedValueToValue = { GestureAction.of(it) },
        ) {
            title(
                liveData = fabOpenMenuGesture,
                getTitle = {
                    if (it == Gesture.DOUBLE_TAP) {
                        fragment.getString(R.string.pref_fab_single_tap_action)
                    } else {
                        fragment.getString(R.string.pref_fab_double_tap_action)
                    }
                },
            )
            icon(
                liveData = fabOpenMenuGesture,
                getIconId = {
                    if (it == Gesture.DOUBLE_TAP) {
                        R.drawable.pref_fab_single_tap_action
                    } else {
                        R.drawable.pref_fab_double_tap_action
                    }
                },
            )
            singleChoiceDialog(GestureAction.entries, R.string.pref_fab_single_or_double_tap_action_summary)
            enabled(defaultValues.fabSingleOrDoubleTapAction.enabled)
        }

        val fabLongPressAction: PreferenceUIPref<GestureAction, String> = fabGestureAction(
            key = "fab_long_press_action",
            defaultValue = { defaultValues.fabLongPressAction.value },
            titleId = R.string.pref_fab_long_press_action,
            iconId = R.drawable.pref_fab_long_press_action,
            summaryId = R.string.pref_fab_long_press_action_summary,
            enabled = defaultValues.fabLongPressAction.enabled,
        )

        val fabLongPressUpAction: PreferenceUIPref<GestureAction, String> = fabGestureAction(
            key = "fab_long_press_up_action",
            defaultValue = { defaultValues.fabLongPressUpAction.value },
            titleId = R.string.pref_fab_long_press_up_action,
            iconId = R.drawable.pref_fab_long_press_up_action,
            summaryId = R.string.pref_fab_long_press_up_action_summary,
            enabled = defaultValues.fabLongPressUpAction.enabled,
        )

        val fabLongPressExtraAction: PreferenceUIPref<FabLongPressExtraAction, String> = PreferenceUIPref(
            name = sharedPreferencesName,
            key = "fab_long_press_extra_action",
            defaultValue = { defaultValues.fabLongPressExtraAction.value },
            valueToStoredValue = { it.id },
            storedValueToValue = { FabLongPressExtraAction.of(it) },
        ) {
            title(R.string.pref_fab_long_press_extra_action)
            icon(R.drawable.pref_fab_long_press_extra_action)
            singleChoiceDialog(FabLongPressExtraAction.entries, R.string.pref_fab_long_press_extra_action_summary)
            enabled(defaultValues.fabLongPressExtraAction.enabled)
        }

        val fabDoubleLongPressAction: PreferenceUIPref<GestureAction, String> = fabGestureAction(
            key = "fab_double_long_press_action",
            defaultValue = { defaultValues.fabDoubleLongPressAction.value },
            titleId = R.string.pref_fab_double_long_press_action,
            iconId = R.drawable.pref_fab_double_long_press_action,
            summaryId = R.string.pref_fab_double_long_press_action_summary,
            enabled = defaultValues.fabDoubleLongPressAction.enabled,
        )

        val fabDoubleLongPressUpAction: PreferenceUIPref<GestureAction, String> = fabGestureAction(
            key = "fab_double_long_press_up_action",
            defaultValue = { defaultValues.fabDoubleLongPressUpAction.value },
            titleId = R.string.pref_fab_double_long_press_up_action,
            iconId = R.drawable.pref_fab_double_long_press_up_action,
            summaryId = R.string.pref_fab_double_long_press_up_action_summary,
            enabled = defaultValues.fabDoubleLongPressUpAction.enabled,
        )

        val fabDoubleLongPressExtraAction: PreferenceUIPref<FabLongPressExtraAction, String> = PreferenceUIPref(
            name = sharedPreferencesName,
            key = "fab_double_long_press_extra_action",
            defaultValue = { defaultValues.fabDoubleLongPressExtraAction.value },
            valueToStoredValue = { it.id },
            storedValueToValue = { FabLongPressExtraAction.of(it) },
        ) {
            title(R.string.pref_fab_double_long_press_extra_action)
            icon(R.drawable.pref_fab_double_long_press_extra_action)
            singleChoiceDialog(FabLongPressExtraAction.entries,
                R.string.pref_fab_double_long_press_extra_action_summary)
            enabled(defaultValues.fabDoubleLongPressExtraAction.enabled)
        }

        private val help: PreferenceUIPref<Unit, Unit> = PreferenceUIPref(
            name = sharedPreferencesName,
            key = "help",
            defaultValue = {},
            valueToStoredValue = {},
            storedValueToValue = {},
        ) {
            icon(R.drawable.pref_help)
            summary(profileSummaryId)
        }

        private val cameraCategory: CategoryUIPref = CategoryUIPref(
            name = sharedPreferencesName,
            key = "camera_category",
        ) {
            title(R.string.pref_camera_category)
        }

        val lensFacing: SwitchUIPref<Int, Boolean> = SwitchUIPref(
            name = sharedPreferencesName,
            key = "is_lens_facing_front",
            defaultValue = { defaultValues.lensFacing.value },
            valueToStoredValue = { it == CameraSelector.LENS_FACING_FRONT },
            storedValueToValue = { if (it) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK },
            storedValueToPreferenceValue = { it },
            preferenceValueToStoredValue = { it },
        ) {
            title(R.string.pref_lens_facing)
            icon(R.drawable.pref_lens_facing_off, R.drawable.pref_lens_facing_on)
            summary(R.string.pref_lens_facing_summary_off, R.string.pref_lens_facing_summary_on)
            enabled(defaultValues.lensFacing.enabled)
        }

        val zoom: SeekBarUIPref<Float, Int> = SeekBarUIPref(
            name = sharedPreferencesName,
            key = "zoom",
            defaultValue = { defaultValues.zoom.value },
            valueToStoredValue = { it.fromPercentage(-100, 100) },
            storedValueToValue = { it.toPercentage(-1f, 1f) },
            storedValueToPreferenceValue = { it },
            preferenceValueToStoredValue = { it },
        ) {
            title(R.string.pref_zoom)
            icon(R.drawable.pref_zoom)
            summary { fragment.getString(R.string.pref_zoom_summary, uiPref.getPreferenceValue()) }
            minMax(-100, 100)
            enabled(defaultValues.zoom.enabled)
        }

        private val previewCaptureCategory: CategoryUIPref = CategoryUIPref(
            name = sharedPreferencesName,
            key = "preview_capture_category",
        ) {
            title(R.string.pref_preview_capture_category)
        }

        val previewMode: PreferenceUIPref<PreviewMode, String> = PreferenceUIPref(
            name = sharedPreferencesName,
            key = "preview_mode",
            defaultValue = { defaultValues.previewMode.value },
            valueToStoredValue = { it.id },
            storedValueToValue = { PreviewMode.of(it) },
        ) {
            title(R.string.pref_preview_mode)
            icon(R.drawable.pref_preview_mode)
            singleChoiceDialog(PreviewMode.entries, R.string.pref_preview_mode_summary)
            enabled(defaultValues.previewMode.enabled)
        }

        val captureMode: PreferenceUIPref<CaptureMode, String> = PreferenceUIPref(
            name = sharedPreferencesName,
            key = "capture_mode",
            defaultValue = { defaultValues.captureMode.value },
            valueToStoredValue = { it.id },
            storedValueToValue = { CaptureMode.of(it) },
        ) {
            title(R.string.pref_capture_mode)
            summary { fragment.getString(R.string.pref_capture_mode_summary, CameraHelper.DIR,
                fragment.getString(it.titleId)) }
            icon(R.drawable.pref_capture_mode)
            singleChoiceDialog(CaptureMode.entries)
            visible(previewMode.map { it != PreviewMode.PREVIEW_ONLY })
            enabled(defaultValues.captureMode.enabled)
        }

        private val sleepModeCategory: CategoryUIPref = CategoryUIPref(
            name = sharedPreferencesName,
            key = "sleep_mode_category",
        ) {
            title(R.string.pref_sleep_mode_category)
        }

        val sleepModeTimeout: PreferenceUIPref<SleepModeTimeout, Long> = PreferenceUIPref(
            name = sharedPreferencesName,
            key = "sleep_mode_timeout",
            defaultValue = { defaultValues.sleepModeTimeout.value },
            valueToStoredValue = { it.value },
            storedValueToValue = { SleepModeTimeout.of(it) },
        ) {
            title(R.string.pref_sleep_mode_timeout)
            icon(R.drawable.pref_sleep_mode_timeout)
            singleChoiceDialog(SleepModeTimeout.entries, R.string.pref_sleep_mode_timeout_summary)
            enabled(defaultValues.sleepModeTimeout.enabled)
        }

        private val previewDisplayCategory: CategoryUIPref = CategoryUIPref(
            name = sharedPreferencesName,
            key = "preview_display_category",
        ) {
            title(R.string.pref_preview_display_category)
            visible(previewMode.map { it != PreviewMode.CAPTURE_ONLY })
        }

        val previewRatio: PreferenceUIPref<PreviewRatio, String> = PreferenceUIPref(
            name = sharedPreferencesName,
            key = "preview_ratio",
            defaultValue = { defaultValues.previewRatio.value },
            valueToStoredValue = { it.id },
            storedValueToValue = { PreviewRatio.of(it) },
        ) {
            title(R.string.pref_preview_ratio)
            icon(R.drawable.pref_preview_ratio)
            singleChoiceDialog(PreviewRatio.entries, R.string.pref_preview_ratio_summary)
            visible(previewMode.map { it != PreviewMode.CAPTURE_ONLY })
            enabled(defaultValues.previewRatio.enabled)
        }

        val previewEnableOut: CheckBoxUIPref<Boolean, Boolean> = CheckBoxUIPref(
            name = sharedPreferencesName,
            key = "preview_enable_out",
            defaultValue = { defaultValues.previewEnableOut.value },
            valueToStoredValue = { it },
            storedValueToValue = { it },
            storedValueToPreferenceValue = { it },
            preferenceValueToStoredValue = { it },
        ) {
            title(R.string.pref_preview_enable_out)
            icon(R.drawable.pref_preview_enable_out)
            summary(R.string.pref_preview_enable_out_summary_off, R.string.pref_preview_enable_out_summary_on)
            visible(previewMode.map { it != PreviewMode.CAPTURE_ONLY })
            enabled(defaultValues.previewEnableOut.enabled)
        }

        val previewSize: SeekBarUIPref<Float, Int> = SeekBarUIPref(
            name = sharedPreferencesName,
            key = "preview_size",
            defaultValue = { defaultValues.previewSize.value },
            valueToStoredValue = { it.fromPercentage(10, 100) },
            storedValueToValue = { it.toPercentage(0.1f, 1f) },
            storedValueToPreferenceValue = { it },
            preferenceValueToStoredValue = { it },
        ) {
            title(R.string.pref_preview_size)
            icon(R.drawable.pref_preview_size)
            summary { fragment.getString(R.string.pref_preview_size_summary, uiPref.getPreferenceValue()) }
            minMax(10, 100)
            visible(previewMode.map { it != PreviewMode.CAPTURE_ONLY })
            enabled(defaultValues.previewSize.enabled)
        }

        val previewX: SeekBarUIPref<Float, Int> = SeekBarUIPref(
            name = sharedPreferencesName,
            key = "preview_x",
            defaultValue = { defaultValues.previewX.value },
            valueToStoredValue = {
                if (previewEnableOut.get()) it.fromPercentage(-99, 199) else it.fromPercentage(0, 100)
            },
            storedValueToValue = {
                if (previewEnableOut.get()) it.toPercentage(-0.99f, 1.99f) else it.toPercentage(0f, 1f)
            },
            storedValueToPreferenceValue = { it },
            preferenceValueToStoredValue = { it },
        ) {
            title(R.string.pref_preview_x)
            icon(R.drawable.pref_preview_x)
            summary { fragment.getString(R.string.pref_preview_x_summary, uiPref.getPreferenceValue()) }
            minMax(previewEnableOut) { if (it) -99 to 199 else 0 to 100 }
            visible(previewMode.map { it != PreviewMode.CAPTURE_ONLY })
            enabled(defaultValues.previewX.enabled)
        }

        val previewY: SeekBarUIPref<Float, Int> = SeekBarUIPref(
            name = sharedPreferencesName,
            key = "preview_y",
            defaultValue = { defaultValues.previewY.value },
            valueToStoredValue = {
                if (previewEnableOut.get()) it.fromPercentage(-99, 199) else it.fromPercentage(0, 100)
            },
            storedValueToValue = {
                if (previewEnableOut.get()) it.toPercentage(-0.99f, 1.99f) else it.toPercentage(0f, 1f)
            },
            storedValueToPreferenceValue = { it },
            preferenceValueToStoredValue = { it },
        ) {
            title(R.string.pref_preview_y)
            icon(R.drawable.pref_preview_y)
            summary { fragment.getString(R.string.pref_preview_y_summary, uiPref.getPreferenceValue()) }
            minMax(previewEnableOut) { if (it) -99 to 199 else 0 to 100 }
            visible(previewMode.map { it != PreviewMode.CAPTURE_ONLY })
            enabled(defaultValues.previewY.enabled)
        }

        private val previewControlCategory: CategoryUIPref = CategoryUIPref(
            name = sharedPreferencesName,
            key = "preview_control_category",
        ) {
            title(R.string.pref_preview_control_category)
            visible(previewMode.map { it != PreviewMode.CAPTURE_ONLY })
        }

        val previewEnableTouch: CheckBoxUIPref<Boolean, Boolean> = CheckBoxUIPref(
            name = sharedPreferencesName,
            key = "preview_enable_touch",
            defaultValue = { defaultValues.previewEnableTouch.value },
            valueToStoredValue = { it },
            storedValueToValue = { it },
            storedValueToPreferenceValue = { it },
            preferenceValueToStoredValue = { it },
        ) {
            title(R.string.pref_preview_enable_touch)
            icon(R.drawable.pref_preview_enable_touch)
            summary(R.string.pref_preview_enable_touch_summary_off, R.string.pref_preview_enable_touch_summary_on)
            visible(previewMode.map { it != PreviewMode.CAPTURE_ONLY })
            enabled(defaultValues.previewEnableTouch.enabled)
        }

        val previewAlpha: SeekBarUIPref<Float, Int> = SeekBarUIPref(
            name = sharedPreferencesName,
            key = "preview_alpha",
            defaultValue = { defaultValues.previewAlpha.value },
            valueToStoredValue = {
                if (previewEnableTouch.get()) it.fromPercentage(15, 100) else it.fromPercentage(5, 80)
            },
            storedValueToValue = {
                if (previewEnableTouch.get()) it.toPercentage(0.15f, 1f) else it.toPercentage(0.05f, 0.8f)
            },
            storedValueToPreferenceValue = { it },
            preferenceValueToStoredValue = { it },
        ) {
            title(R.string.pref_preview_alpha)
            icon(R.drawable.pref_preview_alpha)
            summary { fragment.getString(R.string.pref_preview_alpha_summary, uiPref.getPreferenceValue()) }
            minMax(previewEnableTouch) { if (it) 15 to 100 else 5 to 80 }
            visible(previewMode.map { it != PreviewMode.CAPTURE_ONLY })
            enabled(defaultValues.previewAlpha.enabled)
        }

        val previewSingleTapAction: PreferenceUIPref<GestureAction, String> = previewGestureAction(
            key = "preview_single_tap_action",
            defaultValue = { defaultValues.previewSingleTapAction.value },
            titleId = R.string.pref_preview_single_tap_action,
            iconId = R.drawable.pref_preview_single_tap_action,
            summaryId = R.string.pref_preview_single_tap_action_summary,
            enabled = defaultValues.previewSingleTapAction.enabled,
        )

        val previewDoubleTapAction: PreferenceUIPref<GestureAction, String> = previewGestureAction(
            key = "preview_double_tap_action",
            defaultValue = { defaultValues.previewDoubleTapAction.value },
            titleId = R.string.pref_preview_double_tap_action,
            iconId = R.drawable.pref_preview_double_tap_action,
            summaryId = R.string.pref_preview_double_tap_action_summary,
            enabled = defaultValues.previewDoubleTapAction.enabled,
        )

        val previewLongPressAction: PreferenceUIPref<GestureAction, String> = previewGestureAction(
            key = "preview_long_press_action",
            defaultValue = { defaultValues.previewLongPressAction.value },
            titleId = R.string.pref_preview_long_press_action,
            iconId = R.drawable.pref_preview_long_press_action,
            summaryId = R.string.pref_preview_long_press_action_summary,
            enabled = defaultValues.previewLongPressAction.enabled,
        )

        val previewLongPressUpAction: PreferenceUIPref<GestureAction, String> = previewGestureAction(
            key = "preview_long_press_up_action",
            defaultValue = { defaultValues.previewLongPressUpAction.value },
            titleId = R.string.pref_preview_long_press_up_action,
            iconId = R.drawable.pref_preview_long_press_up_action,
            summaryId = R.string.pref_preview_long_press_up_action_summary,
            enabled = defaultValues.previewLongPressUpAction.enabled,
        )

        val previewDoubleLongPressAction: PreferenceUIPref<GestureAction, String> = previewGestureAction(
            key = "preview_double_long_press_action",
            defaultValue = { defaultValues.previewDoubleLongPressAction.value },
            titleId = R.string.pref_preview_double_long_press_action,
            iconId = R.drawable.pref_preview_double_long_press_action,
            summaryId = R.string.pref_preview_double_long_press_action_summary,
            enabled = defaultValues.previewDoubleLongPressAction.enabled,
        )

        val previewDoubleLongPressUpAction: PreferenceUIPref<GestureAction, String> = previewGestureAction(
            key = "preview_double_long_press_up_action",
            defaultValue = { defaultValues.previewDoubleLongPressUpAction.value },
            titleId = R.string.pref_preview_double_long_press_up_action,
            iconId = R.drawable.pref_preview_double_long_press_up_action,
            summaryId = R.string.pref_preview_double_long_press_up_action_summary,
            enabled = defaultValues.previewDoubleLongPressUpAction.enabled,
        )

        val previewEnableMove: CheckBoxUIPref<Boolean, Boolean> = CheckBoxUIPref(
            name = sharedPreferencesName,
            key = "preview_enable_move",
            defaultValue = { defaultValues.previewEnableMove.value },
            valueToStoredValue = { it },
            storedValueToValue = { it },
            storedValueToPreferenceValue = { it },
            preferenceValueToStoredValue = { it },
        ) {
            title(R.string.pref_preview_enable_move)
            icon(R.drawable.pref_preview_enable_move)
            summary(R.string.pref_preview_enable_move_summary_off, R.string.pref_preview_enable_move_summary_on)
            visible(combine(previewMode, previewEnableTouch).map { (previewMode, previewEnableTouch) ->
                previewMode != PreviewMode.CAPTURE_ONLY && previewEnableTouch
            })
            enabled(defaultValues.previewEnableMove.enabled)
        }

        val previewScaleAction: PreferenceUIPref<PreviewScaleAction, String> = PreferenceUIPref(
            name = sharedPreferencesName,
            key = "preview_scale_action",
            defaultValue = { defaultValues.previewScaleAction.value },
            valueToStoredValue = { it.id },
            storedValueToValue = { PreviewScaleAction.of(it) },
        ) {
            title(R.string.pref_preview_scale_action)
            icon(R.drawable.pref_preview_scale_action)
            singleChoiceDialog(PreviewScaleAction.entries, R.string.pref_preview_scale_action_summary)
            visible(combine(previewMode, previewEnableTouch).map { (previewMode, previewEnableTouch) ->
                previewMode != PreviewMode.CAPTURE_ONLY && previewEnableTouch
            })
            enabled(defaultValues.previewScaleAction.enabled)
        }

        private fun fabGestureAction(
            key: String,
            defaultValue: () -> GestureAction,
            titleId: Int,
            iconId: Int,
            summaryId: Int,
            enabled: Boolean,
        ): PreferenceUIPref<GestureAction, String> {
            return PreferenceUIPref(
                name = sharedPreferencesName,
                key = key,
                defaultValue = defaultValue,
                valueToStoredValue = { it.id },
                storedValueToValue = { GestureAction.of(it) },
            ) {
                title(titleId)
                icon(iconId)
                singleChoiceDialog(GestureAction.entries, summaryId)
                enabled(enabled)
            }
        }

        private fun previewGestureAction(
            key: String,
            defaultValue: () -> GestureAction,
            titleId: Int,
            summaryId: Int,
            iconId: Int,
            enabled: Boolean,
        ): PreferenceUIPref<GestureAction, String> {
            return PreferenceUIPref(
                name = sharedPreferencesName,
                key = key,
                defaultValue = defaultValue,
                valueToStoredValue = { it.id },
                storedValueToValue = { GestureAction.of(it) },
            ) {
                title(titleId)
                icon(iconId)
                singleChoiceDialog(GestureAction.entries, summaryId)
                visible(combine(previewMode, previewEnableTouch).map { (previewMode, previewEnableTouch) ->
                    previewMode != PreviewMode.CAPTURE_ONLY && previewEnableTouch
                })
                enabled(enabled)
            }
        }

        fun fabUIPrefList(): List<UIPref<*, *, *, *>> {
            return listOf(
                fabHelp,
                fabGlobalSettingsCategory,
                fabX,
                fabY,
                fabIdleTimeout,
                fabIdleAlpha,
                fabOpenMenuGesture,
                fabProfileSettingsCategory,
                fabSingleOrDoubleTapAction,
                fabLongPressAction,
                fabLongPressUpAction,
                fabLongPressExtraAction,
                fabDoubleLongPressAction,
                fabDoubleLongPressUpAction,
                fabDoubleLongPressExtraAction,
            )
        }

        fun cameraUIPrefList(): List<UIPref<*, *, *, *>> {
            return listOf(
                help,
                cameraCategory,
                lensFacing,
                zoom,
                previewCaptureCategory,
                previewMode,
                captureMode,
                sleepModeCategory,
                sleepModeTimeout,
            )
        }

        fun previewUIPrefList(): List<UIPref<*, *, *, *>> {
            return listOf(
                previewMode,
                previewDisplayCategory,
                previewRatio,
                previewEnableOut,
                previewSize,
                previewX,
                previewY,
                previewAlpha,
                previewControlCategory,
                previewEnableTouch,
                previewSingleTapAction,
                previewDoubleTapAction,
                previewLongPressAction,
                previewLongPressUpAction,
                previewDoubleLongPressAction,
                previewDoubleLongPressUpAction,
                previewEnableMove,
                previewScaleAction,
            )
        }

        companion object {
            private const val FAB_SHARED_PREFERENCES_NAME = "fab"

            private val fabHelp: PreferenceUIPref<Unit, Unit> = PreferenceUIPref(
                name = FAB_SHARED_PREFERENCES_NAME,
                key = "fab_help",
                defaultValue = {},
                valueToStoredValue = {},
                storedValueToValue = {},
            ) {
                icon(R.drawable.pref_fab_help)
                summary(R.string.pref_fab_help_summary)
            }

            private val fabGlobalSettingsCategory: CategoryUIPref = CategoryUIPref(
                name = FAB_SHARED_PREFERENCES_NAME,
                key = "fab_global_settings_category",
            ) {
                title(R.string.pref_fab_global_settings_category)
            }

            val fabX: SeekBarUIPref<Float, Int> = SeekBarUIPref(
                name = FAB_SHARED_PREFERENCES_NAME,
                key = "fab_x",
                defaultValue = { 1f },
                valueToStoredValue = { it.fromPercentage(0, 100) },
                storedValueToValue = { it.toPercentage(0f, 1f) },
                storedValueToPreferenceValue = { it },
                preferenceValueToStoredValue = { it },
            ) {
                title(R.string.pref_fab_x)
                icon(R.drawable.pref_fab_x)
                summary { fragment.getString(R.string.pref_fab_x_summary, uiPref.getPreferenceValue()) }
                minMax(0, 100)
            }

            val fabY: SeekBarUIPref<Float, Int> = SeekBarUIPref(
                name = FAB_SHARED_PREFERENCES_NAME,
                key = "fab_y",
                defaultValue = { 0.6f },
                valueToStoredValue = { it.fromPercentage(0, 100) },
                storedValueToValue = { it.toPercentage(0f, 1f) },
                storedValueToPreferenceValue = { it },
                preferenceValueToStoredValue = { it },
            ) {
                title(R.string.pref_fab_y)
                icon(R.drawable.pref_fab_y)
                summary { fragment.getString(R.string.pref_fab_y_summary, uiPref.getPreferenceValue()) }
                minMax(0, 100)
            }

            val fabIdleTimeout: PreferenceUIPref<FabIdleTimeout, Long> = PreferenceUIPref(
                name = FAB_SHARED_PREFERENCES_NAME,
                key = "fab_idle_timeout",
                defaultValue = { FabIdleTimeout.SECOND_5 },
                valueToStoredValue = { it.value },
                storedValueToValue = { FabIdleTimeout.of(it) },
            ) {
                title(R.string.pref_fab_idle_timeout)
                icon(R.drawable.pref_fab_idle_timeout)
                singleChoiceDialog(FabIdleTimeout.entries, R.string.pref_fab_idle_timeout_summary)
            }

            val fabIdleAlpha: SeekBarUIPref<Float, Int> = SeekBarUIPref(
                name = FAB_SHARED_PREFERENCES_NAME,
                key = "fab_idle_alpha",
                defaultValue = { 0.4f },
                valueToStoredValue = { it.fromPercentage(15, 99) },
                storedValueToValue = { it.toPercentage(0.15f, 0.99f) },
                storedValueToPreferenceValue = { it },
                preferenceValueToStoredValue = { it },
            ) {
                title(R.string.pref_fab_idle_alpha)
                icon(R.drawable.pref_fab_idle_alpha)
                summary { fragment.getString(R.string.pref_fab_idle_alpha_summary, uiPref.getPreferenceValue()) }
                minMax(15, 99)
            }

            val fabOpenMenuGesture: SwitchUIPref<Gesture, Boolean> = SwitchUIPref(
                name = FAB_SHARED_PREFERENCES_NAME,
                key = "is_fab_open_menu_gesture_double_tap",
                defaultValue = { Gesture.SINGLE_TAP },
                valueToStoredValue = { it == Gesture.DOUBLE_TAP },
                storedValueToValue = { if (it) Gesture.DOUBLE_TAP else Gesture.SINGLE_TAP },
                storedValueToPreferenceValue = { it },
                preferenceValueToStoredValue = { it },
            ) {
                title(R.string.pref_fab_open_menu_gesture)
                icon(R.drawable.pref_fab_open_menu_gesture)
                summary(R.string.pref_fab_open_menu_gesture_summary_off,
                    R.string.pref_fab_open_menu_gesture_summary_on)
            }
        }
    }

    companion object {
        fun of(id: String): Profile {
            return entries.single { it.id == id }
        }
    }
}

private infix fun <T> T.enabled(enabled: Boolean): Profile.DefaultValues.Item<T> {
    return Profile.DefaultValues.Item(this, enabled)
}
