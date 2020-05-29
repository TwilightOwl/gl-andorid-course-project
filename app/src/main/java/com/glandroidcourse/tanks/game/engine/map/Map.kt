package com.glandroidcourse.tanks.game.engine.map

import com.glandroidcourse.tanks.game.engine.IBullet
import kotlin.math.abs
import kotlin.math.sign

class Map {

    private val mapOfOrderedList = mapOf(
        Position::top to OrderedMapObjects(
            Position::top
        ),
        Position::bottom to OrderedMapObjects(
            Position::bottom
        ),
        Position::left to OrderedMapObjects(
            Position::left
        ),
        Position::right to OrderedMapObjects(
            Position::right
        )
    )

    fun getObjectById(objectId: Int): IMapObject? {
        val obj = mapOfOrderedList[Position::top]!!.getObjectById(objectId)
        if (obj == null) {
            val k = 0
        }
        return obj
    }

    private fun addMapObject(mapObject: IMapObject) {
        //TODO: подумать как сделать код менее уродским
        mapOfOrderedList[Position::top]!!.insert(mapObject)
        mapOfOrderedList[Position::bottom]!!.insert(mapObject)
        mapOfOrderedList[Position::left]!!.insert(mapObject)
        mapOfOrderedList[Position::right]!!.insert(mapObject)
    }

    fun removeMapObject(mapObjectId: Int) {
        mapOfOrderedList[Position::top]!!.remove(mapObjectId)
        mapOfOrderedList[Position::bottom]!!.remove(mapObjectId)
        mapOfOrderedList[Position::left]!!.remove(mapObjectId)
        mapOfOrderedList[Position::right]!!.remove(mapObjectId)
    }

    fun updateMapObject(mapObjectId: Int) {
        mapOfOrderedList[Position::top]!!.update(mapObjectId)
        mapOfOrderedList[Position::bottom]!!.update(mapObjectId)
        mapOfOrderedList[Position::left]!!.update(mapObjectId)
        mapOfOrderedList[Position::right]!!.update(mapObjectId)
    }

    fun createTankMapObject(id: Int, name: String, player: IInteractable): IMovableMapObject {
        //TODO: передавать через параметры или расставлять в определенных местах
        val initialLeft = 10 + 30 * id
        val initialBottom = 10 + 30 * id
        val tankObject = TankObject(name)
        val tank: IMovableMapObject = MovableMapObject(
            id,
            player,
            tankObject,
            Position(initialBottom + tankObject.height, initialBottom, initialLeft, initialLeft + tankObject.width),
            Direction.UP
        )
        addMapObject(tank)
        return tank
    }

    fun createBulletMapObject(id: Int, bulletType: BulletType, bullet: IInteractable, tankMapObject: IMovableMapObject): IMovableMapObject {
        val (top, bottom, left, right) = tankMapObject.position
        val horizontalMiddle: Int = right - (right - left + 1) / 2
        val verticalMiddle: Int = top - (top - bottom + 1) / 2
        val bullet: IMovableMapObject =
            MovableMapObject(
                id,
                bullet,
                BulletObject(bulletType),
                //TODO: учесть большой размер для мощных пуль
                when (tankMapObject.direction) {
                    // Пуля пока находится в пределах танка. Дальше в рамках этого же игрового тика, пуля попробует двинуться с учетом скорости пули
                    Direction.UP -> Position(
                        top,
                        top,
                        horizontalMiddle,
                        horizontalMiddle
                    )
                    Direction.DOWN -> Position(
                        bottom,
                        bottom,
                        horizontalMiddle,
                        horizontalMiddle
                    )
                    Direction.LEFT -> Position(
                        verticalMiddle,
                        verticalMiddle,
                        left,
                        left
                    )
                    Direction.RIGHT -> Position(
                        verticalMiddle,
                        verticalMiddle,
                        right,
                        right
                    )
                },
                tankMapObject.direction
            )
        addMapObject(bullet)
        return bullet
    }

    fun processTankMotion(tankMapObjectId: Int, action: Action, currentTime: Long) {

        if (getObjectById(tankMapObjectId) == null) {
            val f = 0
        }

        val subject: MovableMapObject = getObjectById(tankMapObjectId) as MovableMapObject? ?: throw Exception("Doesn't exist")

        when (action) {
            // такой тип сюда не может прилететь, и вообще обрабатывается стрельба отдельно, здесь только движения.
            // поискать можно ли в kotlin action сделать типом "Action omit Fire" (Action исключая Fire)
            Fire -> null
            is Motion -> {
                val (direction, step) = action
                val (subjectEdge, objectEdge) = when (direction) {
                    Direction.UP -> Pair(Position::top, Position::bottom)
                    Direction.DOWN -> Pair(Position::bottom, Position::top)
                    Direction.LEFT -> Pair(Position::left, Position::right)
                    Direction.RIGHT -> Pair(Position::right, Position::left)
                }
                val trajectory = subject.getMoveTrajectory(direction, step)
                val previousPosition = subjectEdge.getter.call(subject.position)
                val newPosition = subjectEdge.getter.call(trajectory.position)
                val intersectedObjects = mapOfOrderedList[objectEdge]!!.findIntersections(trajectory, previousPosition, newPosition)
                // находим ближайший (первый же) упор об стену или танк (nearestSolidObject), и двигаемся только до него (nearestSolidEdge +/- 1)
                var nearestSolidEdge: Int? = null
                var stepToNearestSolidEdge: Int? = null
                for (intersectedObject in intersectedObjects) {
                    if (intersectedObject.type is WallObject || intersectedObject.type is TankObject) {
                        nearestSolidEdge = objectEdge.getter.call(intersectedObject.position)
                        stepToNearestSolidEdge = nearestSolidEdge - previousPosition
                        subject.move(direction, abs(stepToNearestSolidEdge))
                        updateMapObject(tankMapObjectId)
                        break
                    }
                }
                if (nearestSolidEdge == null) {
                    subject.move(direction, step)
                    updateMapObject(tankMapObjectId)
                }
                for (intersectedObject in intersectedObjects) {
                    /*
                    Если нашли ближайший упор, то получается что в траектории по которой искали пересечение
                        отбрасывается часть куда объект не дошел. Поэтому отбрасываем intersectedObject у которых позиция objectEdge
                        превышает (если Direction.RIGHT или Direction.UP, а если LEFT\DOWN - то меньше) позицию subjectEdge подвинутого объекта
                        (т.е. величину nearestSolidObject +/- 1).
                    А с остальными intersectedObjects объект будет взаимодействовать
                    */
                    val currentObjectEdge = objectEdge.getter.call(intersectedObject.position)
                    if (step != null && nearestSolidEdge != null) {
                        if (step.sign * currentObjectEdge >= step.sign * nearestSolidEdge) {
                            break
                        }
                    }
                    subject.gameObject.interactWith(intersectedObject.gameObject, currentTime)
                    intersectedObject.gameObject.interactWith(subject.gameObject, currentTime)
                }
            }

            is Rotation -> {
                val (direction) = action
                // TODO: Учесть границы игрового поля!!!
                val rotatedPosition = subject.getRotatedPosition(direction)
                val (top, bottom, left, right) = rotatedPosition
                val (canBeRotated, intersectedObjects) = when {
                    subject.position.top < top -> Pair(Position::top, Position::bottom)
                    subject.position.bottom > bottom -> Pair(Position::bottom, Position::top)
                    subject.position.left > left -> Pair(Position::left, Position::right)
                    subject.position.right < right -> Pair(Position::right, Position::left)
                    else -> null
                }?.let {
                    val (subjectEdge, objectEdge) = it
                    val rotatedSubject =
                        (subject.clone() as MovableMapObject).apply { rotate(direction) }
                    val previousPosition = subjectEdge.getter.call(subject.position)
                    val newPosition = subjectEdge.getter.call(rotatedSubject.position)
                    val intersectedObjects = mapOfOrderedList[objectEdge]!!.findIntersections(
                        rotatedSubject,
                        previousPosition,
                        newPosition
                    )
                    for (intersectedObject in intersectedObjects) {
                        if (intersectedObject.type is WallObject || intersectedObject.type is TankObject) {
                            return@let Pair(false, null)
                        }
                    }
                    return@let Pair(true, intersectedObjects)
                } ?: Pair(true, null)

                if (!canBeRotated) return // do nothing

                if (intersectedObjects == null) {
                    // just rotate
                    subject.rotate(direction)
                    updateMapObject(tankMapObjectId)
                } else {
                    subject.rotate(direction)
                    updateMapObject(tankMapObjectId)
                    for (intersectedObject in intersectedObjects) {
                        subject.gameObject.interactWith(intersectedObject.gameObject, currentTime)
                        intersectedObject.gameObject.interactWith(subject.gameObject, currentTime)
                    }
                }
            }
        }
    }

    fun processBulletMotion(bulletMapObjectId: Int, action: Motion, currentTime: Long) {
        val subject: MovableMapObject = getObjectById(bulletMapObjectId) as MovableMapObject? ?: throw Exception("Doesn't exist")
        val (direction, step) = action
        val (subjectEdge, objectEdge) = when (direction) {
            Direction.UP -> Pair(Position::top, Position::bottom)
            Direction.DOWN -> Pair(Position::bottom, Position::top)
            Direction.LEFT -> Pair(Position::left, Position::right)
            Direction.RIGHT -> Pair(Position::right, Position::left)
        }
        val trajectory = subject.getMoveTrajectory(direction, step)
        val previousPosition = subjectEdge.getter.call(subject.position)
        val newPosition = subjectEdge.getter.call(trajectory.position)
        val intersectedObjects = mapOfOrderedList[objectEdge]!!.findIntersections(trajectory, previousPosition, newPosition)

        var nearestSolidEdge: Int? = null
        var stepToNearestSolidEdge: Int? = null
        for (intersectedObject in intersectedObjects) {
            if (intersectedObject.type is WallObject || intersectedObject.type is TankObject) {
                nearestSolidEdge = objectEdge.getter.call(intersectedObject.position)
                stepToNearestSolidEdge = nearestSolidEdge - previousPosition
                subject.move(direction, abs(stepToNearestSolidEdge))
                updateMapObject(bulletMapObjectId)
                subject.gameObject.interactWith(intersectedObject.gameObject, currentTime)
                intersectedObject.gameObject.interactWith(subject.gameObject, currentTime)
                //TODO: если пуля жива - продолжаем цикл, если нет - выходим. Для простоты будем давать жизнь всем пулям 1,
                // но жизнь не уменьшается например при взаимодействии с бонусами пуле наносится урон, если она врезается в твердое
                break
            }
        }
        if (nearestSolidEdge == null) {
            subject.move(direction, step)
            updateMapObject(bulletMapObjectId)
        }
    }
}