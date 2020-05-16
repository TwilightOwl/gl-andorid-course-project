package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.*

interface IPlayer : IDamageable, IMoving, ISolid, IInteractable {
    var weapon: BulletType
    val doFire: (player: IPlayer) -> Unit
    val removePlayer: (player: IPlayer) -> Unit
    fun fire()
    fun processMotion(currentTime: Int, deltaTime: Int, motionAction: ControllerMotion?): Action?
    fun processInteraction(currentTime: Int, deltaTime: Int)
    //fun processFire(currentTime: Int, deltaTime: Int, fireAction: ControllerFire)
    fun processDeath(currentTime: Int)
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
    var deathTime: Int = 0
    override val onDeath: (currentTime: Int) -> Unit = {
        deathTime = it
    }

    override fun processDeath(currentTime: Int) {
        if (currentTime - deathTime > corpsePeriod) {
            removePlayer(this)
        }
    }

    override fun fire() {
        if (isDead()) return
        return doFire(this)
    }

    override fun processMotion(currentTime: Int, deltaTime: Int, motionAction: ControllerMotion?): Action? {
        if (isDead()) return null
        return if (motionAction == null) {
            stop()
            null
        } else {
            go(motionAction.direction, deltaTime)
        }
    }

    override fun processInteraction(currentTime: Int, deltaTime: Int) {

    }

//    override fun processFire(currentTime: Int, deltaTime: Int, fireAction: ControllerFire) {
//        fire()
//    }

    override fun interactWith(gameObject: IInteractable, currentTime: Int) {
        // only bonus can interact with player
    }
}