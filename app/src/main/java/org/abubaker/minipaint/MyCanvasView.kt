package org.abubaker.minipaint

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

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

    // For caching the x and y coordinates of the current touch event (the MotionEvent coordinates)
    private var motionTouchEventX = 0f
    private var motionTouchEventY = 0f

    // To cache the latest x and y values
    // After the user stops moving and lifts their touch, these are the starting point for the next path
    private var currentX = 0f
    private var currentY = 0f

    //
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    //
    private lateinit var frame: Rect

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

    /**
     * Path - To store the path that is being drawn when following the user's touch on the screen
     */
    private var path = Path()

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        // This will avoid memory leak
        if (::extraBitmap.isInitialized) extraBitmap.recycle()

        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Create a Canvas instance from extraBitmap and assign it to extraCanvas.
        extraCanvas = Canvas(extraBitmap)

        // Specify the background color in which to fill extraCanvas.
        extraCanvas.drawColor(backgroundColor)

        // Calculate a rectangular frame around the picture.
        val inset = 40
        frame = Rect(inset, inset, width - inset, height - inset)

    }

    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        // The 2D coordinate system used for drawing on a Canvas is in pixels,
        // and the origin (0,0) is at the top left corner of the Canvas
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        // Draw a frame around the canvas.
        canvas.drawRect(frame, paint)

    }

    /**
     * Touch Events
     */

    override fun onTouchEvent(event: MotionEvent): Boolean {

        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {

            // Movement Direction: Down
            MotionEvent.ACTION_DOWN -> touchStart()

            // Movement Direction: Move
            MotionEvent.ACTION_MOVE -> touchMove()

            // Movement Direction: Up
            MotionEvent.ACTION_UP -> touchUp()

        }

        return true
    }

    private fun touchUp() {

        // Reset the path so it doesn't get drawn again.
        path.reset()

    }

    private fun touchMove() {
        /**
         * 1. Calculate the distance that has been moved (dx, dy).
         * 2. If the movement was further than the touch tolerance, add a segment to the path.
         * 3. Set the starting point for the next segment to the endpoint of this segment.
         * 4. Using quadTo() instead of lineTo() create a smoothly drawn line without corners. See Bezier Curves.
         * 5. Call invalidate() to (eventually call onDraw() and) redraw the view.
         */

        // Calculate the traveled distance (dx, dy)
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)

        if (dx >= touchTolerance || dy >= touchTolerance) {

            // Create a curve between the two points and store it in path
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )

            // Update the running currentX and currentY tally
            currentX = motionTouchEventX
            currentY = motionTouchEventY

            // Draw the path in the extra bitmap to cache it.
            extraCanvas.drawPath(path, paint)
        }

        // Then call invalidate() to force redrawing of the screen with the updated path
        invalidate()

    }

    private fun touchStart() {

        // Reset the path so it doesn't get drawn again.
        path.reset()

        //  ove to the x-y coordinates of the touch event (motionTouchEventX and motionTouchEventY
        path.moveTo(motionTouchEventX, motionTouchEventY)

        // Assign currentX and currentY to that value
        currentX = motionTouchEventX
        currentY = motionTouchEventY

    }

}