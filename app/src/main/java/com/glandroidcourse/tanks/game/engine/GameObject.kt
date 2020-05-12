package com.glandroidcourse.tanks.game.engine

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1


sealed class BaseObjectType
data class Tank(val tankId: Int) : BaseObjectType()
data class Bullet(val bulletType: BulletType) : BaseObjectType()
data class Wall(val wallType: WallType) : BaseObjectType()
// data class Bonus(val type: BonusType) : BaseObject()

enum class WallType(val solidity: Int) {
    SOLID(-1),
    DESTROYABLE(1),
    STRONG(5)
}

// Пули должны быть быстрее чем танки!!!

enum class BulletType(val speed: Int, val damage: Int) {
    SIMPLE(2, 1),
    HEAVY(2, 2),
    FAST(4, 1)
}

interface IGameObject {
    val id: Int
    val type: BaseObjectType
    var position: Position
    fun intersectWith(anotherGameObject: IGameObject): Boolean
}

data class Position(var top: Int, var bottom: Int, var left: Int, var right: Int)

open class GameObject(override val id: Int, override val type: BaseObjectType, override var position: Position): IGameObject, Cloneable {

    override fun intersectWith(anotherGameObject: IGameObject): Boolean {
        val (top, bottom, left, right) = anotherGameObject.position

        //Position::bottom.getter.call(anotherGameObject.position)
        //val P: KProperty1<Position, Int> = Position::left

        return position.top >= bottom && position.bottom <= top && position.right >= left && position.left <= right && id != anotherGameObject.id
    }

    public override fun clone(): Any {
        return super.clone()
    }

}