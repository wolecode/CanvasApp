package com.example.canvaapp

import android.content.Context
import android.graphics.*
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat

class CanvasView(context: Context) : View(context) {
    private lateinit var externalCanvas: Canvas
    private lateinit var externalBitmap: Bitmap
    private lateinit var outerRect: Rect
    private lateinit var innerRect: Rect
    private var tolerance = ViewConfiguration.get(context).scaledTouchSlop
    private val path = Path()
    private var initialXCord = 0.0f
    private var initialYCord = 0.0f
    private var currentXCord = 0.0f
    private var currentYCord = 0.0f
    private val bgColor = ResourcesCompat.getColor(resources, R.color.white, null)
    private val strokeColors = ResourcesCompat.getColor(resources, R.color.black, null)
    private val paint = Paint().apply{
        color = strokeColors
        isAntiAlias = true
        strokeCap = Paint.Cap.BUTT
        strokeJoin = Paint.Join.ROUND
        strokeWidth = 12f
        style = Paint.Style.STROKE

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(::externalBitmap.isInitialized) externalBitmap.recycle()
        externalBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        externalCanvas = Canvas(externalBitmap)
        externalCanvas.drawColor(bgColor)

        outerRect = Rect(50, 50, w - 50, h - 50)
        innerRect = Rect(100, 100, w - 100, h - 100)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(externalBitmap, 0f, 0f, null)
        canvas?.drawRect(outerRect, paint)
        canvas?.drawRect(innerRect, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if(event != null) {
            currentXCord = event.x
            currentYCord = event.y
        }

        when(event?.action) {
            MotionEvent.ACTION_DOWN -> actionDown()
            MotionEvent.ACTION_MOVE -> actionMove()
            MotionEvent.ACTION_UP -> actionUp()
        }
        return true
    }

    private fun actionUp() {

        path.reset()
    }

    private fun actionMove() {
        val dx = Math.abs(currentXCord - initialXCord)
        val dy = Math.abs(currentYCord - initialYCord)

        if (dx >= tolerance || dy >= tolerance) {
            path.quadTo(initialXCord, initialYCord, currentXCord, currentYCord)
            externalCanvas.drawPath(path, paint)
            initialXCord = currentXCord
            initialYCord = currentYCord
        }
        invalidate()
    }

    private fun actionDown() {
        path.reset()
        path.moveTo(currentXCord, currentYCord)
        initialXCord = currentXCord
        initialYCord = currentYCord
    }
}