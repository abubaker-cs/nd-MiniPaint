package org.abubaker.minipaint

import android.os.Build
import android.os.Bundle
import android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //
        val myCanvasView = MyCanvasView(this)

        // request the full screen for the layout of myCanvasView
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            myCanvasView.systemUiVisibility = SYSTEM_UI_FLAG_FULLSCREEN
            // https://stackoverflow.com/questions/62577645/android-view-view-systemuivisibility-deprecated-what-is-the-replacement
            // window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }

        // Content Description
        myCanvasView.contentDescription = getString(R.string.canvasContentDescription)

        // Set Content View
        setContentView(myCanvasView)

    }
}