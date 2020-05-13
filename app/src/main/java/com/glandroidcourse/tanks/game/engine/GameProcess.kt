package com.glandroidcourse.tanks.game.engine

class GameProcess {

    val field = GameField()

    fun tick() {}



    fun process() {
        var stop = false
        val players = listOf<IPlayer>() // TODO
        var lastTime: Int = 0 // TODO
        while (!stop) {
            val currentTime = 0 // TODO
            val deltaTime = currentTime - lastTime
            lastTime = currentTime


            // 1. get current actions from players
            val actions = listOf<Action>() // TODO
            // 2. tick:
                // - move players
                for (player in players) {
                    field.action(player, actions, deltaTime)
                }

                // - move bullets
                field.processOtherObjects(deltaTime)

                // - update state of objects

            // 3. produce game state (with current frame index)
            // if game state is game over => stop = true

        }
    }

}