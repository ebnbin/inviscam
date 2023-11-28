package dev.ebnbin.inviscam.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import dev.ebnbin.android.core.appcompatAttr
import dev.ebnbin.android.core.getColorAttr
import dev.ebnbin.android.core.openAppIntent
import dev.ebnbin.inviscam.R

interface InvisCamServiceForegroundModule : InvisCamServiceModule {
    companion object {
        private const val NOTIFICATION_CHANNEL_NAME = "InvisCamService"

        val NOTIFICATION_CHANNEL: NotificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_NONE,
        )
    }
}

class InvisCamServiceForegroundModuleImpl(
    override val callback: InvisCamServiceCallback,
) : InvisCamServiceForegroundModule {
    override fun onStart() {
        val openAppIntent = callback.context.openAppIntent()
        val openAppPendingIntent = openAppIntent?.let {
            PendingIntent.getActivity(callback.context, 0, it, PendingIntent.FLAG_IMMUTABLE)
        }
        val stopServicePendingIntent = PendingIntent.getBroadcast(callback.context, 0,
            Intent(callback.context, StopServiceBroadcastReceiver::class.java), PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(callback.context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo_24)
            .setContentTitle(callback.context.getString(R.string.service_notification_title))
            .also { if (openAppPendingIntent != null) it.setContentIntent(openAppPendingIntent) }
            .addAction(R.drawable.gesture_action_stop_service,
                callback.context.getString(R.string.gesture_action_stop_service), stopServicePendingIntent)
            .setColor(callback.theme.context.getColorAttr(appcompatAttr.colorPrimary))
            .setShowWhen(false)
            .setOnlyAlertOnce(true)
            .build()
        callback.startForeground(NOTIFICATION_ID, notification)
    }

    override fun onStop() {
        callback.stopForeground()
    }

    companion object {
        private const val NOTIFICATION_ID = 1
    }
}

private const val NOTIFICATION_CHANNEL_ID = "inviscam_service"
