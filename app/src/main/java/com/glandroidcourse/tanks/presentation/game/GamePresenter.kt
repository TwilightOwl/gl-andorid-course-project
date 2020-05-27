package com.glandroidcourse.tanks.presentation.game

import android.graphics.Color
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.glandroidcourse.tanks.game.engine.GameProcess
import javax.inject.Inject

@InjectViewState
class GamePresenter : MvpPresenter<IGameView> {

    @Inject
    constructor()

    val gameProcess = GameProcess()

    var color = Color.DKGRAY

    fun left() {
        color = Color.BLUE
        //TODO make it async or run in separate thread because now it blocks
        gameProcess.process()
        //TODO we never will be here
    }

    fun right() {
        color = Color.RED
    }

}