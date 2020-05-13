package com.glandroidcourse.tanks.game.engine

import java.lang.Math.round
import kotlin.math.roundToInt

interface IMovableGameObject : IGameObject {
    // публична т.к. может понадобться пересоздать объект
    var lastStepError: Float
    var direction: Direction
    fun getMoveTrajectory(direction: Direction, deltaTime: Int, speed: Float): IGameObject
    // используется когда сами указываем шаг, т.к. уперлись в стену, а по скорости должны были продвинуться дальше стены
    fun move(direction: Direction, step: Int)
    // используется в обычной ситуации, шаг расчитывается из скорости и пройденном времени, сохраняя в состоянии объекта ошибку округления шага, она учтется прислудующей итерации
    fun move(direction: Direction, deltaTime: Int, speed: Float)
    fun hasToBeRotated(direction: Direction): Boolean
    fun rotate(direction: Direction)
    fun getRotatedPosition(direction: Direction): Position
}

class MovableGameObject(
    override val id: Int,
    override val type: BaseObjectType,
    override var position: Position,
    override var direction: Direction,
    override var lastStepError: Float = 0f

) : GameObject(id, type, position), IMovableGameObject {

    private fun getStep(deltaTime: Int, speed: Float): Pair<Int, Float> {
        val currentPreciseStep = (deltaTime * speed + lastStepError)
        val currentDiscreteStep = currentPreciseStep.roundToInt()
        // lastStepError = currentPreciseStep - currentDiscreteStep
        return Pair(currentDiscreteStep, currentPreciseStep - currentDiscreteStep)
    }

    override fun move(direction: Direction, step: Int) {
        lastStepError = 0f
        when (direction) {
            Direction.UP -> { position.top += step; position.bottom += step }
            Direction.DOWN -> { position.top -= step; position.bottom -= step }
            Direction.RIGHT -> { position.right += step; position.left += step }
            Direction.LEFT -> { position.right -= step; position.left -= step }
        }
    }

    override fun move(direction: Direction, deltaTime: Int, speed: Float) {
        // нужно шаг расчитать исходя из deltaTime, сохранив новую lastStepError
        val (step, stepError) = getStep(deltaTime, speed)
        lastStepError = stepError
        when (direction) {
            Direction.UP -> { position.top += step; position.bottom += step }
            Direction.DOWN -> { position.top -= step; position.bottom -= step }
            Direction.RIGHT -> { position.right += step; position.left += step }
            Direction.LEFT -> { position.right -= step; position.left -= step }
        }
    }

    override fun getMoveTrajectory(direction: Direction, deltaTime: Int, speed: Float): IGameObject {
        val (step, _) = getStep(deltaTime, speed)
        val (top, bottom, left, right) = position
        return GameObject(id, type, when (direction) {
            Direction.UP -> Position(top + step, bottom, left, right)
            Direction.DOWN -> Position(top, bottom - step, left, right)
            Direction.RIGHT -> Position(top, bottom, left, right + step)
            Direction.LEFT -> Position(top, bottom, left - step, right)
        })
    }

    override fun rotate(direction: Direction) {
        if (direction == this.direction) return
        position = getRotatedPosition(direction)
        this.direction = direction
        lastStepError = 0f
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