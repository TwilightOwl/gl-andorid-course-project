package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.*

interface IBonus : IDamageable, IInteractable {
    val bonusType: BonusType
    val removeBonus: (IBonus) -> Unit
}

class Bonus(
    override val id: Int,
    override val bonusType: BonusType,
    override val removeBonus: (bonus: IBonus) -> Unit
) : Interactable(), IBonus {
    override var life = 1
    override val onDeath: (currentTime: Long) -> Unit = {
        removeBonus(this)
    }
    override fun interactWith(interactableObject: IInteractable, currentTime: Long) {
        if (interactableObject is IPlayer) {
            bonusType.bulletType?.let { interactableObject.weapon = it }
            bonusType.extraLife?.let { interactableObject.life += it }
            bonusType.speed?.let { interactableObject.availableSpeed = it }
            removeBonus(this)
        }
    }
}