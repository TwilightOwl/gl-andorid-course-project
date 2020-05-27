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
    val map = Map()


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
            player.weapon,
            player.direction,
            removeBullet = { removeBullet(it) }
        )
        bullets.add(bullet)
        map.createBulletMapObject(id, player.weapon, bullet, tankMapObject)
        return bullet
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
        for (bullet in bullets) {
            val action: Action? = bullet.go(bullet.direction, deltaTime)
            if (action != null && action is Motion) {
                map.processBulletMotion(bullet.id, action, currentTime)
            }
        }
        for (player in players) {
            player.processDeath(currentTime)
        }
    }

}