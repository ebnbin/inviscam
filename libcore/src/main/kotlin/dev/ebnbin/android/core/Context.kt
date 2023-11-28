package dev.ebnbin.android.core

import android.content.Context
import android.content.Intent
import android.os.Build

val Context.versionCode: Int
    get() = if (Build.VERSION.SDK_INT >= SDK_28_P_9) {
        packageManager.getPackageInfo(packageName, 0).longVersionCode.toInt()
    } else {
        @Suppress("DEPRECATION")
        packageManager.getPackageInfo(packageName, 0).versionCode
    }

fun Context.openAppIntent(): Intent? {
    return packageManager.getLaunchIntentForPackage(packageName)
}

fun Context.openApp() {
    val intent = openAppIntent() ?: return
    startActivity(intent)
}
