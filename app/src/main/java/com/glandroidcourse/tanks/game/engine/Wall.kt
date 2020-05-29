package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.BulletType
import com.glandroidcourse.tanks.game.engine.map.IInteractable
import com.glandroidcourse.tanks.game.engine.map.Direction
import com.glandroidcourse.tanks.game.engine.map.WallType

interface IWall : IDamageable, ISolid, IInteractable {
    val wallType: WallType
    val removeWall: (IWall) -> Unit
}

class Wall(
    override val id: Int,
    override val wallType: WallType,
    override val removeWall: (bullet: IWall) -> Unit
) : Interactable(), IWall {
    override var life = wallType.solidity
    override val onDeath: (currentTime: Long) -> Unit = {
        removeWall(this)
    }
    override fun interactWith(interactableObject: IInteractable, currentTime: Long) {}
}