package dev.ebnbin.inviscam.service

import android.annotation.SuppressLint
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.WindowManager
import androidx.camera.core.Preview
import androidx.camera.view.PreviewView
import androidx.core.view.isVisible
import dev.ebnbin.android.core.SDK_34_U_14
import dev.ebnbin.android.core.combine
import dev.ebnbin.android.core.get
import dev.ebnbin.android.core.updateWindowManagerLayoutParams
import dev.ebnbin.android.core.windowManager
import dev.ebnbin.inviscam.type.Gesture
import dev.ebnbin.inviscam.type.PreviewRatio
import dev.ebnbin.inviscam.type.PreviewScaleAction
import kotlin.math.roundToInt

interface InvisCamServicePreviewModule : InvisCamServiceModule {
    val surfaceProvider: Preview.SurfaceProvider
}

class InvisCamServicePreviewModuleImpl(
    override val callback: InvisCamServiceCallback,
) : InvisCamServicePreviewModule, InvisCamServiceGestureModule.Target {
    private lateinit var previewView: PreviewView

    @SuppressLint("ClickableViewAccessibility", "RtlHardcoded")
    override fun onStart() {
        previewView = PreviewView(callback.theme.context)
        previewView.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        previewView.setOnTouchListener { _, event ->
            callback.gesture.onTouchEvent(this, event)
        }
        val params = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            gravity = Gravity.LEFT or Gravity.TOP
            format = PixelFormat.RGBA_8888
            if (Build.VERSION.SDK_INT >= SDK_34_U_14) {
                setCanPlayMoveAnimation(false)
            }
        }
        callback.context.windowManager.addView(previewView, params)

        callback.camera.isPreviewing.observe(callback.lifecycle.lifecycleOwner) { isPreviewing ->
            previewView.isVisible = isPreviewing
        }
        combine(
            callback.window.windowSize,
            callback.profile.pref.previewRatio,
            callback.profile.pref.previewEnableOut,
            callback.profile.pref.previewSize,
            callback.profile.pref.previewX,
            callback.profile.pref.previewY,
            callback.profile.pref.previewAlpha,
            callback.profile.pref.previewEnableTouch,
        ).observe(callback.lifecycle.lifecycleOwner) { tuple ->
            val (windowSize,
                previewRatio,
                previewEnableOut,
                previewSize,
                previewX,
                previewY,
                previewAlpha,
                previewEnableTouch,
            ) = tuple
            previewView.updateWindowManagerLayoutParams {
                val windowWidth = if (previewEnableOut) windowSize.outWidth else windowSize.inWidth
                val windowHeight = if (previewEnableOut) windowSize.outHeight else windowSize.inHeight
                val (maxWidth, maxHeight) = when (previewRatio) {
                    PreviewRatio.RATIO_4_3, PreviewRatio.RATIO_16_9 -> {
                        val (a, b) = when (previewRatio) {
                            PreviewRatio.RATIO_4_3 -> 4f to 3f
                            PreviewRatio.RATIO_16_9 -> 16f to 9f
                            else -> error(Unit)
                        }
                        if (windowWidth > windowHeight) { // landscape
                            if (windowWidth.toFloat() / windowHeight > a / b) { // wider
                                (windowHeight * a / b) to windowHeight.toFloat()
                            } else {
                                windowWidth.toFloat() to (windowWidth * b / a)
                            }
                        } else { // portrait
                            if (windowHeight.toFloat() / windowWidth > a / b) { // wider
                                windowWidth.toFloat() to (windowWidth * a / b)
                            } else {
                                (windowHeight * b / a) to windowHeight.toFloat()
                            }
                        }
                    }
                    PreviewRatio.MATCH_SCREEN -> windowWidth.toFloat() to windowHeight.toFloat()
                    PreviewRatio.RATIO_1_1 -> minOf(windowWidth, windowHeight).toFloat().let { it to it }
                }
                width = (maxWidth * previewSize).roundToInt()
                height = (maxHeight * previewSize).roundToInt()
                x = if (width == windowWidth) {
                    when {
                        previewX < 0f -> width * (previewX + 1f) - width
                        previewX > 1f -> width * (previewX - 1f)
                        else -> 0f
                    }
                } else {
                    when {
                        previewX < 0f -> width * (previewX + 1f) - width
                        previewX > 1f -> width * (previewX - 1f) - width + windowWidth
                        else -> (windowWidth - width) * previewX
                    }
                }.roundToInt()
                y = if (height == windowHeight) {
                    when {
                        previewY < 0f -> height * (previewY + 1f) - height
                        previewY > 1f -> height * (previewY - 1f)
                        else -> 0f
                    }
                } else {
                    when {
                        previewY < 0f -> height * (previewY + 1f) - height
                        previewY > 1f -> height * (previewY - 1f) - height + windowHeight
                        else -> (windowHeight - height) * previewY
                    }
                }.roundToInt()
                flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                if (!previewEnableTouch) {
                    flags = flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                }
                if (previewEnableOut) {
                    flags = flags or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                }
                alpha = previewAlpha
            }
        }
    }

    override fun onStop() {
        previewView.setOnTouchListener(null)
        callback.context.windowManager.removeView(previewView)
    }

    override val surfaceProvider: Preview.SurfaceProvider
        get() = previewView.surfaceProvider

    //*****************************************************************************************************************

    override val enableMove: Boolean
        get() = callback.profile.pref.previewEnableMove.get()

    override val enableScale: Boolean
        get() = callback.profile.pref.previewScaleAction.get() != PreviewScaleAction.NONE

    override val positionX: Int
        get() = (previewView.layoutParams as WindowManager.LayoutParams).x

    override val positionY: Int
        get() = (previewView.layoutParams as WindowManager.LayoutParams).y

    override val scale: Float
        get() = when (callback.profile.pref.previewScaleAction.get()) {
            PreviewScaleAction.WINDOW_SIZE -> callback.profile.pref.previewSize.get()
            PreviewScaleAction.CAMERA_ZOOM -> callback.camera.getZoomRatio()
            PreviewScaleAction.NONE -> 0f
        }

    override fun onDown() {
    }

    override fun onUpOrCancel() {
    }

    override fun onGesture(gesture: Gesture) {
        when (gesture) {
            Gesture.SINGLE_TAP -> callback.profile.pref.previewSingleTapAction
            Gesture.DOUBLE_TAP -> callback.profile.pref.previewDoubleTapAction
            Gesture.LONG_PRESS -> callback.profile.pref.previewLongPressAction
            Gesture.LONG_PRESS_UP -> callback.profile.pref.previewLongPressUpAction
            Gesture.DOUBLE_LONG_PRESS -> callback.profile.pref.previewDoubleLongPressAction
            Gesture.DOUBLE_LONG_PRESS_UP -> callback.profile.pref.previewDoubleLongPressUpAction
        }.get()
            .act(callback)
    }

    override fun onMove(positionX: Int, positionY: Int) {
        val layoutWidth = previewView.layoutParams.width
        val layoutHeight = previewView.layoutParams.height
        val windowSize = callback.window.windowSize.get()
        val previewEnableOut = callback.profile.pref.previewEnableOut.get()
        val windowWidth = if (previewEnableOut) windowSize.outWidth else windowSize.inWidth
        val windowHeight = if (previewEnableOut) windowSize.outHeight else windowSize.inHeight
        val previewX = if (layoutWidth == windowWidth) {
            when {
                positionX < 0 -> (positionX + layoutWidth).toFloat() / layoutWidth - 1f
                positionX > 0 -> positionX.toFloat() / layoutWidth + 1f
                else -> callback.profile.pref.previewX.get()
            }
        } else {
            when {
                positionX < 0 -> (positionX + layoutWidth).toFloat() / layoutWidth - 1f
                positionX > windowWidth - layoutWidth ->
                    (positionX + layoutWidth - windowWidth).toFloat() / layoutWidth + 1f
                else -> positionX.toFloat() / (windowWidth - layoutWidth)
            }
        }
        val previewY = if (layoutHeight == windowHeight) {
            when {
                positionY < 0 -> (positionY + layoutHeight).toFloat() / layoutHeight - 1f
                positionY > 0 -> positionY.toFloat() / layoutHeight + 1f
                else -> callback.profile.pref.previewY.get()
            }
        } else {
            when {
                positionY < 0 -> (positionY + layoutHeight).toFloat() / layoutHeight - 1f
                positionY > windowHeight - layoutHeight ->
                    (positionY + layoutHeight - windowHeight).toFloat() / layoutHeight + 1f
                else -> positionY.toFloat() / (windowHeight - layoutHeight)
            }
        }
        callback.profile.pref.previewX.apply(previewX)
        callback.profile.pref.previewY.apply(previewY)
    }

    override fun onScale(scale: Float) {
        when (callback.profile.pref.previewScaleAction.get()) {
            PreviewScaleAction.WINDOW_SIZE -> callback.profile.pref.previewSize.apply(scale)
            PreviewScaleAction.CAMERA_ZOOM -> callback.camera.setZoomRatio(scale)
            PreviewScaleAction.NONE -> Unit
        }
    }
}
