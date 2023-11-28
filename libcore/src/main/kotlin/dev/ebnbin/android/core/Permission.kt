package dev.ebnbin.android.core

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.content.ContextCompat

fun Context.isPermissionGranted(permission: String): Boolean {
    return when (permission) {
        Manifest.permission.SYSTEM_ALERT_WINDOW -> Settings.canDrawOverlays(this)
        else -> ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}

fun Context.arePermissionsGranted(permissionList: List<String>): Boolean {
    return permissionList.toSet().all { permission ->
        isPermissionGranted(permission)
    }
}
