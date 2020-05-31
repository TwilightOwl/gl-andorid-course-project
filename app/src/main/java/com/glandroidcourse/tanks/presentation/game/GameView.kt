package com.glandroidcourse.tanks.presentation.game

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.glandroidcourse.tanks.R
import com.glandroidcourse.tanks.game.engine.*
import com.glandroidcourse.tanks.game.engine.map.*
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.random.Random

class GameView(context: Context, val presenter: GamePresenter, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private val coef = 9

    fun cellLeft(x: Float, extra: Float = 0f): Float = x - 0.5f - extra
    fun cellBottom(x: Float, extra: Float = 0f): Float = x - 0.5f - extra
    fun cellTop(x: Float, extra: Float = 0f): Float = x + 0.5f + extra
    fun cellRight(x: Float, extra: Float = 0f): Float = x + 0.5f + extra

    val blockSize = BLOCK_SIZE * coef
    val tankLength = (BLOCK_SIZE + 1) * coef
    val tankWidth = BLOCK_SIZE * coef
    val bulletSize = 1 * coef
    val bulletHeavySize = 2 * coef

    val bitmapBricks = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bricks), blockSize, blockSize, false)
    val bitmapBricksDamaged = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bricks_damaged), blockSize, blockSize, false)
    val bitmapBricksSolid = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bricks_solid), blockSize, blockSize, false)
    val bitmapBullet = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bullet), bulletSize, bulletSize, false)
    val bitmapBulletHeavy = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bullet_heavy), bulletHeavySize, bulletHeavySize, false)
    val bitmapBonusSpeed1 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bonus_speed_1), blockSize, blockSize, false)
    val bitmapBonusSpeed2 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bonus_speed_2), blockSize, blockSize, false)
    val bitmapBonusSpeed3 = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bonus_speed_3), blockSize, blockSize, false)
    val bitmapBonusLife = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bonus_heart_2), blockSize, blockSize, false)
    val bitmapBonusWeaponHeavy = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.bonus_weapon_heavy), blockSize, blockSize, false)

    val bitmapTankWhite = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.tank_white), tankWidth, tankLength, false)
    val bitmapTankYellow = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.tank_yellow), tankWidth, tankLength, false)
    val bitmapTankGreen= Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.tank_green), tankWidth, tankLength, false)
    val bitmapTankRed = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.tank_red), tankWidth, tankLength, false)
    val bitmapTankDead = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.tank_dead), tankWidth, tankLength, false)

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
    }


    fun drawState(canvas: Canvas, state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) {
        canvas.drawColor(Color.BLACK)
        for ((objectName, objects) in state) {
            when (objectName) {
                GameObjectName.PLAYER -> {
                    for ((obj, position) in objects) {
                        val player = obj as IPlayer
                        drawTank(canvas, position, player.direction, player.isDead(), player.life, player.id)
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


    private fun drawTank(canvas: Canvas, position: Position, direction: Direction, isDead: Boolean, life: Int, id: Int) {
        val (top, bottom, left, right) = position
        val p = Paint()

        p.color = if (isDead) Color.GRAY else if (life == 1) Color.YELLOW else Color.GREEN
        canvas.drawRect(
            coef * cellLeft(left.toFloat()),
            coef * cellTop(top.toFloat()),
            coef * cellRight(right.toFloat()),
            coef * cellBottom(bottom.toFloat()),
            p
        )

        val bitmap = if (isDead) bitmapTankDead else when (id) {
            0 -> bitmapTankWhite
            1 -> bitmapTankGreen
            2 -> bitmapTankRed
            3 -> bitmapTankYellow
            else -> bitmapTankYellow
        }

        val error = coef / 2f
        val (dx, dy) = when (direction) {
            Direction.LEFT, Direction.RIGHT -> Pair(tankLength / 2f - error, tankWidth / 2f - error)
            Direction.UP, Direction.DOWN -> Pair(tankWidth / 2f - error, tankLength / 2f - error)
        }
        val degree = when (direction) {
            Direction.UP -> 180f
            Direction.RIGHT -> 90f
            Direction.DOWN -> 0f
            Direction.LEFT -> 270f
        }

        val m = Matrix()
        m.postTranslate(coef * cellLeft(left.toFloat()), coef * cellBottom(bottom.toFloat()))
        m.postRotate(degree, coef * left.toFloat() + dx, coef * bottom.toFloat() + dy)
        when (direction) {
            Direction.RIGHT -> m.postTranslate(coef * 0.5f, coef * 0.5f)
            Direction.LEFT -> m.postTranslate(- coef * 0.5f, - coef * 0.5f)
        }
        canvas.drawBitmap(bitmap, m, p)
    }




    private fun drawBullet(canvas: Canvas, position: Position, direction: Direction, type: BulletType) {

        val (top, bottom, left, right) = position
        val p = Paint()
//        p.color = if (type.power == 1) Color.RED else Color.MAGENTA
//        val extra = if (type.power == 1) 0f else 0.7f
//        canvas.drawRect(
//            coef * cellLeft(left.toFloat(), extra),
//            coef * cellTop(top.toFloat(), extra),
//            coef * cellRight(right.toFloat(), extra),
//            coef * cellBottom(bottom.toFloat(), extra),
//            p
//        )
//
//        p.color = Color.WHITE
//        p.textSize = 30f
//        canvas.drawText("${type.power}", coef * left.toFloat(), coef * top.toFloat(), p)


        val extra = if (type == BulletType.HEAVY) 0.5f else 0f
        val bitmap = when (type) {
            BulletType.HEAVY -> bitmapBulletHeavy
            else -> bitmapBullet
        }
        canvas.drawBitmap(bitmap, coef * cellLeft(left.toFloat(), extra), coef * cellBottom(bottom.toFloat(), extra), p)
    }


    private fun drawWall(canvas: Canvas, position: Position, life: Int, wallType: WallType) {

        println(width)

        val (top, bottom, left, right) = position
        val p = Paint()

        val bitmap = when (wallType) {
            WallType.SOLID -> bitmapBricksSolid
            WallType.DESTROYABLE -> bitmapBricksDamaged
            WallType.STRONG -> if (life == 1) bitmapBricksDamaged else bitmapBricks
        }
        canvas.drawBitmap(bitmap, coef * cellLeft(left.toFloat()), coef * cellBottom(bottom.toFloat()), p)

//        val m = Matrix()
//        m.postTranslate(coef * cellLeft(left.toFloat()), coef * cellBottom(bottom.toFloat()))
//
//        Bitmap.createScaledBitmap(bitmap, blockSize, blockSize, false)


//        canvas.drawBitmap(
//            bitmapBricks
//            m,
//            p
//        )
//        canvas.drawBitmap(
//            bitmap,
//            src = Rect(0, 30,30,0),
//            dst = Rect(0, 30,30,0),
//            p
//        )

    }

    private fun drawBonus(canvas: Canvas, position: Position, type: BonusType) {

        val (top, bottom, left, right) = position
        val p = Paint()
        // type is BonusType
//        p.color = when (type) {
//            BonusType.LIFE_EXTRA -> Color.RED
//            BonusType.WEAPON_HEAVY -> Color.WHITE
//            BonusType.WEAPON_FAST -> Color.CYAN
//            BonusType.SPEED_FAST -> Color.MAGENTA
//        }
//        canvas.drawRect(
//            coef * cellLeft(left.toFloat()),
//            coef * cellTop(top.toFloat()),
//            coef * cellRight(right.toFloat()),
//            coef * cellBottom(bottom.toFloat()),
//            p
//        )
        val bitmap = when (type) {
            BonusType.LIFE_EXTRA -> bitmapBonusLife
            BonusType.WEAPON_HEAVY -> bitmapBonusWeaponHeavy
            BonusType.WEAPON_FAST -> bitmapBonusSpeed2
            BonusType.SPEED_FAST -> bitmapBonusSpeed3
        }
        canvas.drawBitmap(bitmap, coef * cellLeft(left.toFloat()), coef * cellBottom(bottom.toFloat()), p)
    }
}