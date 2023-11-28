package dev.ebnbin.inviscam.type

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import dev.ebnbin.android.core.preference.UIPref
import dev.ebnbin.inviscam.R

enum class ProfileSettingsPreference(
    @StringRes val titleId: Int,
    @DrawableRes val iconId: Int,
) {
    FAB(
        titleId = R.string.profile_settings_preference_fab,
        iconId = R.drawable.profile_settings_preference_fab,
    ),
    CAMERA(
        titleId = R.string.profile_settings_preference_camera,
        iconId = R.drawable.profile_settings_preference_camera,
    ),
    PREVIEW(
        titleId = R.string.profile_settings_preference_preview,
        iconId = R.drawable.profile_settings_preference_preview,
    ),
    ;

    fun uiPrefList(profile: Profile): List<UIPref<*, *, *, *>> {
        return when (this) {
            FAB -> profile.pref.fabUIPrefList()
            CAMERA -> profile.pref.cameraUIPrefList()
            PREVIEW -> profile.pref.previewUIPrefList()
        }
    }
}
