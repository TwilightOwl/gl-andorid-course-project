package com.glandroidcourse.tanks.presentation.game

import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.Window


class TestActivity : Activity() {
    private val paint =
        Paint(Paint.ANTI_ALIAS_FLAG)
    private var initX = 0f
    private var initY = 0f
    private var radius = 0f
    private var drawing = false
    public override fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(MySurfaceView(this))
    }

    inner class MySurfaceThread(
        private val myThreadSurfaceHolder: SurfaceHolder,
        private val myThreadSurfaceView: MySurfaceView
    ) : Thread() {
        private var myThreadRun = false
        fun setRunning(b: Boolean) {
            myThreadRun = b
        }

        override fun run() { // super.run();
            while (myThreadRun) {
                var c: Canvas? = null
                try {
                    c = myThreadSurfaceHolder.lockCanvas(null)
                    synchronized(myThreadSurfaceHolder) { myThreadSurfaceView.onDraw(c) }
                } finally { // do this in a finally so that if an exception is thrown
// during the above, we don't leave the Surface in an
// inconsistent state
                    if (c != null) {
                        myThreadSurfaceHolder.unlockCanvasAndPost(c)
                    }
                }
            }
        }

    }

    inner class MySurfaceView : SurfaceView, SurfaceHolder.Callback {
        private var thread: MySurfaceThread? = null
        public override fun onDraw(canvas: Canvas) { // super.onDraw(canvas);
            if (drawing) {
                canvas.drawCircle(initX, initY, radius, paint)
            }
        }

        override fun onTouchEvent(event: MotionEvent): Boolean { // return super.onTouchEvent(event);
            val action = event.action
            if (action == MotionEvent.ACTION_MOVE) {
                val x = event.x
                val y = event.y
                radius = Math.sqrt(
                    Math.pow(x - initX.toDouble(), 2.0)
                            + Math.pow(y - initY.toDouble(), 2.0)
                ).toFloat()
            } else if (action == MotionEvent.ACTION_DOWN) {
                initX = event.x
                initY = event.y
                radius = 1f
                drawing = true
            } else if (action == MotionEvent.ACTION_UP) {
                drawing = false
            }
            return true
        }

        constructor(context: Context?) : super(context) {
            init()
        }

        constructor(context: Context?, attrs: AttributeSet?) : super(
            context,
            attrs
        ) {
            init()
        }

        constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyle: Int
        ) : super(context, attrs, defStyle) {
            init()
        }

        private fun init() {
            holder.addCallback(this)
            isFocusable = true // make sure we get key events
            paint.style = Paint.Style.STROKE
            paint.strokeWidth = 3f
            paint.color = Color.WHITE
        }

        override fun surfaceChanged(
            arg0: SurfaceHolder, arg1: Int, arg2: Int,
            arg3: Int
        ) {
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            thread = MySurfaceThread(getHolder(), this)
            thread!!.setRunning(true)
            thread!!.start()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            var retry = true
            thread!!.setRunning(false)
            while (retry) {
                try {
                    thread!!.join()
                    retry = false
                } catch (e: InterruptedException) {
                }
            }
        }
    }
}