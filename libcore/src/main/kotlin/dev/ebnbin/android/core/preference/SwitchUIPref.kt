package dev.ebnbin.android.core.preference

import android.content.Context
import android.widget.TextView
import androidx.preference.PreferenceViewHolder
import androidx.preference.SwitchPreferenceCompat

open class SwitchUIPref<V, SV : Any>(
    name: String,
    encrypted: Boolean = false,
    key: String,
    defaultValue: () -> V,
    valueToStoredValue: (V) -> SV,
    storedValueToValue: (SV) -> V,
    storedValueToPreferenceValue: (SV) -> Boolean,
    preferenceValueToStoredValue: (Boolean) -> SV,
    onPreferenceCreated: Helper<V, SV, SwitchPreferenceCompat, Boolean>.() -> Unit,
) : TwoStateUIPref<V, SV, SwitchPreferenceCompat>(
    name = name,
    encrypted = encrypted,
    key = key,
    defaultValue = defaultValue,
    valueToStoredValue = valueToStoredValue,
    storedValueToValue = storedValueToValue,
    storedValueToPreferenceValue = storedValueToPreferenceValue,
    preferenceValueToStoredValue = preferenceValueToStoredValue,
    onPreferenceCreated = onPreferenceCreated,
) {
    override fun createPreference(context: Context): SwitchPreferenceCompat {
        return object : SwitchPreferenceCompat(context) {
            override fun onBindViewHolder(holder: PreferenceViewHolder) {
                super.onBindViewHolder(holder)
                (holder.findViewById(android.R.id.summary) as TextView?)?.maxLines = 16
            }
        }
    }
}
