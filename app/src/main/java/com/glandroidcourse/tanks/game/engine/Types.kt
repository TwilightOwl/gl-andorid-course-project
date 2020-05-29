package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.*
import kotlin.math.max

enum class GameObjectName { PLAYER, BULLET, WALL, BONUS }

sealed class ControllerAction
data class ControllerMotion(val direction: Direction) : ControllerAction()
object ControllerFire : ControllerAction()

abstract class Interactable : IInteractable {
    abstract override fun interactWith(interactableObject: IInteractable, currentTime: Long)
}

interface IGameObject {
    val id: Int
}

interface ISolid : IGameObject

interface IDamageable : IGameObject {
    var life: Int
    val onDeath: (currentTime: Long) -> Unit
    fun damage(value: Int, currentTime: Long) {
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
    fun go(direction: Direction, deltaTime: Long): Action? {
        if (this is IDamageable && this.isDead()) {
            return null
        }
        if (direction == this.direction) {
            this.speed = availableSpeed
            val step = deltaTime * this.speed * 0.01f
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
    fun hit(gameObject: IInteractable, currentTime: Long) {
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