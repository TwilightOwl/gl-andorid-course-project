package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.Direction
import com.glandroidcourse.tanks.game.engine.map.Position
import kotlinx.coroutines.delay


class GameProcess {

    val game = GameLogic()

    var TEMPDirection: Direction? = null // Direction.RIGHT
    var firePressed = false

    fun goUp() { TEMPDirection = Direction.UP }
    fun goDown() { TEMPDirection = Direction.DOWN }
    fun goLeft() { TEMPDirection = Direction.LEFT }
    fun goRight() { TEMPDirection = Direction.RIGHT }
    fun fire() { firePressed = true }
    fun stop() { TEMPDirection = null }

    fun process(onStateChanged: (state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) -> Unit) {
        var stop = false
        // val players = listOf<IPlayer>() // TODO


        game.initWorld(4)


        var lastTime: Long = System.currentTimeMillis()

        var i = 0

        while (!stop) {
            Thread.sleep(50L)
            val currentTime = System.currentTimeMillis()
            val deltaTime = currentTime - lastTime
            lastTime = currentTime

            val actionsByPlayer = mutableMapOf<Int, List<ControllerAction>>() //TODO

//            i++
//            if (i == 2) {
//                firePressed = true
//            }

            val actions =
                TEMPDirection?.let { mutableListOf<ControllerAction>(ControllerMotion(it)) } ?: mutableListOf()
            if (firePressed) {
                actions.add(ControllerFire)
                firePressed = false
            }

            actionsByPlayer[0] = actions
            actionsByPlayer[1] = mutableListOf()
            actionsByPlayer[2] = mutableListOf()
            actionsByPlayer[3] = mutableListOf()

            game.nextGameTick(currentTime, deltaTime, actionsByPlayer)
            val state = game.getCurrentState()
            onStateChanged(state)
//
//                            // 3. produce game state (with current frame index)
//                            // if game state is game over => stop = true

        }
    }

}