package dev.ebnbin.inviscam.profilesettings

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import dev.ebnbin.inviscam.type.Profile
import dev.ebnbin.inviscam.type.ProfileSettingsPreference

class ProfileSettingsAdapter(
    private val fragment: Fragment,
    private val profile: Profile,
) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return ProfileSettingsPreference.entries.size
    }

    override fun createFragment(position: Int): Fragment {
        return ProfileSettingsPreferenceFragment.create(
            fragmentManager = fragment.childFragmentManager,
            profile = profile,
            profileSettingsPreference = ProfileSettingsPreference.entries[position],
        )
    }
}
