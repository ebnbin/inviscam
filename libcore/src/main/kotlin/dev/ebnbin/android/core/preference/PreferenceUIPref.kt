package dev.ebnbin.android.core.preference

import android.content.Context
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import dev.ebnbin.android.core.AlertDialogFragment
import dev.ebnbin.android.core.AlertDialogFragment.Companion.setAlertDialogResultListener
import dev.ebnbin.android.core.Entry

open class PreferenceUIPref<V, SV : Any>(
    name: String,
    encrypted: Boolean = false,
    key: String,
    defaultValue: () -> V,
    valueToStoredValue: (V) -> SV,
    storedValueToValue: (SV) -> V,
    onPreferenceCreated: Helper<V, SV, Preference, Unit>.() -> Unit,
) : UIPref<V, SV, Preference, Unit>(
    name = name,
    encrypted = encrypted,
    key = key,
    defaultValue = defaultValue,
    valueToStoredValue = valueToStoredValue,
    storedValueToValue = storedValueToValue,
    storedValueToPreferenceValue = {},
    preferenceValueToStoredValue = { error(Unit) },
    onPreferenceCreated = onPreferenceCreated,
) {
    override fun createPreference(context: Context): Preference {
        return object : Preference(context) {
            override fun onBindViewHolder(holder: PreferenceViewHolder) {
                super.onBindViewHolder(holder)
                (holder.findViewById(android.R.id.summary) as TextView?)?.maxLines = 16
            }
        }
    }

    override fun setPreferenceValue(preference: Preference, preferenceValue: Unit) {
    }

    companion object {
        fun <V, SV : Any> Helper<V, SV, Preference, Unit>.messageDialog(
            title: CharSequence? = null,
            message: CharSequence,
            positiveText: CharSequence? = null,
            negativeText: CharSequence? = null,
            onPositive: () -> Unit,
        ) {
            fragment.childFragmentManager.setAlertDialogResultListener(
                requestKey = uiPref.key,
                lifecycleOwner = fragment.viewLifecycleOwner,
            ) { result ->
                if (result.type != AlertDialogFragment.ResultType.POSITIVE) {
                    return@setAlertDialogResultListener
                }
                onPositive()
            }
            preference.setOnPreferenceClickListener {
                AlertDialogFragment.Builder(
                    context = fragment.requireContext(),
                    requestKey = uiPref.key,
                )
                    .title(title)
                    .message(message)
                    .positiveText(positiveText)
                    .negativeText(negativeText)
                    .show(fragment.childFragmentManager)
                true
            }
        }

        fun <V, SV : Any> Helper<V, SV, Preference, Unit>.singleChoiceDialog(
            entryList: List<Entry<SV>>,
            @StringRes summaryId: Int,
        ) {
            singleChoiceDialog(entryList) { _, entryTitle ->
                fragment.getString(summaryId, entryTitle.toString())
            }
        }

        fun <V, SV : Any> Helper<V, SV, Preference, Unit>.singleChoiceDialog(
            entryList: List<Entry<SV>>,
            getSummary: ((V, CharSequence) -> CharSequence)? = null,
        ) {
            fragment.childFragmentManager.setAlertDialogResultListener(
                requestKey = uiPref.key,
                lifecycleOwner = fragment.viewLifecycleOwner,
            ) { result ->
                if (result.type != AlertDialogFragment.ResultType.SINGLE_CHOICE) {
                    return@setAlertDialogResultListener
                }
                val entry = entryList[result.singleChoiceCheckedItem]
                uiPref.applyStoredValue(entry.entryValue)
            }
            preference.setOnPreferenceClickListener {
                val context = fragment.requireContext()
                AlertDialogFragment.Builder(
                    context = context,
                    requestKey = uiPref.key,
                )
                    .title(preference.title)
                    .singleChoice(
                        items = entryList.map { it.entryTitle(context) }.toTypedArray(),
                        checkedItem = entryList.indexOfFirst { it.entryValue == uiPref.getStoredValue() },
                    )
                    .negativeText()
                    .show(fragment.childFragmentManager)
                true
            }
            if (getSummary != null) {
                summary { value ->
                    val entry = entryList.first { it.entryValue == uiPref.getStoredValue() }
                    val entryTitle = entry.entryTitle(fragment.requireContext())
                    getSummary(value, entryTitle)
                }
            }
        }

        fun <V> Helper<V, String, Preference, Unit>.editTextTextDialog(
            placeholder: CharSequence? = null,
            helper: CharSequence? = null,
            textMaxLength: Int = -1,
        ) {
            fragment.childFragmentManager.setAlertDialogResultListener(
                requestKey = uiPref.key,
                lifecycleOwner = fragment.viewLifecycleOwner,
            ) { result ->
                if (result.type != AlertDialogFragment.ResultType.POSITIVE) {
                    return@setAlertDialogResultListener
                }
                uiPref.applyStoredValue(result.editTextText)
            }
            preference.setOnPreferenceClickListener {
                AlertDialogFragment.Builder(
                    context = fragment.requireContext(),
                    requestKey = uiPref.key,
                )
                    .title(preference.title)
                    .editText(
                        type = AlertDialogFragment.EditTextType.TEXT,
                        text = uiPref.getStoredValue(),
                        placeholder = placeholder,
                        helper = helper,
                        textMaxLength = textMaxLength,
                    )
                    .positiveText()
                    .negativeText()
                    .show(fragment.childFragmentManager)
                true
            }
        }
    }
}
