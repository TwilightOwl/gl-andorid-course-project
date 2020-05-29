package com.glandroidcourse.tanks.presentation.game

import com.arellomobile.mvp.MvpView
import com.glandroidcourse.tanks.game.engine.GameObjectName
import com.glandroidcourse.tanks.game.engine.IGameObject
import com.glandroidcourse.tanks.game.engine.map.Position

interface IGameFragment : MvpView {
    fun onStateChanged(state: Map<GameObjectName, List<Pair<IGameObject, Position>>>): Unit
}