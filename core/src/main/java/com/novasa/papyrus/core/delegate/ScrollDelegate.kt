package com.novasa.papyrus.core.delegate

import com.novasa.papyrus.core.Papyrus

interface ScrollDelegate {

    interface Callback {
        fun onScrolled(p: Int, pp: Int)
        fun onScrollEnded(p: Int)
    }

    var callback: Callback

    fun poke()
    fun getOrientation(): Papyrus.Orientation
    fun isScrolledToBottom(): Boolean
    fun snapTo(p: Int)
}