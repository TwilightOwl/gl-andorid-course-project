package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.*
import kotlin.math.max

sealed class ControllerAction
data class ControllerMotion(val direction: Direction) : ControllerAction()
object ControllerFire : ControllerAction()

abstract class Interactable : IInteractable {
    abstract override fun interactWith(interactableObject: IInteractable, currentTime: Int)
}

interface IGameObject /*: IInteractable*/ {
    val id: Int

//    override fun interactWith(interactableObject: IInteractable, currentTime: Int) {
//
//            val subjectIsDamageable = this is IDamageable
//            val subjectIsCanHit = this is ICanHit
//            val subjectIsSolid = this is ISolid
//            val objectIsDamageable = interactableObject is IDamageable
//            val objectIsCanHit = interactableObject is ICanHit
//            val objectIsSolid = interactableObject is ISolid
//
//            /*
//                пуля: Damageable, CanHit, Solid
//                танк: Damageable, Solid
//                стена: Solid   (Damageable для просты пока не будем делать)
//
//                пуля -> пуля
//                     -> танк
//                     -> стена
//                     -> бонус
//
//                танк -> пуля
//                     -> танк
//                     -> стена
//                     -> бонус
//
//                стена -> пуля
//                      -> танк
//                      -> стена Х
//                      -> бонус Х
//             */
//
//            if (subjectIsCanHit) {
//                val subject = this as ICanHit
//                subject.hit(interactableObject, currentTime)
//            }
//
//
//
//    }
}

interface ISolid : IGameObject

interface IDamageable : IGameObject {
    var life: Int
    val onDeath: (currentTime: Int) -> Unit
    fun damage(value: Int, currentTime: Int) {
        life = max(0, life - value)
        if (isDead()) {
            onDeath(currentTime)
        }
    }
    fun isDead(): Boolean {
        return life <= 0
    }
}

interface IMoving : IGameObject {
    var availableSpeed: Float
    var speed: Float
    var direction: Direction
    fun go(direction: Direction, deltaTime: Int): Action? {
        if (this is IDamageable && this.isDead()) {
            return null
        }
        if (direction == this.direction) {
            this.speed = availableSpeed
            val step = deltaTime * this.speed
            return Motion(direction, step)
        } else {
            stop()
            rotate(direction)
            return Rotation(direction)
        }
    }
    fun stop() {
        speed = 0f
    }
    private fun rotate(direction: Direction) {
        if (this is IDamageable && this.isDead()) {
            return
        }
        this.direction = direction
    }
}



interface ICanHit : IGameObject {
    var power: Int
    fun hit(gameObject: IInteractable, currentTime: Int) {
        val subjectIsDamageable = this is IDamageable
        val objectIsDamageable = gameObject is IDamageable
        val objectIsSolid = gameObject is ISolid

        if (subjectIsDamageable && (this as IDamageable).isDead()) {
            return
        }

        if (objectIsDamageable) {
            (gameObject as IDamageable).damage(power, currentTime)
        }
        if (objectIsSolid || objectIsDamageable) {
            if (subjectIsDamageable) {
                (this as IDamageable).damage(1, currentTime)
            }
        }

    }
}

