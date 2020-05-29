package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

var id = 0
fun getNextId() = id++


class GameLogic {

    val players = mutableListOf<IPlayer>()
    val bullets = mutableListOf<IBullet>()
    val walls = mutableListOf<IWall>()
    // val bonuses = mutableListOf<IBullet>()
    val map = Map()

    fun initWalls() {
        addWall(WallType.SOLID, Position(70, 0, 0, 5))
        addWall(WallType.DESTROYABLE, Position(70, 0, 100, 105))
    }

    fun initPlayers(count: Int): List<Int> {
        return List(count) { addPlayer(it.toString()).id }
    }

    private fun addPlayer(name: String): IPlayer {
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
        map.createTankMapObject(id, name, player)
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

        return mutableMapOf<GameObjectName, List<Pair<IGameObject, Position>>>(
            GameObjectName.PLAYER to p,
            GameObjectName.BULLET to b,
            GameObjectName.WALL to w
        )

    }

    fun nextGameTick(currentTime: Long, deltaTime: Long, actionsByPlayer: Map<Int, List<ControllerAction>>) {
        for (player in players) {
            val actions = actionsByPlayer[player.id]!!
            val motionAction: ControllerMotion? = actions.find { it is ControllerMotion } as ControllerMotion?
            val mapAction: Action? = player.processMotion(currentTime, deltaTime, motionAction)
            if (mapAction != null) {
                // interactions inside!!!
                map.processTankMotion(player.id, mapAction, currentTime)
            }
        }
        for (player in players) {
            val actions = actionsByPlayer[player.id]!!
            val fireAction: ControllerFire? = actions.find { it is ControllerFire } as ControllerFire?
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