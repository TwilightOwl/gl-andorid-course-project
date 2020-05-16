package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.BulletType
import com.glandroidcourse.tanks.game.engine.map.IInteractable
import com.glandroidcourse.tanks.game.engine.map.Direction

interface IBullet : IDamageable, IMoving, ICanHit, ISolid, IInteractable {
    val bulletType: BulletType
    val removeBullet: (IBullet) -> Unit
}

class Bullet(
    override val id: Int,
    override val bulletType: BulletType,
    override var direction: Direction,
    override val removeBullet: (bullet: IBullet) -> Unit
) : Interactable(), IBullet {
    override var availableSpeed = bulletType.speed
    override var speed = bulletType.speed
    override var power = bulletType.power
    override var life = 1
    override val onDeath: (currentTime: Int) -> Unit = {
        removeBullet(this)
    }
    override fun interactWith(interactableObject: IInteractable, currentTime: Int) {
        this.hit(interactableObject, currentTime)
    }
}