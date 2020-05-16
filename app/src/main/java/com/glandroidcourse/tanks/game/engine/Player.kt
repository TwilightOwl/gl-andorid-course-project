package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.*

interface IPlayer : IDamageable, IMoving, ISolid, IInteractable {
    var weapon: BulletType
    val doFire: (player: IPlayer) -> Unit
    val removePlayer: (player: IPlayer) -> Unit
    fun fire()
    fun processMotion(currentTime: Long, deltaTime: Long, motionAction: ControllerMotion?): Action?
    fun processInteraction(currentTime: Long, deltaTime: Long)
    //fun processFire(currentTime: Int, deltaTime: Int, fireAction: ControllerFire)
    fun processDeath(currentTime: Long)
}

class Player (
    override val id: Int,
    override val doFire: (player: IPlayer) -> Unit,
    override val removePlayer: (player: IPlayer) -> Unit
) : Interactable(), IPlayer {
    override var availableSpeed: Float = 1f
    override var speed: Float = 1f
    override var direction = Direction.UP
    override var weapon: BulletType = BulletType.SIMPLE
    override var life: Int = 10
    val corpsePeriod = 10
    var deathTime: Long = 0
    override val onDeath: (currentTime: Long) -> Unit = {
        deathTime = it
    }

    override fun processDeath(currentTime: Long) {
        if (currentTime - deathTime > corpsePeriod) {
            removePlayer(this)
        }
    }

    override fun fire() {
        if (isDead()) return
        return doFire(this)
    }

    override fun processMotion(currentTime: Long, deltaTime: Long, motionAction: ControllerMotion?): Action? {
        if (isDead()) return null
        return if (motionAction == null) {
            stop()
            null
        } else {
            go(motionAction.direction, deltaTime)
        }
    }

    override fun processInteraction(currentTime: Long, deltaTime: Long) {

    }

//    override fun processFire(currentTime: Int, deltaTime: Int, fireAction: ControllerFire) {
//        fire()
//    }

    override fun interactWith(gameObject: IInteractable, currentTime: Long) {
        // only bonus can interact with player
    }
}