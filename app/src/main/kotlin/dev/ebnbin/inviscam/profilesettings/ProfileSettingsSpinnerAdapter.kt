package dev.ebnbin.inviscam.profilesettings

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import dev.ebnbin.android.core.androidAttr
import dev.ebnbin.android.core.appcompatAttr
import dev.ebnbin.android.core.dpToPxRound
import dev.ebnbin.android.core.get
import dev.ebnbin.android.core.setCompoundDrawableTintListAttr
import dev.ebnbin.android.core.setTextColorAttr
import dev.ebnbin.inviscam.type.Profile
import dev.ebnbin.inviscam.util.PrefManager

class ProfileSettingsSpinnerAdapter(
    context: Context,
    profileList: List<Profile>,
) : ArrayAdapter<Profile>(
    context,
    android.R.layout.simple_list_item_1,
    profileList,
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getView(position, convertView, parent).apply {
            this as TextView
            val profile = requireNotNull(getItem(position))
            updateView(this, profile)
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent).apply {
            this as TextView
            val profile = requireNotNull(getItem(position))
            updateDropDownView(this, profile)
        }
    }

    private fun updateView(textView: TextView, profile: Profile) {
        textView.apply {
            setText(profile.titleId)
            setTextColorAttr(androidAttr.textColorPrimary)
            setSingleLine()
            ellipsize = TextUtils.TruncateAt.END
            setCompoundDrawablesRelativeWithIntrinsicBounds(profile.iconId, 0, 0, 0)
            compoundDrawablePadding = 12f.dpToPxRound
            setCompoundDrawableTintListAttr(appcompatAttr.colorControlNormal)
        }
    }

    private fun updateDropDownView(textView: TextView, profile: Profile) {
        textView.apply {
            val runningProfile = PrefManager.profile.get()
            setText(profile.titleId)
            val textColorAttr = if (profile == runningProfile) {
                appcompatAttr.colorPrimary
            } else {
                androidAttr.textColorPrimary
            }
            setTextColorAttr(textColorAttr)
            setSingleLine()
            ellipsize = TextUtils.TruncateAt.END
            setCompoundDrawablesRelativeWithIntrinsicBounds(profile.iconId, 0, 0, 0)
            compoundDrawablePadding = 12f.dpToPxRound
            val iconColorAttr = if (profile == runningProfile) {
                appcompatAttr.colorPrimary
            } else {
                appcompatAttr.colorControlNormal
            }
            setCompoundDrawableTintListAttr(iconColorAttr)
        }
    }
}
