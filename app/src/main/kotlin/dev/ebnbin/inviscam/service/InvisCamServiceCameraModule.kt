package dev.ebnbin.inviscam.service

import androidx.lifecycle.LiveData
import dev.ebnbin.android.core.get
import dev.ebnbin.android.core.map
import dev.ebnbin.inviscam.type.CaptureMode
import dev.ebnbin.inviscam.type.PreviewMode
import dev.ebnbin.inviscam.util.CameraHelper

interface InvisCamServiceCameraModule : InvisCamServiceModule {
    val isTakingPicture: LiveData<Boolean>

    val isRecordingVideo: LiveData<Boolean>

    val recordedDuration: LiveData<Long>

    val isPreviewing: LiveData<Boolean>

    val isCapturing: LiveData<Boolean>

    fun toggleCaptureMode()

    fun takePicture()

    fun startRecordingVideo()

    fun stopRecordingVideo()

    fun toggleRecordingVideo()

    fun capture()

    fun keepAwake(keepAwake: Boolean)

    fun enterSleepMode()

    fun exitSleepMode()

    fun toggleSleepMode()

    fun getZoomRatio(): Float

    fun setZoomRatio(ratio: Float)
}

class InvisCamServiceCameraModuleImpl(
    override val callback: InvisCamServiceCallback,
) : InvisCamServiceCameraModule {
    private val cameraHelper: CameraHelper = CameraHelper(
        context = callback.context,
        lifecycleOwner = callback.lifecycle.lifecycleOwner,
        lensFacing = callback.profile.pref.lensFacing,
        previewMode = callback.profile.pref.previewMode,
        captureMode = callback.profile.pref.captureMode,
        sleepModeTimeout = callback.profile.pref.sleepModeTimeout,
    )

    override fun onStart() {
        cameraHelper.setUp(
            surfaceProvider = callback.preview.surfaceProvider,
            rotation = callback.window.windowSize.map { it.rotation },
            zoom = callback.profile.pref.zoom,
        )
    }

    override fun onStop() {
        cameraHelper.shutdown()
    }

    override val isTakingPicture: LiveData<Boolean>
        get() = cameraHelper.isTakingPicture

    override val isRecordingVideo: LiveData<Boolean>
        get() = cameraHelper.isRecordingVideo

    override val recordedDuration: LiveData<Long>
        get() = cameraHelper.recordedDuration

    override val isPreviewing: LiveData<Boolean>
        get() = cameraHelper.isPreviewing

    override val isCapturing: LiveData<Boolean>
        get() = cameraHelper.isCapturing

    override fun toggleCaptureMode() {
        if (callback.profile.pref.previewMode.get() == PreviewMode.PREVIEW_ONLY) {
            return
        }
        when (callback.profile.pref.captureMode.get()) {
            CaptureMode.PHOTO -> callback.profile.pref.captureMode.apply(CaptureMode.VIDEO)
            CaptureMode.VIDEO -> callback.profile.pref.captureMode.apply(CaptureMode.PHOTO)
            CaptureMode.PHOTO_AND_VIDEO -> Unit
        }
    }

    override fun takePicture() {
        cameraHelper.takePicture()
    }

    override fun startRecordingVideo() {
        cameraHelper.startRecordingVideo()
    }

    override fun stopRecordingVideo() {
        cameraHelper.stopRecordingVideo()
    }

    override fun toggleRecordingVideo() {
        cameraHelper.toggleRecordingVideo()
    }

    override fun capture() {
        cameraHelper.capture()
    }

    override fun keepAwake(keepAwake: Boolean) {
        cameraHelper.keepAwake(keepAwake)
    }

    override fun enterSleepMode() {
        cameraHelper.enterSleepMode()
    }

    override fun exitSleepMode() {
        cameraHelper.exitSleepMode()
    }

    override fun toggleSleepMode() {
        cameraHelper.toggleSleepMode()
    }

    override fun getZoomRatio(): Float {
        return cameraHelper.getZoomRatio()
    }

    override fun setZoomRatio(ratio: Float) {
        val zoom = cameraHelper.getPercentageByRatio(ratio) ?: return
        callback.profile.pref.zoom.apply(zoom)
    }
}
