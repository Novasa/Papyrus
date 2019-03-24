package com.novasa.papyrus.core.event

import android.view.View
import com.novasa.papyrus.core.Papyrus
import com.novasa.papyrus.core.util.MathHelper

abstract class ViewEventFloatRange(view: View) : ViewEvent(view) {

    var v0: Float = 0f
    var v1: Float = 0f

    fun range(v0: Float, v1: Float): ViewEventFloatRange {
        this.v0 = v0
        this.v1 = v1
        return this
    }

    override fun update(papyrus: Papyrus, view: View, p: Float, v: Float, vn: Float) {
        update(papyrus, view, MathHelper.minMaxDenormalize(vn, v0, v1))
    }

    abstract fun update(papyrus: Papyrus, view: View, v: Float)
}
