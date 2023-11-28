package dev.ebnbin.inviscam.service

import android.content.Context
import androidx.appcompat.view.ContextThemeWrapper
import dev.ebnbin.android.core.isNightMode
import dev.ebnbin.android.core.observeOnce
import dev.ebnbin.inviscam.R
import dev.ebnbin.inviscam.util.AnalyticsHelper

interface InvisCamServiceThemeModule : InvisCamServiceModule {
    val context: Context
}

class InvisCamServiceThemeModuleImpl(
    override val callback: InvisCamServiceCallback,
) : InvisCamServiceThemeModule {
    private var isNightMode: Boolean = false

    private var isNightModePending: Boolean = false

    override fun onStart() {
        isNightMode = callback.context.resources.configuration.isNightMode
    }

    override fun onStop() {
    }

    override fun onNightModeChanged(isNightMode: Boolean) {
        super.onNightModeChanged(isNightMode)
        if (isNightModePending) {
            return
        }
        if (this.isNightMode == isNightMode) {
            return
        }
        isNightModePending = true
        callback.camera.isCapturing.observeOnce(
            lifecycleOwner = callback.lifecycle.lifecycleOwner,
            condition = { !it },
        ) {
            if (callback.context.resources.configuration.isNightMode == this.isNightMode) {
                isNightModePending = false
                return@observeOnce
            }
            InvisCamService.start(
                context = callback.context,
                profile = callback.profile,
                force = true,
                where = AnalyticsHelper.StartServiceWhere.NIGHT_MODE,
            )
            isNightModePending = false
        }
    }

    override val context: Context
        get() {
            val themeId = if (isNightMode) {
                R.style.AppTheme_Dark
            } else {
                R.style.AppTheme_Light
            }
            return ContextThemeWrapper(callback.context, themeId)
        }
}
