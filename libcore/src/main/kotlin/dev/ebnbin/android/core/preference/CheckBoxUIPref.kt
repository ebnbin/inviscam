package dev.ebnbin.android.core.preference

import android.content.Context
import android.widget.TextView
import androidx.preference.CheckBoxPreference
import androidx.preference.PreferenceViewHolder

open class CheckBoxUIPref<V, SV : Any>(
    name: String,
    encrypted: Boolean = false,
    key: String,
    defaultValue: () -> V,
    valueToStoredValue: (V) -> SV,
    storedValueToValue: (SV) -> V,
    storedValueToPreferenceValue: (SV) -> Boolean,
    preferenceValueToStoredValue: (Boolean) -> SV,
    onPreferenceCreated: Helper<V, SV, CheckBoxPreference, Boolean>.() -> Unit,
) : TwoStateUIPref<V, SV, CheckBoxPreference>(
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
    override fun createPreference(context: Context): CheckBoxPreference {
        return object : CheckBoxPreference(context) {
            override fun onBindViewHolder(holder: PreferenceViewHolder) {
                super.onBindViewHolder(holder)
                (holder.findViewById(android.R.id.summary) as TextView?)?.maxLines = 16
            }
        }
    }
}
