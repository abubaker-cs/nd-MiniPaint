package org.abubaker.minipaint

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.content.res.ResourcesCompat

// has to be float
private const val STROKE_WIDTH = 12f

class MyCanvasView(context: Context) : View(context) {

    // These are your bitmap and canvas for caching what has been drawn before
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    // Define a class level variable backgroundColor, for the background color of the canvas and
    // initialize it to the colorBackground you defined earlier.
    private val backgroundColor = ResourcesCompat.getColor(resources, R.color.colorBackground, null)

    // For holding the color to draw with and initialize it with the colorPaint resource
    private val drawColor = ResourcesCompat.getColor(resources, R.color.colorPaint, null)

    // Set up the paint with which to draw.
    private val paint = Paint().apply {

        color = drawColor

        // Edge Smoothing - Smooths out edges of what is drawn without affecting shape.
        isAntiAlias = true

        // Dithering affects how colors with higher-precision than the device are down-sampled.
        isDither = true

        // Stroke which essentially is aligned - default: FILL / (STROKE or BOTH)
        style = Paint.Style.STROKE

        // How stroke path will be joined - default: MITER
        strokeJoin = Paint.Join.ROUND

        // Shape of the END of the line - default: BUTT
        strokeCap = Paint.Cap.ROUND

        // Width of stroke in px - default: Hairline-width (really thin)
        strokeWidth = STROKE_WIDTH

    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        // This will avoid memory leak
        if (::extraBitmap.isInitialized) extraBitmap.recycle()

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Create a Canvas instance from extraBitmap and assign it to extraCanvas.
        extraCanvas = Canvas(extraBitmap)

        // Specify the background color in which to fill extraCanvas.
        extraCanvas.drawColor(backgroundColor)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)
    }

}