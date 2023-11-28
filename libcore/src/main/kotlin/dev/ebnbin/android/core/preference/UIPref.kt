package dev.ebnbin.android.core.preference

import android.content.Context
import android.content.res.ColorStateList
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dev.ebnbin.android.core.Pref
import dev.ebnbin.android.core.androidAttr
import dev.ebnbin.android.core.appcompatAttr
import dev.ebnbin.android.core.getColorAttr
import dev.ebnbin.android.core.map

abstract class UIPref<V, SV : Any, P : Preference, PV>(
    name: String,
    encrypted: Boolean = false,
    key: String,
    defaultValue: () -> V,
    valueToStoredValue: (V) -> SV,
    storedValueToValue: (SV) -> V,
    val storedValueToPreferenceValue: (SV) -> PV,
    val preferenceValueToStoredValue: (PV) -> SV,
    val onPreferenceCreated: Helper<V, SV, P, PV>.() -> Unit,
) : Pref<V, SV>(
    name = name,
    encrypted = encrypted,
    key = key,
    defaultValue = defaultValue,
    valueToStoredValue = valueToStoredValue,
    storedValueToValue = storedValueToValue,
) {
    data class Helper<V, SV : Any, P : Preference, PV>(
        val fragment: PreferenceFragmentCompat,
        val uiPref: UIPref<V, SV, P, PV>,
        val preference: P,
    )

    fun valueToPreferenceValue(value: V): PV {
        return storedValueToPreferenceValue(valueToStoredValue(value))
    }

    fun preferenceValueToValue(preferenceValue: PV): V {
        return storedValueToValue(preferenceValueToStoredValue(preferenceValue))
    }

    //*****************************************************************************************************************

    fun defaultPreferenceValue(): PV {
        return valueToPreferenceValue(defaultValue())
    }

    fun getPreferenceValue(): PV {
        return storedValueToPreferenceValue(getStoredValue())
    }

    fun commitPreferenceValue(preferenceValue: PV): Boolean {
        return putPreferenceValue(preferenceValue, commit = true)
    }

    fun commitPreferenceValue(updater: (PV) -> PV): Boolean {
        val preferenceValue = getPreferenceValue()
        return commitPreferenceValue(updater(preferenceValue))
    }

    fun applyPreferenceValue(preferenceValue: PV) {
        putPreferenceValue(preferenceValue, commit = false)
    }

    fun applyPreferenceValue(updater: (PV) -> PV) {
        val preferenceValue = getPreferenceValue()
        applyPreferenceValue(updater(preferenceValue))
    }

    protected fun putPreferenceValue(preferenceValue: PV, commit: Boolean): Boolean {
        return putStoredValue(preferenceValueToStoredValue(preferenceValue), commit)
    }

    //*****************************************************************************************************************

    fun mapPreferenceValue(): LiveData<PV> {
        return map {
            valueToPreferenceValue(it)
        }
    }

    protected abstract fun createPreference(context: Context): P

    protected abstract fun setPreferenceValue(preference: P, preferenceValue: PV)

    protected open fun onPreferenceCreated(helper: Helper<V, SV, P, PV>) {
        helper.preference.isSingleLineTitle = false
    }

    fun onCreatePreference(fragment: PreferenceFragmentCompat): P {
        val preference = createPreference(fragment.requireContext())
        preference.isPersistent = false
        preference.key = key
        preference.setDefaultValue(defaultPreferenceValue())
        preference.setOnPreferenceChangeListener { _, newValue ->
            @Suppress("UNCHECKED_CAST")
            newValue as PV
            commit(preferenceValueToValue(newValue))
        }
        observe(fragment.viewLifecycleOwner) { value ->
            setPreferenceValue(preference, valueToPreferenceValue(value))
        }
        val helper = Helper(fragment, this, preference)
        onPreferenceCreated(helper)
        onPreferenceCreated.invoke(helper)
        fragment.preferenceScreen.addPreference(preference)
        return preference
    }

    companion object {
        fun <V, SV : Any, P : Preference, PV> Helper<V, SV, P, PV>.title(
            title: CharSequence?,
        ) {
            preference.title = title
        }

        fun <V, SV : Any, P : Preference, PV> Helper<V, SV, P, PV>.title(
            @StringRes titleId: Int,
        ) {
            title(if (titleId == 0) null else fragment.getString(titleId))
        }

        fun <V, SV : Any, P : Preference, PV, T> Helper<V, SV, P, PV>.title(
            liveData: LiveData<T>,
            getTitle: (T) -> CharSequence?,
        ) {
            liveData.observe(fragment.viewLifecycleOwner) { value ->
                title(getTitle(value))
            }
        }

        fun <V, SV : Any, P : Preference, PV> Helper<V, SV, P, PV>.icon(
            @DrawableRes iconId: Int,
        ) {
            if (iconId == 0) {
                preference.icon = null
                return
            }
            preference.setIcon(iconId)
            val states = arrayOf(
                intArrayOf(androidAttr.state_enabled),
                intArrayOf(-androidAttr.state_enabled),
            )
            val colors = intArrayOf(
                fragment.requireContext().getColorAttr(appcompatAttr.colorPrimary),
                fragment.requireContext().getColorAttr(androidAttr.textColorHint),
            )
            preference.icon?.setTintList(ColorStateList(states, colors))
        }

        fun <V, SV : Any, P : Preference, PV, T> Helper<V, SV, P, PV>.icon(
            liveData: LiveData<T>,
            getIconId: (T) -> Int,
        ) {
            liveData.observe(fragment.viewLifecycleOwner) { value ->
                icon(getIconId(value))
            }
        }

        fun <V, SV : Any, P : Preference, PV> Helper<V, SV, P, PV>.summary(
            summary: CharSequence?,
        ) {
            preference.summary = summary
        }

        fun <V, SV : Any, P : Preference, PV> Helper<V, SV, P, PV>.summary(
            @StringRes summaryId: Int,
        ) {
            summary(if (summaryId == 0) null else fragment.getString(summaryId))
        }

        fun <V, SV : Any, P : Preference, PV, T> Helper<V, SV, P, PV>.summary(
            liveData: LiveData<T>,
            getSummary: (T) -> CharSequence?,
        ) {
            liveData.observe(fragment.viewLifecycleOwner) { value ->
                summary(getSummary(value))
            }
        }

        fun <V, SV : Any, P : Preference, PV> Helper<V, SV, P, PV>.summary(
            getSummary: (V) -> CharSequence?,
        ) {
            summary(
                liveData = uiPref,
                getSummary = getSummary,
            )
        }

        fun <V, SV : Any, P : Preference, PV> Helper<V, SV, P, PV>.enabled(
            isEnabled: Boolean,
        ) {
            preference.isEnabled = isEnabled
        }

        fun <V, SV : Any, P : Preference, PV> Helper<V, SV, P, PV>.enabled(
            liveData: LiveData<Boolean>,
        ) {
            liveData.observe(fragment.viewLifecycleOwner) { value ->
                enabled(value)
            }
        }

        fun <V, SV : Any, P : Preference, PV> Helper<V, SV, P, PV>.visible(
            liveData: LiveData<Boolean>,
        ) {
            liveData.observe(fragment.viewLifecycleOwner) { value ->
                preference.isVisible = value
            }
        }
    }
}
