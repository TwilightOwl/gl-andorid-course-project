package com.glandroidcourse.tanks.presentation.game

import android.graphics.Color
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import javax.inject.Inject

@InjectViewState
class GamePresenter : MvpPresenter<IGameView> {

    @Inject
    constructor()

    var color = Color.DKGRAY

    fun left() {
        color = Color.BLUE
    }

    fun right() {
        color = Color.RED
    }

}