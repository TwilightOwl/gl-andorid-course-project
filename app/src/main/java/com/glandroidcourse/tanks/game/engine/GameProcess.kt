package com.glandroidcourse.tanks.game.engine


class GameProcess {

    val game = GameLogic()

    fun process() {
        var stop = false
        val players = listOf<IPlayer>() // TODO
        var lastTime: Long = System.currentTimeMillis()
        while (!stop) {
            val currentTime = System.currentTimeMillis()
            val deltaTime = currentTime - lastTime
            lastTime = currentTime

            val actionsByPlayer = mapOf<Int, List<ControllerAction>>() //TODO
            game.nextGameTick(currentTime, deltaTime, actionsByPlayer)

//
//                            // 3. produce game state (with current frame index)
//                            // if game state is game over => stop = true

        }
    }

}