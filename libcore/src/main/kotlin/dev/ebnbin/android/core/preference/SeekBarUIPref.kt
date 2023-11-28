package dev.ebnbin.android.core.preference

import android.content.Context
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.preference.PreferenceViewHolder
import androidx.preference.SeekBarPreference
import dev.ebnbin.android.core.AlertDialogFragment
import dev.ebnbin.android.core.AlertDialogFragment.Companion.setAlertDialogResultListener

open class SeekBarUIPref<V, SV : Any>(
    name: String,
    encrypted: Boolean = false,
    key: String,
    defaultValue: () -> V,
    valueToStoredValue: (V) -> SV,
    storedValueToValue: (SV) -> V,
    storedValueToPreferenceValue: (SV) -> Int,
    preferenceValueToStoredValue: (Int) -> SV,
    onPreferenceCreated: Helper<V, SV, SeekBarPreference, Int>.() -> Unit,
) : UIPref<V, SV, SeekBarPreference, Int>(
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
    override fun createPreference(context: Context): SeekBarPreference {
        return object : SeekBarPreference(context) {
            override fun onBindViewHolder(holder: PreferenceViewHolder) {
                super.onBindViewHolder(holder)
                (holder.findViewById(android.R.id.summary) as TextView?)?.maxLines = 16
            }
        }
    }

    override fun setPreferenceValue(preference: SeekBarPreference, preferenceValue: Int) {
        preference.value = preferenceValue
    }

    override fun onPreferenceCreated(helper: Helper<V, SV, SeekBarPreference, Int>) {
        super.onPreferenceCreated(helper)
        helper.updatesContinuously()
        helper.editTextNumberIntDialog()
    }

    companion object {
        fun <V, SV : Any> Helper<V, SV, SeekBarPreference, Int>.minMax(
            min: Int,
            max: Int,
        ) {
            preference.min = min
            preference.max = max
            uiPref.applyPreferenceValue { preferenceValue ->
                preferenceValue.coerceIn(min, max)
            }
        }

        fun <V, SV : Any, T> Helper<V, SV, SeekBarPreference, Int>.minMax(
            liveData: LiveData<T>,
            getMinMax: (T) -> Pair<Int, Int>,
        ) {
            liveData.observe(fragment.viewLifecycleOwner) { value ->
                val (min, max) = getMinMax(value)
                minMax(min, max)
            }
        }

        fun <V, SV : Any> Helper<V, SV, SeekBarPreference, Int>.updatesContinuously() {
            preference.updatesContinuously = true
        }

        fun <V, SV : Any> Helper<V, SV, SeekBarPreference, Int>.editTextNumberIntDialog() {
            fragment.childFragmentManager.setAlertDialogResultListener(
                requestKey = uiPref.key,
                lifecycleOwner = fragment.viewLifecycleOwner,
            ) { result ->
                if (result.type != AlertDialogFragment.ResultType.POSITIVE) {
                    return@setAlertDialogResultListener
                }
                result.editTextText.toIntOrNull()?.takeIf { it in preference.min..preference.max }?.let { value ->
                    uiPref.applyPreferenceValue(value)
                }
            }
            preference.setOnPreferenceClickListener {
                AlertDialogFragment.Builder(
                    context = fragment.requireContext(),
                    requestKey = uiPref.key,
                )
                    .title(preference.title)
                    .editText(
                        type = AlertDialogFragment.EditTextType.NUMBER_INT,
                        text = "${uiPref.getPreferenceValue()}",
                        helper = "${preference.min} ~ ${preference.max}",
                    )
                    .positiveText()
                    .negativeText()
                    .show(fragment.childFragmentManager)
                true
            }
        }
    }
}
