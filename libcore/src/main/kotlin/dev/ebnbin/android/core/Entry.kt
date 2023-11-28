package dev.ebnbin.android.core

import android.content.Context

interface Entry<T : Any> {
    val entryValue: T

    fun entryTitle(context: Context): CharSequence
}
