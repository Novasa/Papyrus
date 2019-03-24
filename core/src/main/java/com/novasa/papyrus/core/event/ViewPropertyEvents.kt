package com.novasa.papyrus.core.event

import android.view.View
import com.novasa.papyrus.core.Papyrus

class Scale(view: View) : ViewEventFloatRange(view) {

    override fun update(papyrus: Papyrus, view: View, v: Float) {
        view.scaleX = v
        view.scaleY = v
    }
}

class Alpha(view: View) : ViewEventFloatRange(view) {

    override fun update(papyrus: Papyrus, view: View, v: Float) {
        view.alpha = v
    }
}

class X(view: View) : ViewEventFloatRange(view) {

    override fun update(papyrus: Papyrus, view: View, v: Float) {
        view.x = v
    }
}

class Y(view: View) : ViewEventFloatRange(view) {

    override fun update(papyrus: Papyrus, view: View, v: Float) {
        view.y = v
    }
}