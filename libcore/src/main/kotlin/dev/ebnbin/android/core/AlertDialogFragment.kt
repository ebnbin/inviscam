package dev.ebnbin.android.core

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.text.InputFilter
import android.text.InputType
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textfield.TextInputLayout
import dev.ebnbin.android.core.databinding.AlertDialogFragmentEditTextBinding
import dev.ebnbin.android.core.databinding.AlertDialogFragmentMaterialColorPickerBinding
import dev.ebnbin.android.core.databinding.AlertDialogFragmentMaterialColorPickerItemBinding
import kotlinx.parcelize.Parcelize

class AlertDialogFragment : AppCompatDialogFragment() {
    class Builder(
        private val context: Context,
        private val requestKey: String,
    ) {
        private val args: Bundle = bundleOf(
            KEY_REQUEST_KEY to requestKey,
        )

        fun title(title: CharSequence?): Builder {
            if (title == null) {
                args.remove(KEY_TITLE)
            } else {
                args.putAll(
                    KEY_TITLE to title,
                )
            }
            return this
        }

        fun title(@StringRes titleId: Int): Builder {
            return title(if (titleId == 0) null else context.getString(titleId))
        }

        private fun clearContent() {
            args.remove(KEY_MESSAGE)
            args.remove(KEY_SINGLE_CHOICE_ITEMS)
            args.remove(KEY_SINGLE_CHOICE_CHECKED_ITEM)
            args.remove(KEY_EDIT_TEXT_TYPE)
            args.remove(KEY_EDIT_TEXT_TEXT)
            args.remove(KEY_EDIT_TEXT_PLACEHOLDER)
            args.remove(KEY_EDIT_TEXT_HELPER)
            args.remove(KEY_EDIT_TEXT_TEXT_MAX_LENGTH)
            args.remove(KEY_MATERIAL_COLOR_PICKER_COLOR)
        }

        fun message(message: CharSequence?): Builder {
            clearContent()
            if (message != null) {
                args.putAll(
                    KEY_MESSAGE to message,
                )
            }
            return this
        }

        fun message(@StringRes messageId: Int): Builder {
            return message(if (messageId == 0) null else context.getString(messageId))
        }

        fun singleChoice(
            items: Array<out CharSequence>,
            checkedItem: Int = -1,
        ): Builder {
            clearContent()
            args.putAll(
                KEY_SINGLE_CHOICE_ITEMS to items,
                KEY_SINGLE_CHOICE_CHECKED_ITEM to checkedItem,
            )
            return this
        }

        fun editText(
            type: EditTextType,
            text: String = "",
            placeholder: CharSequence? = null,
            helper: CharSequence? = null,
            textMaxLength: Int = -1,
        ): Builder {
            clearContent()
            args.putAll(
                KEY_EDIT_TEXT_TYPE to type,
                KEY_EDIT_TEXT_TEXT to text,
                KEY_EDIT_TEXT_PLACEHOLDER to placeholder,
                KEY_EDIT_TEXT_HELPER to helper,
                KEY_EDIT_TEXT_TEXT_MAX_LENGTH to textMaxLength,
            )
            return this
        }

        fun materialColorPicker(
            color: MaterialColor,
        ): Builder {
            clearContent()
            args.putAll(
                KEY_MATERIAL_COLOR_PICKER_COLOR to color,
            )
            return this
        }

        fun positiveText(positiveText: CharSequence?): Builder {
            if (positiveText == null) {
                args.remove(KEY_POSITIVE_TEXT)
            } else {
                args.putAll(
                    KEY_POSITIVE_TEXT to positiveText,
                )
            }
            return this
        }

        fun positiveText(@StringRes positiveTextId: Int): Builder {
            return positiveText(if (positiveTextId == 0) null else context.getString(positiveTextId))
        }

        fun positiveText(): Builder {
            return positiveText(android.R.string.ok)
        }

        fun negativeText(negativeText: CharSequence?): Builder {
            if (negativeText == null) {
                args.remove(KEY_NEGATIVE_TEXT)
            } else {
                args.putAll(
                    KEY_NEGATIVE_TEXT to negativeText,
                )
            }
            return this
        }

        fun negativeText(@StringRes negativeTextId: Int): Builder {
            return negativeText(if (negativeTextId == 0) null else context.getString(negativeTextId))
        }

        fun negativeText(): Builder {
            return negativeText(android.R.string.cancel)
        }

        fun cancelable(cancelable: Cancelable): Builder {
            args.putAll(
                KEY_CANCELABLE to cancelable,
            )
            return this
        }

        fun resultExtras(vararg pairs: Pair<String, Any?>): Builder {
            val bundle = args.valueOrDefault(KEY_RESULT_EXTRAS, Bundle())
            bundle.putAll(*pairs)
            args.putAll(
                KEY_RESULT_EXTRAS to bundle,
            )
            return this
        }

        fun clearResultExtras(): Builder {
            args.remove(KEY_RESULT_EXTRAS)
            return this
        }

        fun show(fragmentManager: FragmentManager) {
            fragmentManager.commit(allowStateLoss = true) {
                add(AlertDialogFragment::class.java, args, requestKey)
            }
        }
    }

    enum class EditTextType {
        TEXT,
        NUMBER_INT,
        ;
    }

    enum class Cancelable {
        CANCELABLE,
        NOT_CANCELABLE_ON_TOUCH_OUTSIDE,
        NOT_CANCELABLE,
        ;
    }

    enum class ResultType {
        POSITIVE,
        NEGATIVE,
        SINGLE_CHOICE,
        MATERIAL_COLOR_PICKER,
        CANCEL,
        DISMISS,
        ;
    }

    @Parcelize
    data class Result(
        val type: ResultType,
        val singleChoiceCheckedItem: Int,
        val editTextText: String,
        val materialColorPickerColor: MaterialColor,
        val resultExtras: Bundle,
    ) : Parcelable

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var editTextBinding: AlertDialogFragmentEditTextBinding? = null

        val builder = MaterialAlertDialogBuilder(requireContext(), theme)
        if (hasArgument(KEY_TITLE)) {
            builder.setTitle(argumentOrThrow<CharSequence>(KEY_TITLE))
        }
        when {
            hasArgument(KEY_MESSAGE) -> {
                builder.setMessage(argumentOrThrow<CharSequence>(KEY_MESSAGE))
            }
            hasArgument(KEY_SINGLE_CHOICE_ITEMS) -> {
                builder.setSingleChoiceItems(
                    argumentOrThrow<Array<out CharSequence>>(KEY_SINGLE_CHOICE_ITEMS),
                    argumentOrThrow(KEY_SINGLE_CHOICE_CHECKED_ITEM),
                ) { _, which ->
                    setFragmentResult(
                        resultType = ResultType.SINGLE_CHOICE,
                        singleChoiceCheckedItem = which,
                        dismiss = true,
                    )
                }
            }
            hasArgument(KEY_EDIT_TEXT_TYPE) -> {
                editTextBinding = AlertDialogFragmentEditTextBinding.inflate(layoutInflater)
                val type = argumentOrThrow<EditTextType>(KEY_EDIT_TEXT_TYPE)
                val textMaxLength = argumentOrThrow<Int>(KEY_EDIT_TEXT_TEXT_MAX_LENGTH)
                editTextBinding.textInputLayout.apply {
                    placeholderText = argumentOrNull<CharSequence>(KEY_EDIT_TEXT_PLACEHOLDER)
                    helperText = argumentOrNull<CharSequence>(KEY_EDIT_TEXT_HELPER)
                    endIconMode = TextInputLayout.END_ICON_CLEAR_TEXT
                    when (type) {
                        EditTextType.TEXT -> {
                            isCounterEnabled = true
                            counterMaxLength = textMaxLength
                        }
                        EditTextType.NUMBER_INT -> {
                            isCounterEnabled = false
                        }
                    }
                }
                editTextBinding.editText.apply {
                    val text = argumentOrThrow<String>(KEY_EDIT_TEXT_TEXT)
                    setText(text)
                    setSelection(text.length)
                    when (type) {
                        EditTextType.TEXT -> {
                            inputType = InputType.TYPE_CLASS_TEXT
                            if (textMaxLength != -1) {
                                filters = arrayOf(
                                    InputFilter.LengthFilter(textMaxLength),
                                )
                            }
                        }
                        EditTextType.NUMBER_INT -> {
                            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
                        }
                    }
                    requestFocus()
                }
                builder.setView(editTextBinding.root)
            }
            hasArgument(KEY_MATERIAL_COLOR_PICKER_COLOR) -> {
                val color = argumentOrThrow<MaterialColor>(KEY_MATERIAL_COLOR_PICKER_COLOR)
                val binding = AlertDialogFragmentMaterialColorPickerBinding.inflate(layoutInflater)

                fun updateUseFullChroma(view: MaterialSwitch, isChecked: Boolean) {
                    view.isChecked = isChecked
                    val text = if (isChecked) {
                        R.string.alert_dialog_fragment_use_full_chroma_on
                    } else {
                        R.string.alert_dialog_fragment_use_full_chroma_off
                    }
                    view.setText(text)
                    binding.fullChroma.isVisible = isChecked
                    binding.halfChroma.isVisible = !isChecked
                }

                fun initColorView(
                    binding: AlertDialogFragmentMaterialColorPickerItemBinding,
                    materialColor: MaterialColor,
                ) {
                    binding.cardView.apply {
                        strokeWidth = (if (color == materialColor) 4f else 0f).dpToPxRound
                        setOnClickListener {
                            setFragmentResult(
                                resultType = ResultType.MATERIAL_COLOR_PICKER,
                                materialColorPickerColor = materialColor,
                                dismiss = true,
                            )
                        }
                    }
                    binding.colorLight.setBackgroundColor(requireContext().getColor(materialColor.lightColorId))
                    binding.colorDark.setBackgroundColor(requireContext().getColor(materialColor.darkColorId))
                }

                builder.setView(binding.root)
                builder.setBackgroundInsetStart(0)
                builder.setBackgroundInsetEnd(0)
                updateUseFullChroma(binding.useFullChroma, color.isFull)
                binding.useFullChroma.setOnCheckedChangeListener { buttonView, isChecked ->
                    updateUseFullChroma(buttonView as MaterialSwitch, isChecked)
                }
                initColorView(binding.colorPinkFull, MaterialColor.PINK_FULL)
                initColorView(binding.colorRedFull, MaterialColor.RED_FULL)
                initColorView(binding.colorOrangeFull, MaterialColor.ORANGE_FULL)
                initColorView(binding.colorAmberFull, MaterialColor.AMBER_FULL)
                initColorView(binding.colorLimeFull, MaterialColor.LIME_FULL)
                initColorView(binding.colorGreenFull, MaterialColor.GREEN_FULL)
                initColorView(binding.colorTealFull, MaterialColor.TEAL_FULL)
                initColorView(binding.colorCyanFull, MaterialColor.CYAN_FULL)
                initColorView(binding.colorLightBlueFull, MaterialColor.LIGHT_BLUE_FULL)
                initColorView(binding.colorIndigoFull, MaterialColor.INDIGO_FULL)
                initColorView(binding.colorDeepPurpleFull, MaterialColor.DEEP_PURPLE_FULL)
                initColorView(binding.colorPurpleFull, MaterialColor.PURPLE_FULL)
                initColorView(binding.colorPinkHalf, MaterialColor.PINK_HALF)
                initColorView(binding.colorRedHalf, MaterialColor.RED_HALF)
                initColorView(binding.colorOrangeHalf, MaterialColor.ORANGE_HALF)
                initColorView(binding.colorAmberHalf, MaterialColor.AMBER_HALF)
                initColorView(binding.colorLimeHalf, MaterialColor.LIME_HALF)
                initColorView(binding.colorGreenHalf, MaterialColor.GREEN_HALF)
                initColorView(binding.colorTealHalf, MaterialColor.TEAL_HALF)
                initColorView(binding.colorCyanHalf, MaterialColor.CYAN_HALF)
                initColorView(binding.colorLightBlueHalf, MaterialColor.LIGHT_BLUE_HALF)
                initColorView(binding.colorIndigoHalf, MaterialColor.INDIGO_HALF)
                initColorView(binding.colorDeepPurpleHalf, MaterialColor.DEEP_PURPLE_HALF)
                initColorView(binding.colorPurpleHalf, MaterialColor.PURPLE_HALF)
            }
        }
        if (hasArgument(KEY_POSITIVE_TEXT)) {
            builder.setPositiveButton(argumentOrThrow<CharSequence>(KEY_POSITIVE_TEXT)) { _, _ ->
                setFragmentResult(
                    resultType = ResultType.POSITIVE,
                    editTextText = editTextBinding?.editText?.text?.toString() ?: "",
                )
            }
        }
        if (hasArgument(KEY_NEGATIVE_TEXT)) {
            builder.setNegativeButton(argumentOrNull<CharSequence>(KEY_NEGATIVE_TEXT)) { _, _ ->
                setFragmentResult(
                    resultType = ResultType.NEGATIVE,
                )
            }
        }

        val alertDialog = builder.create()
        val cancelable = argumentOrDefault<Cancelable>(KEY_CANCELABLE, Cancelable.CANCELABLE)
        alertDialog.setCanceledOnTouchOutside(cancelable == Cancelable.CANCELABLE)
        alertDialog.setOnShowListener { dialog ->
            dialog as AlertDialog
            editTextBinding?.editText?.apply {
                postDelayed(200L) {
                    context.inputMethodManager.showSoftInput(this, 0)
                }
            }
            if (hasArgument(KEY_MATERIAL_COLOR_PICKER_COLOR)) {
                dialog.window?.attributes = dialog.window?.attributes?.apply {
                    width = 296f.dpToPxRound
                    height = ViewGroup.LayoutParams.WRAP_CONTENT
                }
            }
        }

        isCancelable = cancelable != Cancelable.NOT_CANCELABLE
        return alertDialog
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        setFragmentResult(
            resultType = ResultType.CANCEL,
        )
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult(
            resultType = ResultType.DISMISS,
        )
    }

    private fun setFragmentResult(
        resultType: ResultType,
        singleChoiceCheckedItem: Int = -1,
        editTextText: String = "",
        materialColorPickerColor: MaterialColor = MaterialColor.INDIGO_FULL,
        dismiss: Boolean = false,
    ) {
        val result = Result(
            type = resultType,
            singleChoiceCheckedItem = singleChoiceCheckedItem,
            editTextText = editTextText,
            materialColorPickerColor = materialColorPickerColor,
            resultExtras = argumentOrDefault(KEY_RESULT_EXTRAS, Bundle.EMPTY),
        )
        setFragmentResult(
            requestKey = argumentOrThrow<String>(KEY_REQUEST_KEY),
            result = bundleOf(
                KEY_RESULT to result,
            ),
        )
        if (dismiss) {
            dismissAllowingStateLoss()
        }
    }

    companion object {
        private const val KEY_REQUEST_KEY = "alert_dialog_fragment_request_key"
        private const val KEY_TITLE = "alert_dialog_fragment_title"
        private const val KEY_MESSAGE = "alert_dialog_fragment_message"
        private const val KEY_SINGLE_CHOICE_ITEMS = "alert_dialog_fragment_single_choice_items"
        private const val KEY_SINGLE_CHOICE_CHECKED_ITEM = "alert_dialog_fragment_single_choice_checked_item"
        private const val KEY_EDIT_TEXT_TYPE = "alert_dialog_fragment_edit_text_type"
        private const val KEY_EDIT_TEXT_TEXT = "alert_dialog_fragment_edit_text_text"
        private const val KEY_EDIT_TEXT_PLACEHOLDER = "alert_dialog_fragment_edit_text_placeholder"
        private const val KEY_EDIT_TEXT_HELPER = "alert_dialog_fragment_edit_text_helper"
        private const val KEY_EDIT_TEXT_TEXT_MAX_LENGTH = "alert_dialog_fragment_edit_text_text_max_length"
        private const val KEY_MATERIAL_COLOR_PICKER_COLOR = "alert_dialog_fragment_material_color_picker_color"
        private const val KEY_POSITIVE_TEXT = "alert_dialog_fragment_positive_text"
        private const val KEY_NEGATIVE_TEXT = "alert_dialog_fragment_negative_text"
        private const val KEY_CANCELABLE = "alert_dialog_fragment_cancelable"
        private const val KEY_RESULT_EXTRAS = "alert_dialog_fragment_result_extras"
        private const val KEY_RESULT = "alert_dialog_fragment_result"

        fun FragmentManager.setAlertDialogResultListener(
            requestKey: String,
            lifecycleOwner: LifecycleOwner,
            onResult: (Result) -> Unit,
        ) {
            setFragmentResultListener(requestKey, lifecycleOwner) { _, result ->
                onResult(result.valueOrThrow(KEY_RESULT))
            }
        }
    }
}
