package com.novasa.papyrus.core.delegate

import com.novasa.papyrus.core.widget.ListeningScrollView
import com.novasa.papyrus.core.Papyrus

class ListeningScrollViewDelegate(private val scrollView: ListeningScrollView) : ScrollDelegate,
    ListeningScrollView.OnScrollListener,
    ListeningScrollView.OnScrollStateListener {

    override lateinit var callback: ScrollDelegate.Callback

    private var py: Int = Integer.MIN_VALUE

    init {
        scrollView.addOnScrollListener(this)
        scrollView.addOnScrollStateListener(this)
    }

    override fun poke() {
        callback.onScrolled(scrollView.scrolledY, py)
    }

    override fun getOrientation(): Papyrus.Orientation = Papyrus.Orientation.VERTICAL

    override fun isScrolledToBottom(): Boolean = scrollView.run {
        childCount == 0 || getChildAt(0).height - height + paddingTop + paddingBottom - scrolledY == 0
    }

    override fun snapTo(p: Int) {
        scrollView.smoothScrollTo(0, p)
    }

    override fun onScroll(scrollView: ListeningScrollView, x: Int, dx: Int, y: Int, dy: Int) {
        if (::callback.isInitialized) {
            callback.onScrolled(y, py)
        }
        py = y
    }

    override fun onScrollStateChanged(state: Int) {
        if (::callback.isInitialized) {
            when (state) {
                ListeningScrollView.SCROLL_STATE_IDLE -> callback.onScrollEnded(py)
            }
        }
    }
}
