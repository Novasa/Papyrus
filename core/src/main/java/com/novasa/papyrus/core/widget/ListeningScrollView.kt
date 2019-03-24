package com.novasa.papyrus.core.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

import java.util.HashSet

/**
 * Created by mikkelschlager on 09/09/16.
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class ListeningScrollView : ScrollView {

    companion object {
        const val SCROLL_STATE_IDLE = 0
        const val SCROLL_STATE_TOUCH_STARTED = 1
        const val SCROLL_STATE_SCROLLING = 2
        const val SCROLL_STATE_TOUCH_ENDED = 3
        const val SCROLL_STATE_FLINGING = 4
        const val SCROLL_STATE_SETTLING = 5

        private const val SETTLING_DELAY = 100
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val onScrollListeners = HashSet<OnScrollListener>()
    private val onScrollStateListeners = HashSet<OnScrollStateListener>()

    var scrolledX: Int = 0
        private set

    var scrolledY: Int = 0
        private set

    var scrollState = SCROLL_STATE_IDLE
        private set

    private val settlingRunnable = Runnable {
        setState(SCROLL_STATE_SETTLING)
        post(idleRunnable)
    }

    private val idleRunnable = Runnable { setState(SCROLL_STATE_IDLE) }

    interface OnScrollListener {
        fun onScroll(scrollView: ListeningScrollView, x: Int, dx: Int, y: Int, dy: Int)
    }

    interface OnScrollStateListener {
        fun onScrollStateChanged(state: Int)
    }

    fun addOnScrollListener(listener: OnScrollListener) {
        onScrollListeners.add(listener)
    }

    fun removeOnScrollListener(listener: OnScrollListener) {
        onScrollListeners.remove(listener)
    }

    private fun notifyScrollChanged(x: Int, dx: Int, y: Int, dy: Int) {
        for (listener in onScrollListeners) {
            listener.onScroll(this, x, dx, y, dy)
        }
    }

    fun addOnScrollStateListener(listener: OnScrollStateListener) {
        onScrollStateListeners.add(listener)
    }

    fun removeOnScrollStateListener(listener: OnScrollStateListener) {
        onScrollStateListeners.remove(listener)
    }

    private fun notifyScrollStateChanged(state: Int) {
        for (listener in onScrollStateListeners) {
            listener.onScrollStateChanged(state)
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)

        val dx = l - scrolledX
        val dy = t - scrolledY

        notifyScrollChanged(l, dx, t, dy)

        scrolledX = l
        scrolledY = t

        when (scrollState) {
            SCROLL_STATE_TOUCH_ENDED -> {
                setState(SCROLL_STATE_FLINGING)
                restartSettlingDelay()
            }
            SCROLL_STATE_FLINGING -> restartSettlingDelay()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {

        var state = scrollState
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                removeCallbacks(settlingRunnable)
                state = SCROLL_STATE_TOUCH_STARTED
            }
            MotionEvent.ACTION_MOVE -> state = SCROLL_STATE_SCROLLING
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                state = SCROLL_STATE_TOUCH_ENDED
                restartSettlingDelay()
            }
        }

        setState(state)
        return super.onTouchEvent(ev)
    }

    private fun setState(state: Int) {
        if (state != scrollState) {
            notifyScrollStateChanged(state)
            scrollState = state
        }
    }

    private fun restartSettlingDelay() {
        removeCallbacks(settlingRunnable)
        postDelayed(settlingRunnable, SETTLING_DELAY.toLong())
    }
}
