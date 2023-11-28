package dev.ebnbin.inviscam

import android.app.Application
import dev.ebnbin.android.core.get
import dev.ebnbin.android.core.notificationManager
import dev.ebnbin.android.core.versionCode
import dev.ebnbin.inviscam.service.InvisCamServiceForegroundModule
import dev.ebnbin.inviscam.util.PrefManager

class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val versionCode = versionCode
        if (PrefManager.appVersion.get() != versionCode) {
            PrefManager.appVersion.apply(versionCode)
        }
        notificationManager.createNotificationChannel(InvisCamServiceForegroundModule.NOTIFICATION_CHANNEL)
    }
}
