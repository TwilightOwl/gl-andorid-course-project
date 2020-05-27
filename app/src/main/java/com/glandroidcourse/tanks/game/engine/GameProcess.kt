package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.Direction


class GameProcess {

    val game = GameLogic()

    var TEMPDirection = Direction.UP

    fun goUp() { TEMPDirection = Direction.UP }
    fun goDown() { TEMPDirection = Direction.DOWN }
    fun goLeft() { TEMPDirection = Direction.LEFT }
    fun goRight() { TEMPDirection = Direction.RIGHT }

    fun process() {
        var stop = false
        val players = listOf<IPlayer>() // TODO

        game.initPlayers(1)

        var lastTime: Long = System.currentTimeMillis()
        while (!stop) {
            val currentTime = System.currentTimeMillis()
            val deltaTime = currentTime - lastTime
            lastTime = currentTime

            val actionsByPlayer = mutableMapOf<Int, List<ControllerAction>>() //TODO
            actionsByPlayer[0] = listOf(ControllerMotion(TEMPDirection))

            game.nextGameTick(currentTime, deltaTime, actionsByPlayer)

//
//                            // 3. produce game state (with current frame index)
//                            // if game state is game over => stop = true

        }
    }

}