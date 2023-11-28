package dev.ebnbin.inviscam.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import dev.ebnbin.android.core.SDK_29_Q_10
import dev.ebnbin.android.core.combine
import dev.ebnbin.android.core.firebaseCrashlytics
import dev.ebnbin.android.core.get
import dev.ebnbin.android.core.isPermissionGranted
import dev.ebnbin.android.core.mainHandler
import dev.ebnbin.android.core.map
import dev.ebnbin.android.core.observeOnce
import dev.ebnbin.android.core.set
import dev.ebnbin.android.core.switchMap
import dev.ebnbin.inviscam.R
import dev.ebnbin.inviscam.service.InvisCamService
import dev.ebnbin.inviscam.type.CaptureMode
import dev.ebnbin.inviscam.type.PreviewMode
import dev.ebnbin.inviscam.type.SleepModeTimeout
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

class CameraHelper(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val lensFacing: LiveData<Int>,
    private val previewMode: LiveData<PreviewMode>,
    private val captureMode: LiveData<CaptureMode>,
    private val sleepModeTimeout: LiveData<SleepModeTimeout>,
) {
    private val cameraUseCases: MutableLiveData<CameraUseCases?> = MutableLiveData(null)

    val isTakingPicture: LiveData<Boolean> = cameraUseCases.switchMap { it?.isTakingPicture ?: MutableLiveData(false) }

    val isRecordingVideo: LiveData<Boolean> = cameraUseCases
        .switchMap { it?.isRecordingVideo ?: MutableLiveData(false) }

    val recordedDuration: LiveData<Long> = cameraUseCases.switchMap { it?.recordedDuration ?: MutableLiveData(0L) }

    val isPreviewing: LiveData<Boolean> = cameraUseCases.switchMap { it?.isPreviewing ?: MutableLiveData(false) }

    val isCapturing: LiveData<Boolean> = combine(isTakingPicture, isRecordingVideo)
        .map { (isTakingPicture, isRecording) ->
            isTakingPicture || isRecording
        }

    //*****************************************************************************************************************

    fun setUp(
        surfaceProvider: Preview.SurfaceProvider,
        rotation: LiveData<Int>,
        zoom: LiveData<Float>,
    ) {
        cameraUseCases.observe(lifecycleOwner) { cameraUseCases ->
            cameraUseCases?.setPreviewSurfaceProvider(surfaceProvider)
        }
        combine(
            cameraUseCases,
            rotation,
        ).observe(lifecycleOwner) { (cameraUseCases, rotation) ->
            cameraUseCases?.setTargetRotation(rotation)
        }
        combine(
            cameraUseCases.switchMap { it?.camera ?: MutableLiveData(null) },
            zoom,
        ).observe(lifecycleOwner) { (camera, zoom) ->
            camera?.cameraControl?.setZoomRatio(getRatioByPercentage(camera, zoom))
        }
        combine(
            sleepModeTimeout,
            isCapturing,
            keepAwake,
        ).observe(lifecycleOwner) { (sleepModeTimeout, isCapturing, keepAwake) ->
            mainHandler.removeCallbacks(enterSleepModeRunnable)
            if (sleepModeTimeout == SleepModeTimeout.NEVER || isCapturing || keepAwake) {
                sleepMode.set(false)
            } else {
                if (sleepModeTimeout == SleepModeTimeout.IMMEDIATELY) {
                    sleepMode.set(true)
                } else {
                    sleepMode.set(false)
                    mainHandler.postDelayed(enterSleepModeRunnable, sleepModeTimeout.value)
                }
            }
        }
        combine(
            lensFacing,
            previewMode,
            captureMode,
        ).observe(lifecycleOwner) { (lensFacing, previewMode, captureMode) ->
            unbindUseCases()
            if (sleepMode.get()) {
                return@observe
            }
            bindUseCases(
                lensFacing = lensFacing,
                previewMode = previewMode,
                captureMode = captureMode,
            )
        }
        sleepMode.observe(lifecycleOwner) { sleepMode ->
            if (sleepMode) {
                sleepModeTimestamp = System.currentTimeMillis()
                unbindUseCases()
            } else {
                if (cameraUseCases.get() == null) {
                    bindUseCases(
                        lensFacing = lensFacing.get(),
                        previewMode = previewMode.get(),
                        captureMode = captureMode.get(),
                    )
                }
                AnalyticsHelper.sleepMode(
                    duration = System.currentTimeMillis() - sleepModeTimestamp,
                )
            }
        }
    }

    private fun bindUseCases(
        @CameraSelector.LensFacing lensFacing: Int,
        previewMode: PreviewMode,
        captureMode: CaptureMode,
    ): CameraUseCases {
        require(cameraUseCases.get() == null)
        return CameraUseCases(
            context = context,
            lifecycleOwner = lifecycleOwner,
            lensFacing = lensFacing,
            previewMode = previewMode,
            captureMode = captureMode,
            onUnbound = {
                cameraUseCases.set(null)
            },
        ).also {
            cameraUseCases.set(it)
        }
    }

    private fun unbindUseCases() {
        cameraUseCases.get()?.unbindAll()
    }

    fun shutdown() {
        unbindUseCases()
        mainHandler.removeCallbacks(enterSleepModeRunnable)
    }

    //*****************************************************************************************************************

    fun takePicture() {
        val previewMode = previewMode.get()
        val captureMode = captureMode.get()
        if (previewMode == PreviewMode.PREVIEW_ONLY || captureMode == CaptureMode.VIDEO) {
            return
        }
        val cameraUseCases = cameraUseCases.get() ?: bindUseCases(
            lensFacing = lensFacing.get(),
            previewMode = previewMode,
            captureMode = captureMode,
        )
        cameraUseCases.takePicture()
    }

    fun startRecordingVideo() {
        val previewMode = previewMode.get()
        val captureMode = captureMode.get()
        if (previewMode == PreviewMode.PREVIEW_ONLY || captureMode == CaptureMode.PHOTO) {
            return
        }
        val cameraUseCases = cameraUseCases.get() ?: bindUseCases(
            lensFacing = lensFacing.get(),
            previewMode = previewMode,
            captureMode = captureMode,
        )
        cameraUseCases.startRecordingVideo()
    }

    fun stopRecordingVideo() {
        cameraUseCases.get()?.stopRecordingVideo()
    }

    fun toggleRecordingVideo() {
        if (isRecordingVideo.get()) {
            stopRecordingVideo()
        } else {
            startRecordingVideo()
        }
    }

    fun capture() {
        when (captureMode.get()) {
            CaptureMode.PHOTO -> takePicture()
            CaptureMode.VIDEO -> toggleRecordingVideo()
            CaptureMode.PHOTO_AND_VIDEO -> Unit
        }
    }

    //*****************************************************************************************************************

    private val sleepMode: MutableLiveData<Boolean> = MutableLiveData(false)

    private var sleepModeTimestamp: Long = 0L

    private val keepAwake: MutableLiveData<Boolean> = MutableLiveData(false)

    private val enterSleepModeRunnable: Runnable = Runnable {
        sleepMode.set(true)
    }

    fun keepAwake(keepAwake: Boolean) {
        this.keepAwake.set(keepAwake)
    }

    fun enterSleepMode() {
        if (sleepMode.get()) {
            return
        }
        unbindUseCases()
        mainHandler.removeCallbacks(enterSleepModeRunnable)
        sleepMode.set(true)
    }

    fun exitSleepMode() {
        if (!sleepMode.get() || sleepModeTimeout.get() == SleepModeTimeout.IMMEDIATELY) {
            return
        }
        keepAwake.set(true)
        keepAwake.set(false)
    }

    fun toggleSleepMode() {
        if (sleepMode.get()) {
            exitSleepMode()
        } else {
            enterSleepMode()
        }
    }

    fun getZoomRatio(): Float {
        return cameraUseCases.get()?.getZoomRatio() ?: 1f
    }

    fun getPercentageByRatio(ratio: Float): Float? {
        return cameraUseCases.get()?.getPercentageByRatio(ratio)
    }

    //*****************************************************************************************************************

    private class CameraStateException(code: Int, cause: Throwable?) : RuntimeException("$code", cause)

    private class VideoRecordEventException(error: Int, cause: Throwable?) : RuntimeException("$error", cause)

    private class CameraUseCases(
        private val context: Context,
        private val lifecycleOwner: LifecycleOwner,
        @CameraSelector.LensFacing private val lensFacing: Int,
        private val previewMode: PreviewMode,
        private val captureMode: CaptureMode,
        private val onUnbound: () -> Unit,
    ) {
        private val cameraProvider: ProcessCameraProvider = ProcessCameraProvider.getInstance(context).get()

        private val executor: Executor = ContextCompat.getMainExecutor(context)

        private val canTakePicture: Boolean =
            previewMode != PreviewMode.PREVIEW_ONLY && captureMode != CaptureMode.VIDEO

        private val canRecordVideo: Boolean =
            previewMode != PreviewMode.PREVIEW_ONLY && captureMode != CaptureMode.PHOTO

        private val canPreview: Boolean = previewMode != PreviewMode.CAPTURE_ONLY

        private val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        private val imageCapture: ImageCapture? = if (canTakePicture) {
            ImageCapture.Builder().build()
        } else {
            null
        }

        private val videoCapture: VideoCapture<Recorder>? = if (canRecordVideo) {
            val recorder = Recorder.Builder()
                .setQualitySelector(QualitySelector.from(Quality.HIGHEST))
                .build()
            VideoCapture.withOutput(recorder)
        } else {
            null
        }

        private val preview: Preview? = if (canPreview) {
            Preview.Builder().build()
        } else {
            null
        }

        val camera: MutableLiveData<Camera?> = MutableLiveData(null)

        private val cameraOpen: LiveData<Boolean> = camera.switchMap {
            it?.cameraInfo?.cameraState?.map { cameraState ->
                cameraState.type == CameraState.Type.OPEN
            } ?: MutableLiveData(false)
        }

        private val onCameraErrorObserver: Observer<CameraState> = Observer { cameraState ->
            val error = cameraState.error
            if (error != null) {
                val throwable = CameraStateException(error.code, error.cause)
                val isCritical = error.type == CameraState.ErrorType.CRITICAL
                firebaseCrashlytics.recordException(throwable)
                if (isCritical) {
                    Toast.makeText(context, R.string.camera_error, Toast.LENGTH_SHORT).show()
                    InvisCamService.stop(
                        context = context,
                        where = AnalyticsHelper.StopServiceWhere.ERROR,
                    )
                }
            }
        }

        init {
            val tmpCamera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector)
            cameraProvider.unbindAll()
            tmpCamera.cameraInfo.cameraState.observeOnce(
                lifecycleOwner = lifecycleOwner,
                condition = { it.type == CameraState.Type.CLOSED },
            ) {
                if (isUnbound) {
                    return@observeOnce
                }
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    *listOfNotNull(imageCapture, videoCapture, preview).toTypedArray(),
                )
                camera.cameraInfo.cameraState.observe(lifecycleOwner, onCameraErrorObserver)
                this.camera.set(camera)
            }
        }

        private var isUnbound: Boolean = false

        private val boundTimestamp: Long = System.currentTimeMillis()

        private val _isTakingPicture: MutableLiveData<Boolean> = MutableLiveData(false)
        val isTakingPicture: LiveData<Boolean> = _isTakingPicture

        private val _recording: MutableLiveData<Recording?> = MutableLiveData(null)
        val isRecordingVideo: LiveData<Boolean> = _recording.map { it != null }

        private val _recordedDuration: MutableLiveData<Long> = MutableLiveData(0L)
        val recordedDuration: LiveData<Long> = _recordedDuration

        val isPreviewing: LiveData<Boolean> = cameraOpen.map { it && canPreview }

        fun getZoomRatio(): Float {
            return camera.get()?.cameraInfo?.zoomState?.get()?.zoomRatio ?: 1f
        }

        fun getPercentageByRatio(ratio: Float): Float {
            val camera = camera.get() ?: return 0f
            return getPercentageByRatio(camera, ratio)
        }

        fun setTargetRotation(rotation: Int) {
            imageCapture?.targetRotation = rotation
            videoCapture?.targetRotation = rotation
            preview?.targetRotation = rotation
        }

        fun setPreviewSurfaceProvider(surfaceProvider: Preview.SurfaceProvider) {
            preview?.setSurfaceProvider(surfaceProvider)
        }

        fun takePicture() {
            imageCapture ?: return
            cameraOpen.observeOnce(
                lifecycleOwner = lifecycleOwner,
                condition = { it },
            ) {
                if (_isTakingPicture.get()) {
                    return@observeOnce
                }
                internalTakePicture(
                    context = context,
                    executor = executor,
                    imageCapture = imageCapture,
                    onStart = {
                        _isTakingPicture.set(true)
                    },
                    onImageSaved = {
                        _isTakingPicture.set(false)
                        AnalyticsHelper.takePicture(
                            lensFacing = lensFacing,
                        )
                    },
                    onError = { throwable ->
                        _isTakingPicture.set(false)
                        Toast.makeText(context, R.string.camera_error_photo, Toast.LENGTH_SHORT).show()
                        firebaseCrashlytics.recordException(throwable)
                    },
                )
            }
        }

        fun startRecordingVideo() {
            videoCapture ?: return
            cameraOpen.observeOnce(
                lifecycleOwner = lifecycleOwner,
                condition = { it },
            ) {
                if (_recording.get() != null) {
                    return@observeOnce
                }
                internalStartRecordingVideo(
                    context = context,
                    executor = executor,
                    videoCapture = videoCapture,
                    onStart = { recording ->
                        _recording.set(recording)
                        _recordedDuration.set(0L)
                    },
                    onStatus = { recordedDuration ->
                        _recordedDuration.set(recordedDuration)
                    },
                    onFinalize = { throwable ->
                        _recording.set(null)
                        AnalyticsHelper.recordVideo(
                            lensFacing = lensFacing,
                            duration = _recordedDuration.get(),
                        )
                        if (throwable != null) {
                            Toast.makeText(context, R.string.camera_error_video, Toast.LENGTH_SHORT).show()
                            firebaseCrashlytics.recordException(throwable)
                        }
                    },
                )
            }
        }

        fun stopRecordingVideo() {
            videoCapture ?: return
            _recording.get()?.stop()
        }

        fun unbindAll() {
            internalUnbindAll(
                onUnbound = {
                    AnalyticsHelper.camera(
                        lensFacing = lensFacing,
                        previewMode = previewMode,
                        captureMode = captureMode,
                        duration = System.currentTimeMillis() - boundTimestamp,
                    )
                    onUnbound()
                },
            )
        }

        private fun internalUnbindAll(
            onUnbound: () -> Unit,
        ) {
            if (isUnbound) {
                return
            }
            isUnbound = true
            stopRecordingVideo()
            camera.get()?.cameraInfo?.cameraState?.removeObserver(onCameraErrorObserver)
            cameraProvider.unbindAll()
            onUnbound()
        }
    }

    companion object {
        val DIR = "${Environment.DIRECTORY_DCIM}/InvisCam/"

        private fun internalTakePicture(
            context: Context,
            executor: Executor,
            imageCapture: ImageCapture,
            onStart: () -> Unit,
            onImageSaved: () -> Unit,
            onError: (throwable: Throwable) -> Unit,
        ) {
            val contentValues = createContentValues(isVideo = false)
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues,
            ).build()
            imageCapture.takePicture(outputFileOptions, executor, object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onImageSaved()
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            })
            onStart()
        }

        @SuppressLint("MissingPermission")
        private fun internalStartRecordingVideo(
            context: Context,
            executor: Executor,
            videoCapture: VideoCapture<Recorder>,
            onStart: (recording: Recording) -> Unit,
            onStatus: (recordedDuration: Long) -> Unit,
            onFinalize: (throwable: Throwable?) -> Unit,
        ) {
            val contentValues = createContentValues(isVideo = true)
            val fileOutputOptions = MediaStoreOutputOptions.Builder(
                context.contentResolver,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            ).setContentValues(contentValues).build()
            var recording: Recording? = null
            recording = videoCapture.output
                .prepareRecording(context, fileOutputOptions)
                .also {
                    if (context.isPermissionGranted(Manifest.permission.RECORD_AUDIO)) {
                        it.withAudioEnabled()
                    }
                }
                .start(executor) { videoRecordEvent ->
                    when (videoRecordEvent) {
                        is VideoRecordEvent.Start -> {
                            onStart(requireNotNull(recording))
                        }
                        is VideoRecordEvent.Status -> {
                            onStatus(videoRecordEvent.recordingStats.recordedDurationNanos / 1_000_000L)
                        }
                        is VideoRecordEvent.Finalize -> {
                            val throwable = if (videoRecordEvent.hasError()) {
                                VideoRecordEventException(videoRecordEvent.error, videoRecordEvent.cause)
                            } else {
                                null
                            }
                            onFinalize(throwable)
                        }
                    }
                }
        }

        private fun createContentValues(isVideo: Boolean): ContentValues {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmssSSS",
                Locale.getDefault()).format(System.currentTimeMillis())
            val extension = if (isVideo) "mp4" else "jpg"
            val fileName = "InvisCam_$timestamp.$extension"
            return ContentValues().apply {
                if (Build.VERSION.SDK_INT >= SDK_29_Q_10) {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.RELATIVE_PATH, DIR)
                } else {
                    val dir = Environment.getExternalStoragePublicDirectory(DIR)
                    if (!dir.exists()) {
                        dir.mkdirs()
                    }
                    put(MediaStore.MediaColumns.DATA, File(dir, fileName).path)
                }
            }
        }

        private fun getRatioByPercentage(camera: Camera, percentage: Float): Float {
            val zoomState = camera.cameraInfo.zoomState.get()
            return when {
                percentage >= 1f -> zoomState.maxZoomRatio
                percentage <= -1f -> zoomState.minZoomRatio
                percentage > 0f -> {
                    val cropWidthInMaxZoom = 1f / zoomState.maxZoomRatio
                    val cropWidthInMinZoom = 1f
                    val cropWidth = cropWidthInMinZoom + (cropWidthInMaxZoom - cropWidthInMinZoom) * percentage
                    val ratio = 1f / cropWidth
                    ratio.coerceIn(1f, zoomState.maxZoomRatio)
                }
                percentage < 0f -> {
                    val cropWidthInMaxZoom = 1f
                    val cropWidthInMinZoom = 1f / zoomState.minZoomRatio
                    val cropWidth = cropWidthInMinZoom + (cropWidthInMaxZoom - cropWidthInMinZoom) * (percentage + 1f)
                    val ratio = 1f / cropWidth
                    ratio.coerceIn(zoomState.minZoomRatio, 1f)
                }
                else -> 1f
            }
        }

        private fun getPercentageByRatio(camera: Camera, ratio: Float): Float {
            val zoomState = camera.cameraInfo.zoomState.get()
            return when {
                zoomState.maxZoomRatio == zoomState.minZoomRatio -> 0f
                ratio == 1f -> 0f
                ratio >= zoomState.maxZoomRatio -> 1f
                ratio <= zoomState.minZoomRatio -> -1f
                ratio > 1f -> {
                    val cropWidth = 1f / ratio
                    val cropWidthInMaxZoom = 1f / zoomState.maxZoomRatio
                    val cropWidthInMinZoom = 1f
                    (cropWidth - cropWidthInMinZoom) / (cropWidthInMaxZoom - cropWidthInMinZoom)
                }
                ratio < 1f -> {
                    val cropWidth = 1f / ratio
                    val cropWidthInMaxZoom = 1f
                    val cropWidthInMinZoom = 1f / zoomState.minZoomRatio
                    (cropWidth - cropWidthInMinZoom) / (cropWidthInMaxZoom - cropWidthInMinZoom) - 1f
                }
                else -> 0f
            }
        }
    }
}
