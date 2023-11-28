package dev.ebnbin.inviscam.profilesettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import dev.ebnbin.android.core.argumentOrThrow
import dev.ebnbin.android.core.createFragment
import dev.ebnbin.android.core.dpToPxRound
import dev.ebnbin.inviscam.type.Profile
import dev.ebnbin.inviscam.type.ProfileSettingsPreference

class ProfileSettingsPreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceScreen = preferenceManager.createPreferenceScreen(requireContext())
    }

    override fun onCreateRecyclerView(
        inflater: LayoutInflater,
        parent: ViewGroup,
        savedInstanceState: Bundle?,
    ): RecyclerView {
        return super.onCreateRecyclerView(inflater, parent, savedInstanceState).apply {
            updatePadding(bottom = 120f.dpToPxRound)
            clipToPadding = false
            scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val profile = argumentOrThrow<Profile>(KEY_PROFILE)
        val profileSettingsPreference = argumentOrThrow<ProfileSettingsPreference>(KEY_PROFILE_SETTINGS_PREFERENCE)
        profileSettingsPreference.uiPrefList(profile).forEach { uiPref ->
            uiPref.onCreatePreference(this)
        }
    }

    companion object {
        private const val KEY_PROFILE = "profile"
        private const val KEY_PROFILE_SETTINGS_PREFERENCE = "profile_settings_preference"

        fun create(
            fragmentManager: FragmentManager,
            profile: Profile,
            profileSettingsPreference: ProfileSettingsPreference,
        ): ProfileSettingsPreferenceFragment {
            return fragmentManager.createFragment(
                arguments = bundleOf(
                    KEY_PROFILE to profile,
                    KEY_PROFILE_SETTINGS_PREFERENCE to profileSettingsPreference,
                ),
            )
        }
    }
}
