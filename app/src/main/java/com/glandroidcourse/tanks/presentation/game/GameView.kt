package com.glandroidcourse.tanks.presentation.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.random.Random

class GameView(context: Context, val presenter: GamePresenter, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

//    @Inject
//    @InjectPresenter
//    lateinit var presenter: GamePresenter
//
//    @ProvidePresenter
//    fun providePresenter() = presenter
//
//    constructor(context: Context): this(context, null, 0)
//    constructor(context: Context, attrs: AttributeSet): this(context, attrs, 0)

    private var drawingJob: Job? = null

    init {
        holder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) { }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        runBlocking {
            drawingJob?.cancelAndJoin()
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        drawingJob = GlobalScope.launch {
            // While this coroutine is running
            while(isActive) {
                // canvas for double buffering
                val canvas = holder.lockCanvas()?: continue

                try {
                    synchronized(holder) {
                        // drawing logic
                        drawingLogic(canvas)
                    }
                } finally {
                    // print to screen
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private val raindrops = mutableListOf<Pair<Int, Long>>()

    // add new raindrop to the list
    private fun newRainDrop(width: Int, time: Long) {
        val x = Random.nextInt(width+1)
        raindrops.add(x to time)
    }

    // maximum number of raindrops that can be generated at the same time
    private val maximumNewRaindrops = 5
    private val raindropSpeed = 10
    private val tailLength = 120

    private fun drawingLogic(canvas: Canvas) {
        // clear canvas
        canvas.drawColor(presenter.color)

        val time = System.currentTimeMillis()

        // generate raindrops
        repeat(Random.nextInt(maximumNewRaindrops + 1)) {
            newRainDrop(canvas.width, time)
        }

        // color of the raindrops
        val p = Paint()
        p.color = Color.WHITE

        // draw raindrops
        val terminatingDrops = mutableListOf<Pair<Int, Long>>()
        raindrops.forEach { drop ->
            // calculate position
            val x = drop.first.toFloat()
            val y = ((time - drop.second) / 1000f * raindropSpeed)

            if(y > canvas.height) {
                // drops out of the screen
                terminatingDrops.add(drop)
            } else {
                canvas.drawRect(x, y-tailLength, x+1, y, p)
            }
        }

        // remove drops out of the screen
        raindrops.removeAll(terminatingDrops)
    }
}