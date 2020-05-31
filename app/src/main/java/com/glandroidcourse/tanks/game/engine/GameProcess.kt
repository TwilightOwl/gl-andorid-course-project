package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.Direction
import com.glandroidcourse.tanks.game.engine.map.Position
import kotlinx.coroutines.delay

class GameProcess(val playerCount: Int) {

    val game = GameLogic()

    val actionsByPlayer: MutableMap<Int, CurrentControllers>

    init {
        actionsByPlayer = mutableMapOf()
        clearActions()
    }

    fun clearActions() {
        repeat(playerCount) {
            actionsByPlayer[it] = CurrentControllers(null, null)
        }
    }

    //var TEMPDirection: Direction? = null // Direction.RIGHT
    //var firePressed = false

    fun goLeft() { incomingActions(0, motionAction = ControllerMotion(Direction.LEFT)) }
    fun goRight() { incomingActions(0, motionAction = ControllerMotion(Direction.RIGHT)) }
    fun goUp() { incomingActions(0, motionAction = ControllerMotion(Direction.UP)) }
    fun goDown() { incomingActions(0, motionAction = ControllerMotion(Direction.DOWN)) }
    fun fire() { incomingActions(0, fireAction = ControllerFire ) }

    // fun fire() { firePressed = true }
    //fun stop() { TEMPDirection = null }

    fun incomingActions(playerId: Int, motionAction: ControllerMotion? = null, fireAction: ControllerFire? = null) {
        println("$motionAction  $fireAction")
        motionAction?.let {actionsByPlayer[playerId]!!.motion = it}
        fireAction?.let {actionsByPlayer[playerId]!!.fire = it}
    }

    fun process(onStateChanged: (state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) -> Unit) {
        var stop = false
        // val players = listOf<IPlayer>() // TODO

        game.initWorld(playerCount)

        var lastTime: Long = System.currentTimeMillis()

        var i = 0

        while (!stop) {
            Thread.sleep(50L)
            val currentTime = System.currentTimeMillis()
            val deltaTime = currentTime - lastTime
            lastTime = currentTime

            //val actionsByPlayer = mutableMapOf<Int, List<ControllerAction>>() //TODO
//
//            val actions =
//                TEMPDirection?.let { mutableListOf<ControllerAction>(ControllerMotion(it)) } ?: mutableListOf()
//            if (firePressed) {
//                actions.add(ControllerFire)
//                firePressed = false
//            }
//
//            actionsByPlayer[0] = actions
//            actionsByPlayer[1] = mutableListOf()
//            actionsByPlayer[2] = mutableListOf()
//            actionsByPlayer[3] = mutableListOf()

            game.nextGameTick(currentTime, deltaTime, actionsByPlayer)
            clearActions()
            val state = game.getCurrentState()
            onStateChanged(state)
//
//                            // 3. produce game state (with current frame index)
//                            // if game state is game over => stop = true

        }
    }

}