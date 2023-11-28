package dev.ebnbin.android.core.preference

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.preference.TwoStatePreference

abstract class TwoStateUIPref<V, SV : Any, P : TwoStatePreference>(
    name: String,
    encrypted: Boolean = false,
    key: String,
    defaultValue: () -> V,
    valueToStoredValue: (V) -> SV,
    storedValueToValue: (SV) -> V,
    storedValueToPreferenceValue: (SV) -> Boolean,
    preferenceValueToStoredValue: (Boolean) -> SV,
    onPreferenceCreated: Helper<V, SV, P, Boolean>.() -> Unit,
) : UIPref<V, SV, P, Boolean>(
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
    override fun setPreferenceValue(preference: P, preferenceValue: Boolean) {
        preference.isChecked = preferenceValue
    }

    companion object {
        fun <V, SV : Any, P : TwoStatePreference> Helper<V, SV, P, Boolean>.icon(
            @DrawableRes iconOffId: Int,
            @DrawableRes iconOnId: Int,
        ) {
            uiPref.mapPreferenceValue().observe(fragment.viewLifecycleOwner) { isChecked ->
                icon(if (isChecked) iconOnId else iconOffId)
            }
        }

        fun <V, SV : Any, P : TwoStatePreference> Helper<V, SV, P, Boolean>.summary(
            summaryOff: CharSequence?,
            summaryOn: CharSequence?,
        ) {
            uiPref.mapPreferenceValue().observe(fragment.viewLifecycleOwner) { isChecked ->
                summary(if (isChecked) summaryOn else summaryOff)
            }
        }

        fun <V, SV : Any, P : TwoStatePreference> Helper<V, SV, P, Boolean>.summary(
            @StringRes summaryOffId: Int,
            @StringRes summaryOnId: Int,
        ) {
            summary(
                summaryOff = if (summaryOffId == 0) null else fragment.getString(summaryOffId),
                summaryOn = if (summaryOnId == 0) null else fragment.getString(summaryOnId),
            )
        }
    }
}
