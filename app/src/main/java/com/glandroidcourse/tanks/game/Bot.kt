package com.glandroidcourse.tanks.game

import com.glandroidcourse.tanks.game.engine.ControllerMotion
import com.glandroidcourse.tanks.game.engine.GameObjectName
import com.glandroidcourse.tanks.game.engine.IGameObject
import com.glandroidcourse.tanks.game.engine.map.Direction
import com.glandroidcourse.tanks.game.engine.map.Position
import kotlin.random.Random

class Bot(val playerId: Int) {

    var fireCounter = 0
    var motionCounter = 0
    var go: () -> Unit = { goUp() }

    init {
        // networkGame.onStateChangedListener = { state -> onStateChanged(state) }
        networkGame.addStateChangedListener { state -> onStateChanged(state) }
    }

    fun onStateChanged(state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) {
        if (fireCounter++ % 10 == 0) fire()
        if (motionCounter++ % 30 == 0) {
            go = when (Random.nextInt(4)) {
                0 -> ({ goDown() })
                1 -> ({ goUp() })
                2 -> ({ goLeft() })
                else -> ({ goRight() })
            }
        }
        go()
    }

    fun goUp() { networkGame.sendMotionAction(playerId, ControllerMotion(Direction.DOWN)) }
    fun goDown() { networkGame.sendMotionAction(playerId, ControllerMotion(Direction.UP)) }
    fun goRight() { networkGame.sendMotionAction(playerId, ControllerMotion(Direction.RIGHT)) }
    fun goLeft() { networkGame.sendMotionAction(playerId, ControllerMotion(Direction.LEFT)) }
    fun fire() { networkGame.sendFireAction(playerId) }
}