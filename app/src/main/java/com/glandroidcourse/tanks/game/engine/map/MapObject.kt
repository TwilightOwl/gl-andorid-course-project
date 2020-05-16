package com.glandroidcourse.tanks.game.engine

import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1


sealed class BaseObjectType
data class TankObject(val tankId: Int) : BaseObjectType()
data class BulletObject(val bulletType: BulletType) : BaseObjectType()
data class WallObject(val wallType: WallType) : BaseObjectType()
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

interface IMapObject {
    val id: Int
    val type: BaseObjectType
    var position: Position
    fun intersectWith(anotherMapObject: IMapObject): Boolean
}

data class Position(var top: Int, var bottom: Int, var left: Int, var right: Int)

open class MapObject(override val id: Int, override val type: BaseObjectType, override var position: Position): IMapObject, Cloneable {

    override fun intersectWith(anotherMapObject: IMapObject): Boolean {
        val (top, bottom, left, right) = anotherMapObject.position

        //Position::bottom.getter.call(anotherMapObject.position)
        //val P: KProperty1<Position, Int> = Position::left

        return position.top >= bottom && position.bottom <= top && position.right >= left && position.left <= right && id != anotherMapObject.id
    }

    public override fun clone(): Any {
        return super.clone()
    }

}