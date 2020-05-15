package com.glandroidcourse.tanks.game.engine

import kotlin.math.max
import kotlin.math.min

//enum class Color(val rgb: Int) {
//    RED(0xFF0000),
//    GREEN(0x00FF00),
//    BLUE(0x0000FF)
//}

enum class Direction { UP, DOWN, LEFT, RIGHT }

sealed class Action
data class Motion(val direction: Direction, val step: Float) : Action()
data class Rotation(val direction: Direction) : Action()
object Fire : Action()

/*
interface IPlayer {
    val id: Int
    var x: Int
    var y: Int
    var step: Int
    val bodySize: Int
    // fun move(direction: Direction, xBorder: Int, yBorder: Int)
    var direction: Direction
}

//enum class Element(val value: Int) {
//    EMPTY(0),
//    BULLET(1),
//    TANK(2),
//    WALL(3)
//}

sealed class Element
object Empty : Element() {
    const val value = 0
}
data class Tank(val id: Int) : Element() {
    val value = id + 31
}
data class Bullet(val type: BulletType) : Element()
data class Wall(val type: WallType) : Element()
data class Bonus(val type: BonusType) : Element()

enum class WallType(val value: Int) {
    SOLID(1),
    DESTROYABLE(2)
}
enum class BulletType(val value: Int) {
    SIMPLE(11),
    HEAVY(12)
}
enum class BonusType(val value: Int) {
    LIFE_1(21),
    LIFE_2(22),
    ARMOR_SHORT(23),
    ARMOR_LONG(24),
    FIRE_HEAVY_SHORT(25),
    FIRE_HEAVY_LONG(26)
}



class Player(
    override val id: Int,
    override var x: Int,
    override var y: Int
): IPlayer {
    override var step = 1
    override val bodySize = 1
    override var direction = Direction.UP

//    fun move(direction: Direction, xBorder: Int, yBorder: Int) {
//        x = max(bodySize, min(xBorder - bodySize,
//            x + step * when (direction) { Direction.LEFT -> -1; Direction.RIGHT -> 1; else -> 0 }
//        ))
//        y = max(bodySize, min(yBorder - bodySize,
//            y + step * when (direction) { Direction.UP -> -1; Direction.DOWN -> 1; else -> 0 }
//        ))
//    }


}
*/

class OLD_GameField(
    val width: Int = 20,
    val height: Int = 20,
    val playerCount: Int = 2
) {

    val players = mutableListOf<IPlayer>()
    var field = arrayOf<Array<Int>>()

    init {
        // fill the field
        for (x in 1..width) {
            var column = arrayOf<Int>()
            for (y in 1..height) column += Empty.value
            field += column
        }

        for (id in 0..playerCount-1) players.add(
            Player(id, 1 + id * 3, 1 + id * 3)
        )
    }

    fun getActualField() {

        // TODO: copy field to new field
        for (player in players) {
            // TODO: draw tank body field consider tank direction
            field[player.x][player.y] = Tank(player.id).value
        }
        for (bullet in bullets) {
            // TODO: draw tank body field consider tank direction
            field[bullet.x][bullet.y] = Bullet
        }

    }

    /*
        move all tanks:
            - calc xy
            - checkCollision
                - bullet x tank -> -1 life; dead bullet
                - мертвый танк остается как припятствие для других танков, удалится в конце итерации (или дольше пролежит на поле без возможности управлять им)
            - move
        move all bullets
            - calc xy
            - checkCollision
            - move
        remove dead tanks & bullets
        init new tanks
     */

    fun move(playerId: Int, direction: Direction, xBorder: Int, yBorder: Int) {
        val player = players[playerId]

        if (player.direction != direction) {
            player.direction = direction
        } else {
            val x = max(player.bodySize, min(xBorder - player.bodySize,
                player.x + player.step * when (direction) { Direction.LEFT -> -1; Direction.RIGHT -> 1; else -> 0 }
            ))
            val y = max(player.bodySize, min(yBorder - player.bodySize,
                player.y + player.step * when (direction) { Direction.UP -> -1; Direction.DOWN -> 1; else -> 0 }
            ))

            // ok
            // ok & hurt
            // stop
            val  = checkCollision(playerId, x, y)
        }


    }

    fun checkCollision(playerId: Int, x: Int, y: Int, direction: Direction) {

    }

    fun action(playerId: Int, actions: List<Action>) {
        val player = players[playerId]
        for (action in actions) {
            when (action) {
                is Motion -> player.move(action.direction, width, height)
                Fire -> player.fire()
            }
        }
    }

    private fun movePlayer(direction: Direction) {

    }

    fun iteration() {

    }

    fun getState() {

    }
}