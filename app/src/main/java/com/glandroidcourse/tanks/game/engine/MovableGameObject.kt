package com.glandroidcourse.tanks.game.engine

interface IMovableGameObject : IGameObject {
    var direction: Direction
    fun move(direction: Direction, step: Int)
    fun hasToBeRotated(direction: Direction): Boolean
    fun rotate(direction: Direction)
    fun getRotatedPosition(direction: Direction): Position
}

class MovableGameObject(override val id: Int, override val type: BaseObjectType, override var position: Position, override var direction: Direction) : GameObject(id, type, position), IMovableGameObject {

    override fun move(direction: Direction, step: Int) {
        when (direction) {
            Direction.UP -> { position.top += step; position.bottom += step }
            Direction.DOWN -> { position.top -= step; position.bottom -= step }
            Direction.RIGHT -> { position.right += step; position.left += step }
            Direction.LEFT -> { position.right -= step; position.left -= step }
        }
    }

    override fun rotate(direction: Direction) {
        if (direction == this.direction) return
        position = getRotatedPosition(direction)
        this.direction = direction
    }

    override fun hasToBeRotated(direction: Direction): Boolean {
        return this.direction != direction
    }

    override fun getRotatedPosition(direction: Direction): Position {
        val (top, bottom, left, right) = position
        val width = right - left
        val height = top - bottom
        return when (this.direction) {
            Direction.UP -> when (direction) {
                Direction.RIGHT -> Position(top = top, left = left, bottom = top - width, right = left + height)
                Direction.LEFT -> Position(top = top, right = right, bottom = top - width, left = right - height)
                else -> position
            }
            Direction.DOWN -> when (direction) {
                Direction.RIGHT -> Position(bottom = bottom, left = left, top = bottom + width, right = left + height)
                Direction.LEFT -> Position(bottom = bottom, right = right, top = bottom + width, left = right - height)
                else -> position
            }
            Direction.RIGHT -> when (direction) {
                Direction.UP -> Position(bottom = bottom, right = right, top = bottom + width, left = right - height)
                Direction.DOWN -> Position(top = top, right = right, bottom = top - width, left = right - height)
                else -> position
            }
            Direction.LEFT -> when (direction) {
                Direction.UP -> Position(bottom = bottom, left = left, top = bottom + width, right = left + height)
                Direction.DOWN -> Position(top = top, left = left, bottom = top - width, right = left + height)
                else -> position
            }
        }
    }

}