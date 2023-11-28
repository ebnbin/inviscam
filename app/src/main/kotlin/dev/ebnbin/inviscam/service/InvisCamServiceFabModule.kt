package dev.ebnbin.inviscam.service

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.isVisible
import androidx.lifecycle.MutableLiveData
import dev.ebnbin.android.core.SDK_34_U_14
import dev.ebnbin.android.core.androidAttr
import dev.ebnbin.android.core.appcompatAttr
import dev.ebnbin.android.core.combine
import dev.ebnbin.android.core.coreAttr
import dev.ebnbin.android.core.dpToPxRound
import dev.ebnbin.android.core.get
import dev.ebnbin.android.core.layoutInflater
import dev.ebnbin.android.core.mainHandler
import dev.ebnbin.android.core.map
import dev.ebnbin.android.core.materialAttr
import dev.ebnbin.android.core.set
import dev.ebnbin.android.core.setCompoundDrawableTintListAttr
import dev.ebnbin.android.core.setImageTintListAttr
import dev.ebnbin.android.core.setTextColorAttr
import dev.ebnbin.android.core.updateWindowManagerLayoutParams
import dev.ebnbin.android.core.windowManager
import dev.ebnbin.inviscam.R
import dev.ebnbin.inviscam.databinding.FabViewBinding
import dev.ebnbin.inviscam.type.FabIdleTimeout
import dev.ebnbin.inviscam.type.Gesture
import dev.ebnbin.inviscam.type.GestureAction
import dev.ebnbin.inviscam.type.Profile
import dev.ebnbin.inviscam.util.AnalyticsHelper
import kotlin.math.roundToInt

interface InvisCamServiceFabModule : InvisCamServiceModule {
    fun openMenu()

    fun hide(hide: Boolean)
}

class InvisCamServiceFabModuleImpl(
    override val callback: InvisCamServiceCallback,
) : InvisCamServiceFabModule, InvisCamServiceGestureModule.Target {
    private lateinit var fabView: FabView
    private lateinit var spinner: AppCompatSpinner

    private val isIdling: MutableLiveData<Boolean> = MutableLiveData(false)
    private val keepActive: MutableLiveData<Boolean> = MutableLiveData(false)
    private val idleRunnable = Runnable {
        isIdling.set(true)
    }

    @SuppressLint("ClickableViewAccessibility", "RtlHardcoded", "SetTextI18n")
    override fun onStart() {
        fabView = FabView(callback.theme.context)
        fabView.binding.cardView.setOnTouchListener { _, event ->
            callback.gesture.onTouchEvent(this, event)
        }
        fabView.binding.icon.setImageResource(callback.profile.iconId)
        val fabViewParams = WindowManager.LayoutParams().apply {
            val size = FAB_SIZE.dpToPxRound
            width = size
            height = size
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            gravity = Gravity.LEFT or Gravity.TOP
            format = PixelFormat.RGBA_8888
            if (Build.VERSION.SDK_INT >= SDK_34_U_14) {
                setCanPlayMoveAnimation(false)
            }
        }
        callback.context.windowManager.addView(fabView, fabViewParams)

        spinner = AppCompatSpinner(callback.theme.context).apply {
            alpha = 0f
            background = null
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val adapter = adapter as FabSpinnerAdapter
                    when (position) {
                        adapter.count - 1 -> {
                            InvisCamService.stop(
                                context = callback.context,
                                where = AnalyticsHelper.StopServiceWhere.FAB_MENU,
                            )
                        }
                        adapter.count - 2 -> {
                            setSelection(adapter.getPosition(callback.profile), false)
                            callback.camera.toggleSleepMode()
                            AnalyticsHelper.fabMenuToggleSleepMode(
                                profile = callback.profile,
                            )
                        }
                        else -> {
                            InvisCamService.start(
                                context = callback.context,
                                profile = Profile.entries[position],
                                where = AnalyticsHelper.StartServiceWhere.FAB_MENU,
                            )
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
            adapter = FabSpinnerAdapter(
                context = context,
                profileList = Profile.entries + Unit + Unit, // Toggle sleep mode, stop service.
                runningProfile = callback.profile,
            )
            setSelection(Profile.entries.indexOf(callback.profile), false)
        }
        val spinnerParams = WindowManager.LayoutParams().apply {
            width = 0
            height = 0
            type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            gravity = Gravity.LEFT or Gravity.TOP
            format = PixelFormat.RGBA_8888
            alpha = 0f
            if (Build.VERSION.SDK_INT >= SDK_34_U_14) {
                setCanPlayMoveAnimation(false)
            }
        }
        callback.context.windowManager.addView(spinner, spinnerParams)

        combine(
            callback.window.windowSize,
            Profile.Pref.fabX,
            Profile.Pref.fabY,
        ).observe(callback.lifecycle.lifecycleOwner) { (windowSize, fabX, fabY) ->
            fabView.updateWindowManagerLayoutParams {
                val size = FAB_SIZE.dpToPxRound
                x = ((windowSize.inWidth - size) * fabX).roundToInt()
                y = ((windowSize.inHeight - size) * fabY).roundToInt()
            }
            spinner.updateWindowManagerLayoutParams {
                val size = FAB_SIZE.dpToPxRound
                x = ((windowSize.inWidth - size) * fabX + size / 2f).roundToInt()
                y = ((windowSize.inHeight - size) * fabY + size / 2f).roundToInt()
            }
        }
        combine(
            Profile.Pref.fabIdleTimeout,
            keepActive,
        ).observe(callback.lifecycle.lifecycleOwner) { (fabIdleTimeout, keepActive) ->
            mainHandler.removeCallbacks(idleRunnable)
            if (fabIdleTimeout == FabIdleTimeout.NEVER || keepActive) {
                isIdling.set(false)
            } else {
                if (fabIdleTimeout == FabIdleTimeout.IMMEDIATELY) {
                    isIdling.set(true)
                } else {
                    isIdling.set(false)
                    mainHandler.postDelayed(idleRunnable, fabIdleTimeout.value)
                }
            }
        }
        combine(
            isIdling,
            Profile.Pref.fabIdleAlpha,
        ).observe(callback.lifecycle.lifecycleOwner) { (isIdling, fabIdleAlpha) ->
            fabView.updateWindowManagerLayoutParams {
                alpha = if (isIdling) fabIdleAlpha else 1f
            }
        }
        combine(
            callback.camera.isTakingPicture,
            callback.camera.isRecordingVideo,
            callback.camera.isPreviewing,
        ).observe(callback.lifecycle.lifecycleOwner) { (isTakingPicture, isRecordingVideo, isPreviewing) ->
            val colorAttrId = when {
                isTakingPicture -> coreAttr.colorRedFull
                isRecordingVideo -> coreAttr.colorGreenFull
                isPreviewing -> coreAttr.colorIndigoFull
                else -> materialAttr.colorOnBackground
            }
            fabView.binding.icon.setImageTintListAttr(colorAttrId)
        }
        combine(
            callback.camera.isRecordingVideo,
            callback.camera.recordedDuration,
        ).map { (isRecordingVideo, recordedDuration) ->
            if (isRecordingVideo) {
                val minutes = (recordedDuration / 1_000 / 60).toInt()
                val seconds = (recordedDuration / 1_000 % 60).toInt()
                if (minutes > 99 && seconds > 59) {
                    99 to 59
                } else {
                    minutes to seconds
                }
            } else {
                null
            }
        }.observe(callback.lifecycle.lifecycleOwner) { pair ->
            if (pair == null) {
                fabView.binding.duration.isVisible = false
            } else {
                fabView.binding.duration.isVisible = true
                fabView.binding.duration.text = "%02d:%02d".format(pair.first, pair.second)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onStop() {
        callback.context.windowManager.removeView(spinner)
        fabView.binding.cardView.setOnTouchListener(null)
        callback.context.windowManager.removeView(fabView)
        mainHandler.removeCallbacks(idleRunnable)
    }

    override fun openMenu() {
        spinner.performClick()
    }

    override fun hide(hide: Boolean) {
        fabView.alpha = if (hide) 0f else 1f
    }

    // ****************************************************************************************************************

    override val enableMove: Boolean
        get() = true

    override val enableScale: Boolean
        get() = false

    override val positionX: Int
        get() = (fabView.layoutParams as WindowManager.LayoutParams).x

    override val positionY: Int
        get() = (fabView.layoutParams as WindowManager.LayoutParams).y

    override val scale: Float
        get() = 1f

    override fun onDown() {
        keepActive.set(true)
    }

    override fun onUpOrCancel() {
        keepActive.set(false)
    }

    override fun onGesture(gesture: Gesture) {
        when (gesture) {
            Gesture.LONG_PRESS -> callback.profile.pref.fabLongPressExtraAction
            Gesture.DOUBLE_LONG_PRESS -> callback.profile.pref.fabDoubleLongPressExtraAction
            else -> null
        }?.get()?.downAct(callback)
        when (gesture) {
            Gesture.SINGLE_TAP, Gesture.DOUBLE_TAP -> if (gesture == Profile.Pref.fabOpenMenuGesture.get()) {
                callback.fab.openMenu()
                AnalyticsHelper.openFabMenu(
                    profile = callback.profile,
                )
                null
            } else {
                callback.profile.pref.fabSingleOrDoubleTapAction
            }
            Gesture.LONG_PRESS -> callback.profile.pref.fabLongPressAction
            Gesture.LONG_PRESS_UP -> callback.profile.pref.fabLongPressUpAction
            Gesture.DOUBLE_LONG_PRESS -> callback.profile.pref.fabDoubleLongPressAction
            Gesture.DOUBLE_LONG_PRESS_UP -> callback.profile.pref.fabDoubleLongPressUpAction
        }?.get()?.act(callback)
        when (gesture) {
            Gesture.LONG_PRESS_UP -> callback.profile.pref.fabLongPressExtraAction
            Gesture.DOUBLE_LONG_PRESS_UP -> callback.profile.pref.fabDoubleLongPressExtraAction
            else -> null
        }?.get()?.upAct(callback)
    }

    override fun onMove(positionX: Int, positionY: Int) {
        val windowSize = callback.window.windowSize.get()
        val layoutSize = FAB_SIZE.dpToPxRound
        val fabX = positionX.toFloat() / (windowSize.inWidth - layoutSize)
        val fabY = positionY.toFloat() / (windowSize.inHeight - layoutSize)
        Profile.Pref.fabX.apply(fabX)
        Profile.Pref.fabY.apply(fabY)
    }

    override fun onScale(scale: Float) {
    }

    companion object {
        private const val FAB_SIZE = 88f
    }
}

private class FabView(context: Context) : FrameLayout(context) {
    val binding: FabViewBinding = FabViewBinding.inflate(this.context.layoutInflater, this)
}

private class FabSpinnerAdapter(
    context: Context,
    profileList: List<Any>,
    private val runningProfile: Profile,
) : ArrayAdapter<Any>(
    context,
    android.R.layout.simple_list_item_1,
    profileList,
) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getView(position, convertView, parent).apply {
            this as TextView
            this.text = ""
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return super.getDropDownView(position, convertView, parent).apply {
            this as TextView
            when (position) {
                count - 1 -> updateStopServiceDropDownView(this)
                count - 2 -> updateToggleSleepModeDropDownView(this)
                else -> updateDropDownView(this, getItem(position) as Profile)
            }
        }
    }

    private fun updateDropDownView(textView: TextView, profile: Profile) {
        textView.apply {
            setText(profile.titleId)
            val textColorAttr = if (profile == runningProfile) {
                appcompatAttr.colorPrimary
            } else {
                androidAttr.textColorPrimary
            }
            setTextColorAttr(textColorAttr)
            setSingleLine()
            ellipsize = TextUtils.TruncateAt.END
            setCompoundDrawablesRelativeWithIntrinsicBounds(profile.iconId, 0, 0, 0)
            compoundDrawablePadding = 12f.dpToPxRound
            val iconColorAttr = if (profile == runningProfile) {
                appcompatAttr.colorPrimary
            } else {
                appcompatAttr.colorControlNormal
            }
            setCompoundDrawableTintListAttr(iconColorAttr)
            background = null
        }
    }

    private fun updateToggleSleepModeDropDownView(textView: TextView) {
        textView.apply {
            setText(GestureAction.TOGGLE_SLEEP_MODE.titleId)
            setTextColorAttr(androidAttr.textColorPrimary)
            setSingleLine()
            ellipsize = TextUtils.TruncateAt.END
            setCompoundDrawablesRelativeWithIntrinsicBounds(GestureAction.TOGGLE_SLEEP_MODE.iconId, 0, 0, 0)
            compoundDrawablePadding = 12f.dpToPxRound
            setCompoundDrawableTintListAttr(appcompatAttr.colorControlNormal)
            setBackgroundResource(R.drawable.profile_settings_fragment_spinner_item_background)
        }
    }

    private fun updateStopServiceDropDownView(textView: TextView) {
        textView.apply {
            setText(GestureAction.STOP_SERVICE.titleId)
            setTextColorAttr(appcompatAttr.colorError)
            setSingleLine()
            ellipsize = TextUtils.TruncateAt.END
            setCompoundDrawablesRelativeWithIntrinsicBounds(GestureAction.STOP_SERVICE.iconId, 0, 0, 0)
            compoundDrawablePadding = 12f.dpToPxRound
            setCompoundDrawableTintListAttr(appcompatAttr.colorError)
            background = null
        }
    }
}
