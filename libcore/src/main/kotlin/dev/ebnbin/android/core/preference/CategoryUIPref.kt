package dev.ebnbin.android.core.preference

import android.content.Context
import androidx.preference.PreferenceCategory

open class CategoryUIPref(
    name: String,
    encrypted: Boolean = false,
    key: String,
    onPreferenceCreated: Helper<Unit, Unit, PreferenceCategory, Unit>.() -> Unit,
) : UIPref<Unit, Unit, PreferenceCategory, Unit>(
    name = name,
    encrypted = encrypted,
    key = key,
    defaultValue = {},
    valueToStoredValue = {},
    storedValueToValue = {},
    storedValueToPreferenceValue = {},
    preferenceValueToStoredValue = {},
    onPreferenceCreated = onPreferenceCreated,
) {
    override fun createPreference(context: Context): PreferenceCategory {
        return PreferenceCategory(context)
    }

    override fun setPreferenceValue(preference: PreferenceCategory, preferenceValue: Unit) {
    }
}
