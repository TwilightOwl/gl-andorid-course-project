package com.glandroidcourse.tanks.presentation.game

import android.graphics.Color
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.glandroidcourse.tanks.game.engine.GameObjectName
import com.glandroidcourse.tanks.game.engine.GameProcess
import com.glandroidcourse.tanks.game.engine.IGameObject
import com.glandroidcourse.tanks.game.engine.map.Position
import javax.inject.Inject
import kotlin.concurrent.thread

@InjectViewState
class GamePresenter : MvpPresenter<IGameFragment> {

    @Inject
    constructor()

    var i = 0

    val gameProcess = GameProcess()

    var color = Color.DKGRAY

    fun onStateChanged(state: Map<GameObjectName, List<Pair<IGameObject, Position>>>) {
        // println(state)
        viewState.onStateChanged(state)
    }

    fun start() {
        color = Color.BLUE
        //TODO make it async or run in separate thread because now it blocks
        thread { gameProcess.process({ state -> onStateChanged(state) }) }
        //gameProcess.process({ state -> onStateChanged(state) })
        //TODO we never will be here
        val r = 9
    }

    fun goUp() { gameProcess.goUp() }
    fun goDown() { gameProcess.goDown() }
    fun goRight() { gameProcess.goRight() }
    fun goLeft() { gameProcess.goLeft() }
    fun fire() { gameProcess.fire() }

    fun old() {
        i++
        if (i > 4) i = 0
        // color = Color.RED
        when (i) {
            0 -> gameProcess.stop()
            1 -> gameProcess.goUp()
            2 -> gameProcess.goLeft()
            3 -> gameProcess.goDown()
            4 -> gameProcess.goRight()
        }
    }

}