package com.novasa.papyrus.core.delegate

import android.view.animation.DecelerateInterpolator
import android.view.animation.Interpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.novasa.papyrus.core.Papyrus

class RecyclerViewScrollDelegate(private val recyclerView: RecyclerView) : ScrollDelegate {

    override lateinit var callback: ScrollDelegate.Callback

    private var pp = Integer.MIN_VALUE

    private lateinit var orientationHelper: OrientationHelper
    private val snapInterpolator: Interpolator by lazy {
        DecelerateInterpolator()
    }

    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                onScrolled()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                onScrollStateChanged(newState)
            }
        })
    }

    private fun onScrolled() {
        if (!::callback.isInitialized) {
            return
        }

        val p = if (recyclerView.childCount == 0) 0 else {

            val orientation = getOrientation()
            val firstChild = recyclerView.getChildAt(0)
            val firstShowing = recyclerView.getChildAdapterPosition(firstChild) == 0
            val decoratedStart = getOrientationHelper(recyclerView).getDecoratedStart(firstChild)

            when (orientation) {
                Papyrus.Orientation.VERTICAL -> {
                    if (firstShowing) {
                        recyclerView.paddingTop - decoratedStart
                    } else {
                        Integer.MAX_VALUE
                    }
                }
                Papyrus.Orientation.HORIZONTAL -> {
                    if (firstShowing) {
                        recyclerView.paddingLeft - decoratedStart
                    } else {
                        Integer.MAX_VALUE
                    }
                }
            }
        }

        callback.onScrolled(p, pp)
        pp = p
    }

    private fun onScrollStateChanged(state: Int) {
        if (!::callback.isInitialized) {
            return
        }

        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            callback.onScrollEnded(pp)
        }
    }

    override fun poke() {
        onScrolled()
    }

    override fun getOrientation(): Papyrus.Orientation {
        val layoutManager = recyclerView.layoutManager
        if (layoutManager != null) {
            if (layoutManager is LinearLayoutManager) {
                return when (layoutManager.orientation) {
                    LinearLayoutManager.VERTICAL -> Papyrus.Orientation.VERTICAL
                    LinearLayoutManager.HORIZONTAL -> Papyrus.Orientation.HORIZONTAL
                    else -> Papyrus.Orientation.VERTICAL
                }
            }
        }
        return Papyrus.Orientation.VERTICAL
    }

    override fun isScrolledToBottom(): Boolean {
        val viewCount = recyclerView.childCount

        if (viewCount == 0) {
            return true
        }

        val lastChild = recyclerView.getChildAt(viewCount - 1)
        val pos = recyclerView.getChildAdapterPosition(lastChild)
        val count = recyclerView.layoutManager?.itemCount ?: 0

        return if (pos == count - 1) {
            val orientationHelper = getOrientationHelper(recyclerView)
            recyclerView.height - orientationHelper.getDecoratedEnd(lastChild) - recyclerView.paddingBottom == 0

        } else {
            false
        }
    }

    override fun snapTo(p: Int) {
        recyclerView.smoothScrollBy(0, p - p, snapInterpolator)
    }

    private fun getOrientationHelper(recyclerView: RecyclerView): OrientationHelper {
        if (!::orientationHelper.isInitialized) {
            orientationHelper = OrientationHelper.createVerticalHelper(recyclerView.layoutManager)
        }
        return orientationHelper
    }
}