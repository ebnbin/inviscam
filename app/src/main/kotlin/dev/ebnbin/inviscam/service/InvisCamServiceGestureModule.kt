package dev.ebnbin.inviscam.service

import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import androidx.core.view.GestureDetectorCompat
import dev.ebnbin.inviscam.type.Gesture
import kotlin.math.roundToInt

interface InvisCamServiceGestureModule : InvisCamServiceModule {
    interface Target {
        val enableMove: Boolean

        val enableScale: Boolean

        val positionX: Int

        val positionY: Int

        val scale: Float

        fun onDown()

        fun onUpOrCancel()

        fun onGesture(gesture: Gesture)

        fun onMove(positionX: Int, positionY: Int)

        fun onScale(scale: Float)
    }

    fun onTouchEvent(target: Target, event: MotionEvent): Boolean
}

class InvisCamServiceGestureModuleImpl(
    override val callback: InvisCamServiceCallback,
) : InvisCamServiceGestureModule,
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener,
    ScaleGestureDetector.OnScaleGestureListener {
    private lateinit var gestureDetector: GestureDetectorCompat
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    override fun onStart() {
        gestureDetector = GestureDetectorCompat(callback.context, this)
        scaleGestureDetector = ScaleGestureDetector(callback.context, this)
    }

    override fun onStop() {
    }

    private lateinit var target: InvisCamServiceGestureModule.Target

    // For move.
    private lateinit var downRawOffset: Pair<Float, Float>

    // For scale.
    private var scale: Float = 1f

    private var canScroll: Boolean = true
    private var isDoubleTapping: Boolean = false
    private var isLongPressing: Boolean = false

    override fun onTouchEvent(target: InvisCamServiceGestureModule.Target, event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                this.target = target
                target.onDown()
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                canScroll = false // Can not scroll when pointer down.
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDoubleTapping) {
                    if (isLongPressing) {
                        target.onGesture(Gesture.DOUBLE_LONG_PRESS_UP)
                    } else {
                        target.onGesture(Gesture.DOUBLE_TAP)
                    }
                } else {
                    if (isLongPressing) {
                        target.onGesture(Gesture.LONG_PRESS_UP)
                    }
                }
                target.onUpOrCancel()
                isDoubleTapping = false
                isLongPressing = false
                canScroll = true
            }
        }
        if (gestureDetector.onTouchEvent(event)) {
            return true
        }
        if (target.enableScale) {
            return scaleGestureDetector.onTouchEvent(event)
        }
        return false
    }

    //*****************************************************************************************************************

    override fun onDown(e: MotionEvent): Boolean {
        downRawOffset = (target.positionX - e.rawX) to (target.positionY - e.rawY)
        return false
    }

    override fun onShowPress(e: MotionEvent) {
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        if (!canScroll || isDoubleTapping || isLongPressing) {
            return false
        }
        if (!target.enableMove) {
            return false
        }
        target.onMove(
            positionX = (downRawOffset.first + e2.rawX).roundToInt(),
            positionY = (downRawOffset.second + e2.rawY).roundToInt(),
        )
        return false
    }

    override fun onLongPress(e: MotionEvent) {
        isLongPressing = true
        if (isDoubleTapping) {
            target.onGesture(Gesture.DOUBLE_LONG_PRESS)
        } else {
            target.onGesture(Gesture.LONG_PRESS)
        }
    }

    override fun onFling(e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    //*****************************************************************************************************************

    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        target.onGesture(Gesture.SINGLE_TAP)
        return false
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        isDoubleTapping = true
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent): Boolean {
        return false
    }

    //*****************************************************************************************************************

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scale *= detector.scaleFactor
        target.onScale(scale)
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        if (isDoubleTapping || isLongPressing) {
            return false
        }
        scale = target.scale
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
    }
}
