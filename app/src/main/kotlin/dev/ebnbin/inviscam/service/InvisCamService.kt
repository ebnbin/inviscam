package dev.ebnbin.inviscam.service

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.ebnbin.android.core.SDK_29_Q_10
import dev.ebnbin.android.core.SDK_33_T_13
import dev.ebnbin.android.core.arePermissionsGranted
import dev.ebnbin.android.core.extraOrThrow
import dev.ebnbin.android.core.get
import dev.ebnbin.android.core.isNightMode
import dev.ebnbin.android.core.map
import dev.ebnbin.android.core.set
import dev.ebnbin.inviscam.type.Profile
import dev.ebnbin.inviscam.util.AnalyticsHelper
import dev.ebnbin.inviscam.util.PrefManager

interface InvisCamServiceCallback {
    val context: Context

    fun startForeground(id: Int, notification: Notification)

    fun stopForeground()

    val profile: Profile

    val lifecycle: InvisCamServiceLifecycleModule
    val theme: InvisCamServiceThemeModule
    val foreground: InvisCamServiceForegroundModule
    val gesture: InvisCamServiceGestureModule
    val window: InvisCamServiceWindowModule
    val preview: InvisCamServicePreviewModule
    val fab: InvisCamServiceFabModule
    val camera: InvisCamServiceCameraModule
}

interface InvisCamServiceModule {
    val callback: InvisCamServiceCallback

    fun onStart()

    fun onStop()

    fun onNightModeChanged(isNightMode: Boolean) {
    }
}

class InvisCamService : Service(), InvisCamServiceCallback {
    private lateinit var _lifecycle: InvisCamServiceLifecycleModule
    private lateinit var _theme: InvisCamServiceThemeModule
    private lateinit var _foreground: InvisCamServiceForegroundModule
    private lateinit var _gesture: InvisCamServiceGestureModule
    private lateinit var _window: InvisCamServiceWindowModule
    private lateinit var _preview: InvisCamServicePreviewModule
    private lateinit var _fab: InvisCamServiceFabModule
    private lateinit var _camera: InvisCamServiceCameraModule

    private val moduleList: List<InvisCamServiceModule>
        get() = listOf(
            _lifecycle,
            _theme,
            _foreground,
            _gesture,
            _window,
            _preview,
            _fab,
            _camera,
        )

    var createdTimestamp: Long = 0L
        private set

    private var runningProfile: Profile? = null

    override fun onCreate() {
        super.onCreate()
        runningService.set(this)
        createdTimestamp = System.currentTimeMillis()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId).also {
            requireNotNull(intent)
            val profile = intent.extraOrThrow<Profile>(KEY_PROFILE)
            val force = intent.extraOrThrow<Boolean>(KEY_FORCE)
            if (!force && runningProfile == profile) {
                return@also
            }
            if (runningProfile != null) {
                internalStop()
            }
            internalStart(profile)
        }
    }

    override fun onDestroy() {
        internalStop()
        runningService.set(null)
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        moduleList.forEach { module ->
            module.onNightModeChanged(newConfig.isNightMode)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun internalStart(profile: Profile) {
        runningProfile = profile
        PrefManager.profile.apply(profile)

        _lifecycle = InvisCamServiceLifecycleModuleImpl(this)
        _theme = InvisCamServiceThemeModuleImpl(this)
        _foreground = InvisCamServiceForegroundModuleImpl(this)
        _gesture = InvisCamServiceGestureModuleImpl(this)
        _window = InvisCamServiceWindowModuleImpl(this)
        _preview = InvisCamServicePreviewModuleImpl(this)
        _fab = InvisCamServiceFabModuleImpl(this)
        _camera = InvisCamServiceCameraModuleImpl(this)

        moduleList.forEach { module ->
            module.onStart()
        }
    }

    private fun internalStop() {
        moduleList.reversed().forEach { module ->
            module.onStop()
        }
        runningProfile = null
    }

    override val context: Context
        get() = this

    override fun stopForeground() {
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override val profile: Profile
        get() = requireNotNull(runningProfile)

    override val lifecycle: InvisCamServiceLifecycleModule
        get() = _lifecycle
    override val theme: InvisCamServiceThemeModule
        get() = _theme
    override val foreground: InvisCamServiceForegroundModule
        get() = _foreground
    override val gesture: InvisCamServiceGestureModule
        get() = _gesture
    override val window: InvisCamServiceWindowModule
        get() = _window
    override val preview: InvisCamServicePreviewModule
        get() = _preview
    override val fab: InvisCamServiceFabModule
        get() = _fab
    override val camera: InvisCamServiceCameraModule
        get() = _camera

    companion object {
        val PERMISSIONS: List<String> = if (Build.VERSION.SDK_INT >= SDK_29_Q_10) {
            listOf(
                android.Manifest.permission.SYSTEM_ALERT_WINDOW,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
            )
        } else {
            listOf(
                android.Manifest.permission.SYSTEM_ALERT_WINDOW,
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        }

        val OPTIONAL_PERMISSIONS: List<String> = if (Build.VERSION.SDK_INT >= SDK_33_T_13) {
            listOf(
                android.Manifest.permission.POST_NOTIFICATIONS,
            )
        } else {
            emptyList()
        }

        private const val KEY_PROFILE = "profile"
        private const val KEY_FORCE = "force"

        private var runningService: MutableLiveData<InvisCamService?> = MutableLiveData(null)

        val isRunning: LiveData<Boolean>
            get() = runningService.map { it != null }

        fun start(
            context: Context,
            profile: Profile = PrefManager.profile.get(),
            force: Boolean = false,
            where: AnalyticsHelper.StartServiceWhere,
        ) {
            if (!context.arePermissionsGranted(PERMISSIONS)) {
                return
            }
            val intent = Intent(context, InvisCamService::class.java)
                .putExtra(KEY_PROFILE, profile)
                .putExtra(KEY_FORCE, force)
            context.startForegroundService(intent)
            AnalyticsHelper.startService(
                profile = profile,
                where = where,
            )
        }

        fun stop(
            context: Context,
            where: AnalyticsHelper.StopServiceWhere,
        ) {
            val runningService = runningService.get() ?: return
            val intent = Intent(context, InvisCamService::class.java)
            context.stopService(intent)
            AnalyticsHelper.stopService(
                where = where,
                duration = System.currentTimeMillis() - runningService.createdTimestamp,
            )
        }
    }
}
