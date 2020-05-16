package com.glandroidcourse.tanks.game.engine.map

interface IMapObject {
    val id: Int
    val gameObject: IInteractable
    val type: BaseObjectType
    var position: Position
    fun intersectWith(anotherMapObject: IMapObject): Boolean
}

open class MapObject(override val id: Int, override val gameObject: IInteractable, override val type: BaseObjectType, override var position: Position): IMapObject, Cloneable {

    override fun intersectWith(anotherMapObject: IMapObject): Boolean {
        val (top, bottom, left, right) = anotherMapObject.position
        return position.top >= bottom && position.bottom <= top && position.right >= left && position.left <= right && id != anotherMapObject.id
    }

    public override fun clone(): Any {
        return super.clone()
    }
}

