package dev.ebnbin.inviscam.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.ebnbin.inviscam.util.AnalyticsHelper

class StopServiceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        InvisCamService.stop(
            context = context,
            where = AnalyticsHelper.StopServiceWhere.NOTIFICATION,
        )
    }
}
