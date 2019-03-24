package com.novasa.papyrus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import com.novasa.papyrus.core.Papyrus
import com.novasa.papyrus.core.event.Scale
import com.novasa.papyrus.core.event.Y
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val papyrus = Papyrus(listeningScrollView)

        papyrus.addDelegate(0, resources.getDimensionPixelSize(R.dimen.example_header_height))
                .factor(.5f)
                .interp(DecelerateInterpolator())
                .addEvent(Y(exampleHeaderBackground))
                .addEvent(Scale(exampleHeaderTitle).range(1f, 0.5f))
                .addEvent(Scale(exampleHeaderSubtitle).range(1f, 0f))
    }
}
