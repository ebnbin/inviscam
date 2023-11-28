package dev.ebnbin.inviscam.service

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dev.ebnbin.android.core.SDK_34_U_14
import dev.ebnbin.android.core.WindowSize
import dev.ebnbin.android.core.get
import dev.ebnbin.android.core.set
import dev.ebnbin.android.core.windowManager

interface InvisCamServiceWindowModule : InvisCamServiceModule {
    val windowSize: LiveData<WindowSize>
}

class InvisCamServiceWindowModuleImpl(
    override val callback: InvisCamServiceCallback,
) : InvisCamServiceWindowModule {
    private lateinit var windowSizeView: WindowSizeView

    @SuppressLint("RtlHardcoded")
    override fun onStart() {
        windowSizeView = WindowSizeView(callback.theme.context)
        val params = WindowManager.LayoutParams().apply {
            width = ViewGroup.LayoutParams.MATCH_PARENT
            height = ViewGroup.LayoutParams.MATCH_PARENT
            x = 0
            y = 0
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            gravity = Gravity.LEFT or Gravity.TOP
            format = PixelFormat.RGBA_8888
            alpha = 0f
            if (Build.VERSION.SDK_INT >= SDK_34_U_14) {
                setCanPlayMoveAnimation(false)
            }
        }
        callback.context.windowManager.addView(windowSizeView, params)
    }

    override fun onStop() {
        callback.context.windowManager.removeView(windowSizeView)
    }

    override val windowSize: LiveData<WindowSize>
        get() = windowSizeView.windowSize
}

private class WindowSizeView(context: Context) : View(context) {
    private val _windowSize: MutableLiveData<WindowSize> = MutableLiveData(WindowSize.create(context))
    val windowSize: LiveData<WindowSize> = _windowSize

    private val orientationEventListener: OrientationEventListener = object : OrientationEventListener(this.context) {
        override fun onOrientationChanged(orientation: Int) {
            if (orientation == ORIENTATION_UNKNOWN) {
                return
            }
            update()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        update()
        orientationEventListener.enable()
    }

    override fun onDetachedFromWindow() {
        orientationEventListener.disable()
        super.onDetachedFromWindow()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        update(
            width = w,
            height = h,
        )
    }

    private fun update(
        width: Int = this.width,
        height: Int = this.height,
    ) {
        val windowSize = WindowSize.create(
            context = context,
            inWidth = if (width == 0) null else width,
            inHeight = if (height == 0) null else height,
        )
        if (_windowSize.get() == windowSize) {
            return
        }
        _windowSize.set(windowSize)
    }
}
