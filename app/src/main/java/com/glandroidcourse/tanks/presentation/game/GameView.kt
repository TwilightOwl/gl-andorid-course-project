package com.glandroidcourse.tanks.presentation.game

import android.R
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.glandroidcourse.tanks.game.engine.*
import com.glandroidcourse.tanks.game.engine.map.*
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
        return
//        drawingJob = GlobalScope.launch {
//            // While this coroutine is running
//            while(isActive) {
//                // canvas for double buffering
//                val canvas = holder.lockCanvas()?: continue
//
//                try {
//                    synchronized(holder) {
//                        // drawing logic
//                        drawingLogic(canvas)
//                    }
//                } finally {
//                    // print to screen
//                    holder.unlockCanvasAndPost(canvas)
//                }
//            }
//        }
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

//    private fun drawingLogic(canvas: Canvas) {
//        // clear canvas
//        canvas.drawColor(presenter.color)
//
//        val time = System.currentTimeMillis()
//
//        // generate raindrops
//        repeat(Random.nextInt(maximumNewRaindrops + 1)) {
//            newRainDrop(canvas.width, time)
//        }
//
//        // color of the raindrops
//        val p = Paint()
//        p.color = Color.WHITE
//
//        // draw raindrops
//        val terminatingDrops = mutableListOf<Pair<Int, Long>>()
//        raindrops.forEach { drop ->
//            // calculate position
//            val x = drop.first.toFloat()
//            val y = ((time - drop.second) / 1000f * raindropSpeed)
//
//            if(y > canvas.height) {
//                // drops out of the screen
//                terminatingDrops.add(drop)
//            } else {
//                canvas.drawRect(x, y-tailLength, x+1, y, p)
//            }
//        }
//
//        // remove drops out of the screen
//        raindrops.removeAll(terminatingDrops)
//    }

    fun drawState(canvas: Canvas, state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) {
        canvas.drawColor(Color.BLACK)
        for ((objectName, objects) in state) {
            when (objectName) {
                GameObjectName.PLAYER -> {
                    for ((obj, position) in objects) {
                        val player = obj as IPlayer
                        drawTank(canvas, position, player.direction, player.isDead(), player.life)
                    }
                }
                GameObjectName.BULLET -> {
                    for ((obj, position) in objects) {
                        val bullet = obj as IBullet
                        drawBullet(canvas, position, bullet.direction, bullet.bulletType)
                    }
                }
                GameObjectName.WALL -> {
                    for ((obj, position) in objects) {
                        val wall = obj as IWall
                        drawWall(canvas, position, wall.life, wall.wallType)
                    }
                }
                GameObjectName.BONUS -> {
                    for ((obj, position) in objects) {
                        val bonus = obj as IBonus
                        drawBonus(canvas, position, bonus.bonusType)
                    }
                }
            }
        }
    }

    fun onStateChanged(state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) {
        drawingJob = GlobalScope.launch {
            val canvas = holder.lockCanvas()
            // canvas.scale(1f, -1f, width / 2f, height / 2f)
            if (canvas != null) {
                try {
                    synchronized(holder) {
                        // drawing logic
                        drawState(canvas, state)
                    }
                } finally {
                    // print to screen
                    holder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }

    private val coef = 9

    private fun drawTank(canvas: Canvas, position: Position, direction: Direction, isDead: Boolean, life: Int) {
        val (top, bottom, left, right) = position
        val p = Paint()
        //val xCenter = (right - left) * 0.5
        //val yCenter = (top - bottom) * 0.5
        p.color = if (isDead) Color.GRAY else if (life == 1) Color.YELLOW else Color.GREEN
        canvas.drawRect(
            coef * cellLeft(left.toFloat()),
            coef * cellTop(top.toFloat()),
            coef * cellRight(right.toFloat()),
            coef * cellBottom(bottom.toFloat()),
            p
        )

        p.color = Color.WHITE
        p.textSize = 30f
        canvas.drawText("$life", coef * left.toFloat(), coef * top.toFloat(), p)
    }

    fun cellLeft(x: Float, extra: Float = 0f): Float = x - 0.5f - extra
    fun cellBottom(x: Float, extra: Float = 0f): Float = x - 0.5f - extra
    fun cellTop(x: Float, extra: Float = 0f): Float = x + 0.5f + extra
    fun cellRight(x: Float, extra: Float = 0f): Float = x + 0.5f + extra


    private fun drawBullet(canvas: Canvas, position: Position, direction: Direction, type: BulletType) {

        val (top, bottom, left, right) = position
        val p = Paint()
        //val xCenter = (right - left) * 0.5
        //val yCenter = (top - bottom) * 0.5
        p.color = if (type.power == 1) Color.RED else Color.MAGENTA
        val extra = if (type.power == 1) 0f else 0.7f
        canvas.drawRect(
            coef * cellLeft(left.toFloat(), extra),
            coef * cellTop(top.toFloat(), extra),
            coef * cellRight(right.toFloat(), extra),
            coef * cellBottom(bottom.toFloat(), extra),
            p
        )



        p.color = Color.WHITE
        p.textSize = 30f
        canvas.drawText("${type.power}", coef * left.toFloat(), coef * top.toFloat(), p)
    }

    private fun drawWall(canvas: Canvas, position: Position, life: Int, wallType: WallType) {

        val (top, bottom, left, right) = position
        val p = Paint()
        p.color = when (wallType) {
            WallType.SOLID -> Color.LTGRAY
            WallType.DESTROYABLE -> Color.rgb(190, 50, 0)
            WallType.STRONG -> if (life == 1) Color.rgb(190, 50, 0) else Color.rgb(110, 30, 0)
        }

        canvas.drawRect(
            coef * cellLeft(left.toFloat()),
            coef * cellTop(top.toFloat()),
            coef * cellRight(right.toFloat()),
            coef * cellBottom(bottom.toFloat()),
            p
        )

//        val res = resources
//        val bitmap = BitmapFactory.decodeResource(res, R.drawable.pic)
//        canvas.drawBitmap(bitmap, 0f, 0f, p)
    }

    private fun drawBonus(canvas: Canvas, position: Position, type: BonusType) {

        val (top, bottom, left, right) = position
        val p = Paint()
        // type is BonusType
        p.color = when (type) {
            BonusType.LIFE_EXTRA -> Color.RED
            BonusType.WEAPON_HEAVY -> Color.WHITE
            BonusType.WEAPON_FAST -> Color.CYAN
            BonusType.SPEED_FAST -> Color.MAGENTA
        }
        canvas.drawRect(
            coef * cellLeft(left.toFloat()),
            coef * cellTop(top.toFloat()),
            coef * cellRight(right.toFloat()),
            coef * cellBottom(bottom.toFloat()),
            p
        )
    }
}