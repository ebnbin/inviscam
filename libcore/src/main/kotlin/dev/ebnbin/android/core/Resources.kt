package dev.ebnbin.android.core

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.core.widget.TextViewCompat
import com.google.android.material.color.MaterialColors
import kotlin.math.roundToInt

val Float.dpToPx: Float
    get() = this * coreApp.resources.displayMetrics.density

val Float.dpToPxRound: Int
    get() = dpToPx.roundToInt()

val Float.dpToPxInt: Int
    get() = dpToPx.toInt()

val Int.pxToDp: Float
    get() = this / coreApp.resources.displayMetrics.density

val Int.pxToDpRound: Int
    get() = pxToDp.roundToInt()

val Int.pxToDpInt: Int
    get() = pxToDp.toInt()

typealias androidAttr = android.R.attr
typealias appcompatAttr = androidx.appcompat.R.attr
typealias materialAttr = com.google.android.material.R.attr
typealias coreAttr = R.attr

fun Context.getColorAttr(@AttrRes attrId: Int): Int {
    return MaterialColors.getColor(this, attrId, 0)
}

fun View.getColorAttr(@AttrRes attrId: Int): Int {
    return MaterialColors.getColor(this, attrId)
}

fun TextView.setTextColorAttr(@AttrRes attrId: Int) {
    setTextColor(getColorAttr(attrId))
}

fun TextView.setCompoundDrawableTintListAttr(@AttrRes attrId: Int) {
    TextViewCompat.setCompoundDrawableTintList(this, ColorStateList.valueOf(getColorAttr(attrId)))
}

fun ImageView.setImageTintListAttr(@AttrRes attrId: Int) {
    imageTintList = ColorStateList.valueOf(getColorAttr(attrId))
}

fun Window.setNavigationBarColorAttr(context: Context, @AttrRes attrId: Int) {
    navigationBarColor = context.getColorAttr(attrId)
}

val Configuration.isNightMode: Boolean
    get() = uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
