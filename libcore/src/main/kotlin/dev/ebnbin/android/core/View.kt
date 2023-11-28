package dev.ebnbin.android.core

import android.view.View
import android.view.WindowManager

fun View.updateWindowManagerLayoutParams(block: WindowManager.LayoutParams.() -> Unit) {
    val params = layoutParams as WindowManager.LayoutParams
    params.block()
    context.windowManager.updateViewLayout(this, params)
}
