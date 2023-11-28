package dev.ebnbin.android.core

import android.app.NotificationManager
import android.content.Context
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService

private inline fun <reified T : Any> Context.systemService(): T {
    return requireNotNull(getSystemService<T>())
}

val Context.inputMethodManager: InputMethodManager
    get() = systemService()

val Context.layoutInflater: LayoutInflater
    get() = systemService()

val Context.notificationManager: NotificationManager
    get() = systemService()

val Context.windowManager: WindowManager
    get() = systemService()
