package com.novasa.papyrus.core.event

import android.view.View
import com.novasa.papyrus.core.Papyrus
import com.novasa.papyrus.core.util.MathHelper
import kotlin.math.roundToInt

abstract class ViewEventIntRange(view: View): ViewEvent(view) {

    var v0: Int = 0
    var v1: Int = 0

    fun range(v0: Int, v1: Int): ViewEventIntRange {
        this.v0 = v0
        this.v1 = v1
        return this
    }

    override fun update(papyrus: Papyrus, view: View, p: Float, v: Float, vn: Float) {
        update(papyrus, view, MathHelper.minMaxDenormalize(vn, v0.toFloat(), v1.toFloat()).roundToInt())
    }

    abstract fun update(papyrus: Papyrus, view: View, v: Int)
}
