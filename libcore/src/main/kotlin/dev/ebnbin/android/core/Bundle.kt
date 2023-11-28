package dev.ebnbin.android.core

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment

fun Bundle.hasKey(key: String): Boolean {
    return containsKey(key)
}

inline fun <reified T : Any> Bundle.valueOrNull(key: String): T? {
    @Suppress("DEPRECATION")
    return get(key) as T?
}

inline fun <reified T : Any> Bundle.valueOrThrow(key: String): T {
    return requireNotNull(valueOrNull(key))
}

inline fun <reified T : Any> Bundle.valueOrDefault(key: String, defaultValue: T): T {
    return if (hasKey(key)) {
        valueOrThrow(key)
    } else {
        defaultValue
    }
}

fun Bundle.putAll(vararg pairs: Pair<String, Any?>): Bundle {
    putAll(bundleOf(*pairs))
    return this
}

//*********************************************************************************************************************

inline fun <reified T : Any> Intent.extraOrNull(key: String): T? {
    return extras?.valueOrNull(key)
}

inline fun <reified T : Any> Intent.extraOrThrow(key: String): T {
    return requireNotNull(extraOrNull(key))
}

inline fun <reified T : Any> Intent.extraOrDefault(key: String, defaultValue: T): T {
    return if (hasExtra(key)) {
        extraOrThrow(key)
    } else {
        defaultValue
    }
}

fun Intent.putExtras(vararg pairs: Pair<String, Any?>): Intent {
    return putExtras(bundleOf(*pairs))
}

//*********************************************************************************************************************

fun Activity.hasExtra(key: String): Boolean {
    return intent?.hasExtra(key) ?: false
}

inline fun <reified T : Any> Activity.extraOrNull(key: String): T? {
    return intent?.extraOrNull(key)
}

inline fun <reified T : Any> Activity.extraOrThrow(key: String): T {
    return requireNotNull(extraOrNull(key))
}

inline fun <reified T : Any> Activity.extraOrDefault(key: String, defaultValue: T): T {
    return if (hasExtra(key)) {
        extraOrThrow(key)
    } else {
        defaultValue
    }
}

//*********************************************************************************************************************

fun Fragment.hasArgument(key: String): Boolean {
    return arguments?.hasKey(key) ?: false
}

inline fun <reified T : Any> Fragment.argumentOrNull(key: String): T? {
    return arguments?.valueOrNull(key)
}

inline fun <reified T : Any> Fragment.argumentOrThrow(key: String): T {
    return requireNotNull(argumentOrNull(key))
}

inline fun <reified T : Any> Fragment.argumentOrDefault(key: String, defaultValue: T): T {
    return if (hasArgument(key)) {
        argumentOrThrow(key)
    } else {
        defaultValue
    }
}
