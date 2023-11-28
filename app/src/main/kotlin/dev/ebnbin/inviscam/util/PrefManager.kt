package dev.ebnbin.inviscam.util

import dev.ebnbin.android.core.Pref
import dev.ebnbin.inviscam.type.Profile

object PrefManager {
    private const val NAME = "app"

    val appVersion: Pref<Int, Int> = Pref(
        name = NAME,
        key = "app_version",
        defaultValue = { 0 },
        valueToStoredValue = { it },
        storedValueToValue = { it },
    )

    val firstTimeHint: Pref<Boolean, Boolean> = Pref(
        name = NAME,
        key = "first_time_hint",
        defaultValue = { true },
        valueToStoredValue = { it },
        storedValueToValue = { it },
    )

    val profileSettingsPage: Pref<Int, Int> = Pref(
        name = NAME,
        key = "profile_settings_page",
        defaultValue = { 1 },
        valueToStoredValue = { it },
        storedValueToValue = { it },
    )

    val profile: Pref<Profile, String> = Pref(
        name = NAME,
        key = "profile",
        defaultValue = { Profile.entries.first() },
        valueToStoredValue = { it.id },
        storedValueToValue = { Profile.of(it) },
    )
}
