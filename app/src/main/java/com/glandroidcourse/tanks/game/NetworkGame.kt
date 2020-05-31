package com.glandroidcourse.tanks.game

import com.glandroidcourse.tanks.game.engine.*
import com.glandroidcourse.tanks.game.engine.map.Direction
import com.glandroidcourse.tanks.game.engine.map.Position
import kotlin.concurrent.thread

/*
Модуль должен по сети отправлять и получать данные. Пока он взаимодействует напрямую с игровым движком
 */

class NetworkGame {

    val gameProcess = GameProcess(4)

    // var onStateChangedListener: ((state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) -> Unit)? = null

    val listeners = mutableListOf<(state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) -> Unit>()

    fun addStateChangedListener(listener: (state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) -> Unit) {
        listeners.add(listener)
    }

    fun startGameServer() {
        //TODO make it async or run in separate thread because now it blocks
        thread { gameProcess.process({ state -> onStateChanged(state) }) }
        // gameProcess.process({ state -> onStateChanged(state) })
    }

    init {
        startGameServer()
    }

    fun onStateChanged(state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) {
        // println(state)
        listeners.forEach { it(state) }
        // onStateChangedListener?.let { it(state) }
    }

    fun sendMotionAction(playerId: Int, motionAction: ControllerMotion) {
        gameProcess.incomingActions(playerId, motionAction)
    }

    fun sendFireAction(playerId: Int) {
        gameProcess.incomingActions(playerId, fireAction = ControllerFire)
    }
}

val networkGame = NetworkGame()
val bots = List(3) { Bot(it + 1) }