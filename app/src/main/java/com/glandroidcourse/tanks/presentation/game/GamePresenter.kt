package com.glandroidcourse.tanks.presentation.game

import android.graphics.Color
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.glandroidcourse.tanks.game.NetworkGame
import com.glandroidcourse.tanks.game.engine.*
import com.glandroidcourse.tanks.game.engine.map.Direction
import com.glandroidcourse.tanks.game.engine.map.Position
import com.glandroidcourse.tanks.game.networkGame
import javax.inject.Inject
import kotlin.concurrent.thread

@InjectViewState
class GamePresenter : MvpPresenter<IGameFragment> {

    @Inject
    constructor()

    val playerId = 0

    init {
        // networkGame.onStateChangedListener = { state -> onStateChanged(state) }
        networkGame.addStateChangedListener { state -> onStateChanged(state) }
    }

    fun onStateChanged(state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) {
        viewState.onStateChanged(state)
    }

    fun goUp() { networkGame.sendMotionAction(playerId, ControllerMotion(Direction.DOWN)) }
    fun goDown() { networkGame.sendMotionAction(playerId, ControllerMotion(Direction.UP)) }
    fun goRight() { networkGame.sendMotionAction(playerId, ControllerMotion(Direction.RIGHT)) }
    fun goLeft() { networkGame.sendMotionAction(playerId, ControllerMotion(Direction.LEFT)) }
    fun fire() { networkGame.sendFireAction(playerId) }

}