package com.glandroidcourse.tanks.game.engine

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

var id = 0
fun getNextId() = id++

interface IGameObject {
    val id: Int
}

interface IDamageable : IGameObject {
    var life: Int
    val onDeath: () -> Unit
    fun damage(value: Int) {
        life = max(0, life - value)
        if (isDead()) {
            onDeath()
        }
    }
    fun isDead(): Boolean {
        return life <= 0
    }
}

//                    abstract class Damageable(
//                        override val id: Int,
//                        override var life: Int,
//                        override val onDeath: (objectId: Int) -> Unit
//                    ) : IDamageable {
//
//                        override fun damage(value: Int) {
//                            life = max(0, life - value)
//                            if (isDead()) {
//                                onDeath(id)
//                            }
//                        }
//
//                        override fun isDead() = life <= 0
//                    }

interface IMoving : IGameObject {
    var availableSpeed: Float
    var speed: Float
    var direction: Direction
    fun go(direction: Direction) {
        if (direction == this.direction) {
            this.speed = availableSpeed
            !!!!!!!!!! MAP_ACTION(Motion)
        } else {
            stop()
            rotate(direction)
        }
    }
    fun stop() {
        speed = 0f
    }
    private fun rotate(direction: Direction) {
        this.direction = direction
    }
}

//                    abstract class Moving(override val id: Int, override var speed: Float = 0f, override var direction: Direction) : IMoving {
//                        override fun go(speed: Float) {
//                            this.speed = speed
//                        }
//                        override fun stop() {
//                            speed = 0f
//                        }
//                        override fun rotate(direction: Direction) {
//                            this.direction = direction
//                        }
//                    }

interface ICanHit : IGameObject {
    fun hit(objectId: Int, damage: Int)
}


//
//
//class P(override val id: Int, override var speed: Float = 0f, override var direction: Direction, override var life: Int,
//        override val onDeath: () -> Unit) : IPlayer {
//
//}



interface IPlayer : IDamageable, IMoving {
    // val id: Int
    // val speed: Float
    // var life: Int
    // fun hit(damage: Int)
    // fun isDead(): Boolean
    var weapon: BulletType
}

class Player (override val id: Int) : IPlayer {
    override var speed: Float = 1f
    override var direction = Direction.UP
    override var life: Int = 10
    override var weapon: BulletType = BulletType.SIMPLE

    // скорость не меняем!!!
    // если будет скорость выше 1, то пресечение будет расчитываться на несколько точек, но до стены может остаться например 1 точка
    // и тогда надо будет делать минимальный возможный шаг, это доп сложности в расчете положения объектов

//    fun current(deltaTime: Int) {
//        var deltaCurrentPosition = deltaTime * speed
//        var currentPrecisePosition = (positionByDirection + deltaCurrentPosition + lastPositionError)
//        var currentDiscretePosition = round(currentPrecisePosition)
//        lastPositionError = currentPrecisePosition - currentDiscretePosition
//        resultPosition = currentDiscretePosition
//    }

    override val onDeath: () -> Unit = {

    }

    override fun hit(damage: Int) {
        life = max(0, life - damage)
    }

    override fun isDead() = life == 0
}


/* TODO: Надо хорошо подумать над этим:
    в игре должны обрабатываться тики с какой-то внутренней частотой, типа fps
    объектам задается скорость, исходя из внутренней частоты и реального времени вычислять текущее положение по скорости и
    проецировать на дискретное игровое поле.
    Тогда танки могут медленно ездить а пульки быстро.
    Главное чтоб частота обработки была достаточно быстрой, чтобы никакой объект не смог шагнуть больше чем на одну координату,
    иначе могут быть проблемы с вычислением пересечений объектов, типа пуля пролетает 5 точек, но до стены было 2, она не может за стену залететь.

    Возможно стоит продумать лучше алгоритм пересечения, чем повышать чатсоту, а то тормозить будет на высокой частоте


 */
interface IBulletObject {
    val id: Int
    val speed: Int
    val damage: Int
    val removeBulletObject: (bulletObjectId: Int) -> Unit
    fun hit(objectId: Int): Boolean
}

class BulletObject(override val id: Int,
                   override val speed: Int,
                   override val damage: Int,
                   override val removeBulletObject: (bulletObjectId: Int) -> Unit
) : IBulletObject {
    private var life = 1
    private fun canHitThisObject(objectId: Int): Boolean = true

    override fun hit(objectId: Int): Boolean {
        // TODO: запоминаем в объекте пульки что поразили объект objectId, может быть полезно когда пуля сможет поражать больше одного объекта, тогда этот уже учли и больше на него удар не будет действовать от этой пули
        if (canHitThisObject(objectId)) {
            // урон самой пульке
            hitItself(1)
            return true
        } else {
            return false
        }
    }

    private fun hitItself(value: Int) {
        life -= value
        if (life <= 0) {
            removeBulletObject(id)
        }
    }
}


class GameField {
//    val OrderedByTop = OrderedMapObjects(Position::top)
//    val OrderedByBottom = OrderedMapObjects(Position::bottom)
//    val OrderedByLeft = OrderedMapObjects(Position::left)
//    val OrderedByRight = OrderedMapObjects(Position::right)



    val mapOfOrderedList = mapOf(
        Position::top to OrderedMapObjects(Position::top),
        Position::bottom to OrderedMapObjects(Position::bottom),
        Position::left to OrderedMapObjects(Position::left),
        Position::right to OrderedMapObjects(Position::right)
    )

    fun getObjectById(objectId: Int): IMapObject? {
        return mapOfOrderedList[Position::top]!!.getObjectById(objectId)
    }

    fun addMapObject(mapObject: IMapObject) {
        //TODO
//        OrderedByTop.insert(mapObject)
//        OrderedByBottom.insert(mapObject)
//        OrderedByLeft.insert(mapObject)
//        OrderedByRight.insert(mapObject)
    }

    fun removeMapObject(mapObjectId: Int) {
        //TODO
//        OrderedByTop.remove(mapObjectId)
//        OrderedByBottom.remove(mapObjectId)
//        OrderedByLeft.remove(mapObjectId)
//        OrderedByRight.remove(mapObjectId)
    }

    fun updateMapObject(mapObjectId: Int) {
        //TODO
//        OrderedByTop.update(mapObjectId)
//        OrderedByBottom.update(mapObjectId)
//        OrderedByLeft.update(mapObjectId)
//        OrderedByRight.update(mapObjectId)
    }

//    fun addTank(id: Int) {
//
//    }

    fun createBullet(player: IPlayer, mapObject: IMovableMapObject): IMovableMapObject {
        val bulletType = player.weapon
        val (top, bottom, left, right) = mapObject.position

        val horizontalMiddle: Int = right - (right - left + 1) / 2
        val verticalMiddle: Int = top - (top - bottom + 1) / 2

        val bullet: IMovableMapObject = MovableMapObject(
            getNextId(),
            Bullet(bulletType),
            when (mapObject.direction) {
                Direction.UP -> Position(top + 1, top + 1, horizontalMiddle, horizontalMiddle)
                Direction.DOWN -> Position(bottom - 1, bottom - 1, horizontalMiddle, horizontalMiddle)
                Direction.LEFT -> Position(verticalMiddle, verticalMiddle, left - 1, left - 1)
                Direction.RIGHT -> Position(verticalMiddle, verticalMiddle, right + 1, right + 1)
            },
            mapObject.direction
        )

        return bullet

        // DO NOT ADD BEFORE INTERSECTION CHECKS!!!
        // addMapObject(bullet)

    }

        fun processOtherObjects(deltaTime: Int) {
        // TODO
    }

    fun action(player: IPlayer, actions: List<Action>, deltaTime: Int) {
        val movableMapObjectId = player.id
        /* move action:
            hasToBeRotated
                ? getRotatedPosition -> куда "выросли" габариты, там и проверяем на пересечение с объектами на поле
                    если пересеклись с твердыми, то нельзя повернуться, отменяем, если пересеклись с пулей или бонусом, то обрабатываем как обычно - убиваем или бонус даем
                : обычная обработка шага
           fire action
                создаем пульку добавляя ее через addMapObject


         */

        // val player = players[playerId]

        val subject: MovableMapObject = getObjectById(movableMapObjectId) as MovableMapObject? ?: throw Exception("Doesn't exist")

        for (action in actions) {
            when (action) {
                is Motion -> {
                    val (direction) = action

                    // TODO: Учесть границы игрового поля!!!

                    if (subject.hasToBeRotated(direction)) {
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
                            val rotatedSubject = (subject.clone() as MovableMapObject).apply { rotate(direction) }
                            val previousPosition = subjectEdge.getter.call(subject.position)
                            val newPosition = subjectEdge.getter.call(rotatedSubject.position)
                            val intersectedObjects = mapOfOrderedList[objectEdge]!!.findIntersections(rotatedSubject, previousPosition, newPosition)
                            for (intersectedObject in intersectedObjects) {
                                /*
                                if some is solid -> just return : can not be rotated
                                else:
                                    будет повернут, и т.к. размеры увеличатся по одному направлению, то можно приравнять к
                                     движению в этом направлении -> сдедует проверить столкновение с мягкими штуками как при обычном движении
                                 */
                                if (intersectedObject.type is Wall || intersectedObject.type is Tank) {
                                    return@let Pair(false, null)
                                }
                            }
                            return@let Pair(true, intersectedObjects)
                        }?: Pair(true, null)

                        if (!canBeRotated) return // do nothing

                        if (intersectedObjects == null) {
                            // just rotate
                            subject.rotate(direction)
                            updateMapObject(movableMapObjectId)
                        } else {
                            // обработать как движение, перед вращением, т.к. позиция сдвинулась в каком-то одном направлении
                            // TODO: process as common motion

                            // TODO: вращать или до обрабокти или после
                            subject.rotate(direction)
                            updateMapObject(movableMapObjectId)
                        }
                        // if ok return subject.rotate(direction)
                    } else {
                    // Обработка обычного перемещения

                        val (subjectEdge, objectEdge) = when (direction) {
                            Direction.UP -> Pair(Position::top, Position::bottom)
                            Direction.DOWN -> Pair(Position::bottom, Position::top)
                            Direction.LEFT -> Pair(Position::left, Position::right)
                            Direction.RIGHT -> Pair(Position::right, Position::left)
                        }

                        // val movedSubject = (subject.clone() as MovableMapObject).apply { move(direction, player.speed) }
                        // val trajectory = (subject.clone() as MovableMapObject).apply { getMoveTrajectory(direction, deltaTime, player.speed) }
                        val trajectory = subject.getMoveTrajectory(direction, deltaTime, player.speed)
                        val previousPosition = subjectEdge.getter.call(subject.position)
                        val newPosition = subjectEdge.getter.call(trajectory.position)
                        val intersectedObjects = mapOfOrderedList[objectEdge]!!.findIntersections(trajectory, previousPosition, newPosition)

                        // val canBeMoved = intersectedObjects.none { it.type is Wall || it.type is Tank }
                        //TODO: находим ближайший (первый же) упор об стену или танк (nearestSolidObject), и двигаемся только до него (nearestSolidObject +/- 1)
                        //var nearestSolidObject: IMapObject? = null
                        var nearestSolidEdge: Int? = null
                        var step: Int? = null
                        for (intersectedObject in intersectedObjects) {
                            if (intersectedObject.type is Wall || intersectedObject.type is Tank) {
                                nearestSolidEdge = objectEdge.getter.call(intersectedObject.position)
                                step = nearestSolidEdge - previousPosition
                                subject.move(direction, abs(step))
                                break
                            }
                        }
                        if (nearestSolidEdge == null) {
                            subject.move(direction, deltaTime, player.speed)
                        }

                        for (intersectedObject in intersectedObjects) {
                            //TODO: если нашли ближайший упор, то получается что в траектории по которой искали пересечение
                            // отбрасывается часть куда объект не дошел. Поэтому отбрасываем intersectedObject у которых позиция objectEdge
                            // превышает (если Direction.RIGHT или Direction.UP, а если LEFT\DOWN - то меньше) позицию subjectEdge подвинутого объекта
                            // (т.е. величину nearestSolidObject +/- 1)

                            val currentObjectEdge = objectEdge.getter.call(intersectedObject.position)

                            if (step != null && nearestSolidEdge != null) {
                                if (step.sign * currentObjectEdge >= step.sign * nearestSolidEdge) {
                                    break
                                }
                            }

                            //TODO: intersectedObject попадаемые под требования (выше) и не являющиеся твердыми - взаимодействуют с объектом
                            if (intersectedObject.type is Bullet) {
                                // можно наткнуться на две пули сразу, при этом первая уже может убить, но столкновение со второй
                                // тоже засчитываем, и удаляться обе пули. Потом уже танк будет помечен дохлым
                                player.hit((intersectedObject.type as Bullet).bulletType.damage)
                                removeMapObject(intersectedObject.id)

                                // TODO:
                                /*
                                BulletObject.interactWith(subject)
                                 */
                            }
                            /* TODO: На будущее
                            if (intersectedObject.type is Bonus) {
                                // можно наткнуться на два бонуса
                                player.bonus((intersectedObject.type as Bonus).bonusType.something)
                                removeMapObject(intersectedObject.id)
                            }
                            */
                                                            /*
                                                             = - танк
                                                             0 - стена
                                                             * - пулька
                                                            текущая позиция:
                                                            == 000
                                                             ==
                                                            == *
                                                            хотим шагнуть в право. мешает стена -> не должны нарваться на пульку, но если в intersectedObjects пулька будет первой,
                                                            то рискуем сделать ошибочный вывод, что мы на неё натыкаемся.
                                                            вывод: надо сперва обработать все  intersectedObjects на то твердые ли придметы (стена\танк) и можем ли шагнуть.
                                                            если можем шагнуть, тогда только обрабатывать вторым проходом по intersectedObjects наткнулись ли сами на пульку или бонус.
                                                            то же самое и при повороте - сначала проверяем поменялась ли позиция при повороте (танк не квадратный), а потом проверем на
                                                            наскакивание на другие объекты.

                                                            когда пуля сама налетает на танк, это обработается в ней, а не в танке.

                                                             */


                        }
                        // мертвый танк удалять сразу не будем, он остается на поле еще ход или несколько, и просто является припятствием для других танков

                    }

                }
                Fire -> {
                    val bullet = createBullet(player, subject)
                    BulletObject(bullet.id) { removeMapObject(it) }
                    addMapObject(bullet)
                    /*
                    Лучше создать пульку в общем массиве объектов
                    а потом отдельно после ходов всех танков обработать ходы всех пулек.
                     */
                }
            }
        }
    }
}