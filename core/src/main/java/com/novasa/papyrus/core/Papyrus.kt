package com.novasa.papyrus.core

import android.view.View
import android.view.animation.Interpolator
import androidx.recyclerview.widget.RecyclerView
import com.novasa.papyrus.core.delegate.ListeningScrollViewDelegate
import com.novasa.papyrus.core.delegate.RecyclerViewScrollDelegate
import com.novasa.papyrus.core.delegate.ScrollDelegate
import com.novasa.papyrus.core.event.ViewEvent
import com.novasa.papyrus.core.util.MathHelper
import com.novasa.papyrus.core.widget.ListeningScrollView

import java.util.ArrayList

/**
 * Created by mikkelschlager on 02/03/2017.
 */

@Suppress("unused")
class Papyrus : ScrollDelegate.Callback {

    enum class Orientation {
        VERTICAL,
        HORIZONTAL
    }

    private var delegate: ScrollDelegate

    private var recyclerViewOnScrollListener: RecyclerView.OnScrollListener? = null

    private var orientation: Orientation? = null

    private val callbacks = ArrayList<Delegate>()
    private val snaps = ArrayList<Snap>()

    var isEnabled = true
        set(enabled) {
            field = enabled
            if (isEnabled) {
                // Trigger update
                onScrolled(0, 0)
            }
        }

    private fun isScrolledToBottom(): Boolean = delegate.isScrolledToBottom()

    constructor(recyclerView: RecyclerView) {
        delegate = RecyclerViewScrollDelegate(recyclerView)
        delegate.callback = this
    }

    constructor(scrollView: ListeningScrollView) {
        delegate = ListeningScrollViewDelegate(scrollView)
        delegate.callback = this
    }

    private fun resolveOrientation() {
        orientation = delegate.getOrientation()
    }

    fun snap(threshold: Float): Snap {
        val s = Snap(threshold)
        snaps.add(s)

        return s
    }

    fun addDelegate(p0: Int, p1: Int): Delegate {
        val delegate = Delegate(p0.toFloat(), p1.toFloat())
        callbacks.add(delegate)
        return delegate
    }

    fun refresh() {
        delegate.poke()
    }

    override fun onScrolled(p: Int, pp: Int) {
        if (orientation == null) {
            resolveOrientation()
        }

        if (!isEnabled) {
            return
        }

        for (callback in callbacks) {
            callback.onScrolled(this, p.toFloat(), pp.toFloat())
        }
    }

    override fun onScrollEnded(p: Int) {
        for (s in snaps) {
            if (s.snap(p)) {
                break
            }
        }
    }

    private fun snapTo(p: Int) {
        if (isScrolledToBottom()) {
            return
        }

        delegate.snapTo(p)
    }

    class Delegate(val rangeP0: Float, val rangeP1: Float) {

        private val events = ArrayList<Event>()

        var factor = 1f
        var interpolator: Interpolator? = null
        var resetOnBackScroll = false

        internal fun onScrolled(papyrus: Papyrus, p: Float, pp: Float) {

            if (rangeP0 == rangeP1) {
                // Range is 0, we can't get an actual value
                return
            }

            if (factor != 0f) {
                var v = p
                // Check if the scroll goes outside the lower scroll limit.
                if (p < rangeP0) {
                    // If it does, and it did last frame as well, no change is needed, bail out.
                    if (pp < rangeP0 && pp != Integer.MIN_VALUE.toFloat()) {
                        return
                    }

                    // Clamp to limit
                    v = rangeP0

                    // Same for upper limit
                } else if (p > rangeP1 && pp != Integer.MIN_VALUE.toFloat()) {
                    if (pp > rangeP1) {
                        return
                    }

                    v = rangeP1
                }

                // Normalized value
                var n = MathHelper.minMaxNormalize(v, rangeP0, rangeP1)

                if (interpolator != null) {
                    n = interpolator!!.getInterpolation(n)
                    v = n * (rangeP1 - rangeP0)
                }

                n *= factor
                v *= factor

                events.forEach {
                    it.update(papyrus, p, v, n)
                }
            }
        }

        fun addEvent(event: Event): Delegate {
            events.add(event)
            return this
        }

        /**
         * Set the factor that the view will scroll relative to the scroll event. 1 = follow the scroll, 0 = no scroll.
         * Negative values will scroll opposite the scroll event.
         */
        fun factor(factor: Float): Delegate {
            this.factor = factor
            return this
        }

        fun interp(interp: Interpolator): Delegate {
            this.interpolator = interp
            return this
        }

        fun resetOnBackScroll(): Delegate {
            this.resetOnBackScroll = true
            return this
        }
    }

    interface Event {
        fun update(papyrus: Papyrus, p: Float, v: Float, vn: Float)
    }


    inner class Snap(private val threshold: Float) {
        private var v0: Float = 0.toFloat()
        private var v1: Float = 0.toFloat()

        init {
            v0 = 0f
            v1 = threshold * 2f
        }

        fun below(below: Float): Snap {
            v0 = below
            return this
        }

        fun above(above: Float): Snap {
            v1 = above
            return this
        }

        fun snap(v: Int): Boolean {
            if (v >= v0 && v <= v1) {
                if (v < threshold) {
                    snapTo(Math.round(v0))
                } else {
                    snapTo(Math.round(v1))
                }
                return true
            }

            return false
        }
    }
}
