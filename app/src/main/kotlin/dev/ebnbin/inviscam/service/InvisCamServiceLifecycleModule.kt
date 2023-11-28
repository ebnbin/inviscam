package dev.ebnbin.inviscam.service

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import dev.ebnbin.inviscam.util.AnalyticsHelper

interface InvisCamServiceLifecycleModule : InvisCamServiceModule {
    val lifecycleOwner: LifecycleOwner
}

class InvisCamServiceLifecycleModuleImpl(
    override val callback: InvisCamServiceCallback,
) : InvisCamServiceLifecycleModule, LifecycleOwner {
    private var timestamp: Long = 0L

    private lateinit var dispatcher: ServiceLifecycleDispatcher

    override fun onStart() {
        timestamp = System.currentTimeMillis()

        dispatcher = ServiceLifecycleDispatcher(this)
        dispatcher.onServicePreSuperOnCreate()
        dispatcher.onServicePreSuperOnStart()
    }

    override fun onStop() {
        dispatcher.onServicePreSuperOnDestroy()

        AnalyticsHelper.profile(
            profile = callback.profile,
            duration = System.currentTimeMillis() - timestamp,
        )
    }

    override val lifecycleOwner: LifecycleOwner
        get() = this

    override val lifecycle: Lifecycle
        get() = dispatcher.lifecycle
}
