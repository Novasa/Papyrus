package com.novasa.papyrus.core.event

import android.view.View
import androidx.core.view.doOnLayout
import com.novasa.papyrus.core.Papyrus

abstract class ViewEvent(private val view: View): Papyrus.Event {

    private var didInit = false
    private var goneAboveP1 = false

    init {
        view.doOnLayout {
            didInit = true
            onInit(view)
        }
    }

    fun goneAboveP1(): ViewEvent {
        goneAboveP1 = true
        return this
    }

    open fun onInit(view: View) {

    }

    override fun update(papyrus: Papyrus, p: Float, v: Float, vn: Float) {
        update(papyrus, view, p, v, vn)
    }

    abstract fun update(papyrus: Papyrus, view: View, p: Float, v: Float, vn: Float)
}