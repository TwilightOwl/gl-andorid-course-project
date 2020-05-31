package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

var id = 0
fun getNextId() = id++

val BLOCK_SIZE = 5  // 3
val BLOCK_Y_COUNT = 24 // 40
val BLOCK_X_COUNT = 33 // 54

class GameLogic {

    val players = mutableListOf<IPlayer>()
    val bullets = mutableListOf<IBullet>()
    val walls = mutableListOf<IWall>()
    val bonuses = mutableListOf<IBonus>()
    val map = Map()
    val MAP_WIDTH = 100
    val MAP_HEIGHT = 100



    fun initWorld(playerCount: Int) {
        val width = 162
        val height = 120


        fun gridBlock(x: Int, y: Int, extraLeft: Int = 0, extraRight: Int = 0, extraBottom: Int = 0, extraTop: Int = 0): Position {
            return Position(BLOCK_SIZE * (y + 1) - 1 + extraTop, BLOCK_SIZE * y - extraBottom, BLOCK_SIZE * x - extraLeft, BLOCK_SIZE * (x + 1) - 1 + extraRight)
        }

        fun initPlayers(count: Int): List<Int> {
            val positions = listOf(
                gridBlock(1, 1, extraTop = 1),
                gridBlock(1, BLOCK_Y_COUNT - 2, extraBottom = 1),
                gridBlock(BLOCK_X_COUNT - 2, BLOCK_Y_COUNT - 2, extraBottom = 1),
                gridBlock(BLOCK_X_COUNT - 2, 1, extraTop = 1)
            )
            return List(count) { addPlayer(it.toString(), positions[it]).id }
        }
        fun initWalls() {
            //addWall(WallType.SOLID, Position(70, 0, 0, 5))
            //addWall(WallType.DESTROYABLE, Position(70, 0, 100, 105))

            // left vertical
            repeat(BLOCK_Y_COUNT) { addWall(WallType.SOLID, gridBlock(0, it)) }
            repeat(BLOCK_Y_COUNT) { addWall(WallType.SOLID, gridBlock(BLOCK_X_COUNT - 1, it)) }
            repeat(BLOCK_X_COUNT - 2) { addWall(WallType.SOLID, gridBlock(it + 1, 0)) }
            repeat(BLOCK_X_COUNT - 2) { addWall(WallType.SOLID, gridBlock(it + 1, BLOCK_Y_COUNT - 1)) }

            repeat(BLOCK_Y_COUNT - 2) { addWall(WallType.DESTROYABLE, gridBlock(BLOCK_X_COUNT / 3, it + 1)) }
            repeat(BLOCK_Y_COUNT - 2) { addWall(WallType.STRONG, gridBlock(2 * BLOCK_X_COUNT / 3, it + 1)) }


        }
        fun initBonuses() {
            addBonus(BonusType.LIFE_EXTRA, gridBlock(BLOCK_X_COUNT / 2, BLOCK_Y_COUNT / 3))
            addBonus(BonusType.WEAPON_FAST, gridBlock(BLOCK_X_COUNT / 2, 2 * BLOCK_Y_COUNT / 3))
            addBonus(BonusType.SPEED_FAST, gridBlock(BLOCK_X_COUNT / 6, BLOCK_Y_COUNT / 2))
            addBonus(BonusType.WEAPON_HEAVY, gridBlock(5 * BLOCK_X_COUNT / 6, BLOCK_Y_COUNT / 2))
        }

        initPlayers(playerCount)
        initWalls()
        initBonuses()
    }



    private fun addPlayer(name: String, position: Position): IPlayer {
        fun removePlayer(player: IPlayer) {
            map.removeMapObject(player.id)
            players.remove(player)
        }
        val id = getNextId()
        val player = Player(
            id,
            doFire = { addBullet(it) },
            removePlayer = { removePlayer(it) }
        )
        players.add(player)
        map.createTankMapObject(id, player, name, position)
        return player
    }

    private fun addBullet(player: IPlayer): IBullet {
        val tankMapObject: MovableMapObject = map.getObjectById(player.id) as MovableMapObject? ?: throw Exception("Doesn't exist")
        fun removeBullet(bullet: IBullet) {
            map.removeMapObject(bullet.id)
            bullets.remove(bullet)
        }
        val id = getNextId()
        val bullet = Bullet(
            id,
            player.id,
            player.weapon,
            player.direction,
            removeBullet = { removeBullet(it) }
        )
        bullets.add(bullet)
        map.createBulletMapObject(id, player.weapon, bullet, tankMapObject)
        return bullet
    }

    private fun addWall(type: WallType, position: Position): IWall {
        //val tankMapObject: MovableMapObject = map.getObjectById(player.id) as MovableMapObject? ?: throw Exception("Doesn't exist")
        fun removeWall(wall: IWall) {
            map.removeMapObject(wall.id)
            walls.remove(wall)
        }
        val id = getNextId()
        val wall = Wall(
            id,
            type,
            removeWall = { removeWall(it) }
        )
        walls.add(wall)
        map.createWallMapObject(id, wall, type, position)
        return wall
    }

    private fun addBonus(type: BonusType, position: Position): IBonus {
        fun removeBonus(bonus: IBonus) {
            map.removeMapObject(bonus.id)
            bonuses.remove(bonus)
        }
        val id = getNextId()
        val bonus = Bonus(
            id,
            type,
            removeBonus = { removeBonus(it) }
        )
        bonuses.add(bonus)
        map.createBonusMapObject(id, bonus, type, position)
        return bonus
    }

    fun getCurrentState(): Map<GameObjectName, List<Pair<IGameObject, Position>>> {
//        //return Pair(
//        return players.map { Pair(it, map.getObjectById(it.id)!!.position) }
//            //bullets.map { Pair(it, map.getObjectById(it.id)!!.position) }
//        //)


        for (player in players) {
            val p = map.getObjectById(player.id)
            if (p == null) {
                val i = 0
            } else {
                p.position
            }
        }
        val p = players.map { Pair(it, map.getObjectById(it.id)!!.position) }
        val b = bullets.map { Pair(it, map.getObjectById(it.id)!!.position) }
        val w = walls.map { Pair(it, map.getObjectById(it.id)!!.position) }
        val bn = bonuses.map { Pair(it, map.getObjectById(it.id)!!.position) }

        return mutableMapOf<GameObjectName, List<Pair<IGameObject, Position>>>(
            GameObjectName.PLAYER to p,
            GameObjectName.BULLET to b,
            GameObjectName.WALL to w,
            GameObjectName.BONUS to bn
        )

    }

    fun nextGameTick(currentTime: Long, deltaTime: Long, actionsByPlayer: Map<Int, CurrentControllers>) {
        for (player in players) {
            val motionAction = actionsByPlayer[player.id]!!.motion
            // val motionAction: ControllerMotion? = actions.find { it is ControllerMotion } as ControllerMotion?
            val mapAction: Action? = player.processMotion(currentTime, deltaTime, motionAction)
            if (mapAction != null) {
                // interactions inside!!!
                map.processTankMotion(player.id, mapAction, currentTime)
            }
        }
        for (player in players) {
            val fireAction = actionsByPlayer[player.id]!!.fire
            //val fireAction: ControllerFire? = actions.find { it is ControllerFire } as ControllerFire?
            if (fireAction != null) {
                // Просто насоздаем пулек, действий на карте не требуется пока
                player.fire()
            }
        }
        for (bullet in bullets.map{ it }) {
            val action: Action? = bullet.go(bullet.direction, deltaTime)
            if (action != null && action is Motion) {
                map.processBulletMotion(bullet.id, action, currentTime)
            }
        }
        for (player in players.map{ it }) {
            player.processDeath(currentTime)
        }
    }

}