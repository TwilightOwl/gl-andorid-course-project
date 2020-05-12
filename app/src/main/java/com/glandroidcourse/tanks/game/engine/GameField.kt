package com.glandroidcourse.tanks.game.engine

import kotlin.math.max

var id = 0
fun getNextId() = id++

interface IPlayer {
    val id: Int
    val speed: Int
    var life: Int
    fun hit(damage: Int)
    fun isDead(): Boolean
    var weapon: BulletType
}

class Player (override val id: Int) : IPlayer {
    override val speed: Int = 1
    override var life: Int = 10
    override var weapon: BulletType = BulletType.SIMPLE
    // скорость не меняем!!!
    // если будет скорость выше 1, то пресечение будет расчитываться на несколько точек, но до стены может остаться например 1 точка
    // и тогда надо будет делать минимальный возможный шаг, это доп сложности в расчете положения объектов

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



class GameField {
//    val OrderedByTop = OrderedGameObjects(Position::top)
//    val OrderedByBottom = OrderedGameObjects(Position::bottom)
//    val OrderedByLeft = OrderedGameObjects(Position::left)
//    val OrderedByRight = OrderedGameObjects(Position::right)

    val mapOfOrderedList = mapOf(
        Position::top to OrderedGameObjects(Position::top),
        Position::bottom to OrderedGameObjects(Position::bottom),
        Position::left to OrderedGameObjects(Position::left),
        Position::right to OrderedGameObjects(Position::right)
    )

    fun getObjectById(objectId: Int): IGameObject? {
        return mapOfOrderedList[Position::top]!!.getObjectById(objectId)
    }

    fun addGameObject(gameObject: IGameObject) {
        //TODO
//        OrderedByTop.insert(gameObject)
//        OrderedByBottom.insert(gameObject)
//        OrderedByLeft.insert(gameObject)
//        OrderedByRight.insert(gameObject)
    }

    fun removeGameObject(gameObjectId: Int) {
        //TODO
//        OrderedByTop.remove(gameObjectId)
//        OrderedByBottom.remove(gameObjectId)
//        OrderedByLeft.remove(gameObjectId)
//        OrderedByRight.remove(gameObjectId)
    }

    fun updateGameObject(gameObjectId: Int) {
        //TODO
//        OrderedByTop.update(gameObjectId)
//        OrderedByBottom.update(gameObjectId)
//        OrderedByLeft.update(gameObjectId)
//        OrderedByRight.update(gameObjectId)
    }

//    fun addTank(id: Int) {
//
//    }

    fun createBullet(player: IPlayer, gameObject: IMovableGameObject): IMovableGameObject {
        val bulletType = player.weapon
        val (top, bottom, left, right) = gameObject.position

        val horizontalMiddle: Int = right - (right - left + 1) / 2
        val verticalMiddle: Int = top - (top - bottom + 1) / 2

        val bullet: IMovableGameObject = MovableGameObject(
            getNextId(),
            Bullet(bulletType),
            when (gameObject.direction) {
                Direction.UP -> Position(top + 1, top + 1, horizontalMiddle, horizontalMiddle)
                Direction.DOWN -> Position(bottom - 1, bottom - 1, horizontalMiddle, horizontalMiddle)
                Direction.LEFT -> Position(verticalMiddle, verticalMiddle, left - 1, left - 1)
                Direction.RIGHT -> Position(verticalMiddle, verticalMiddle, right + 1, right + 1)
            },
            gameObject.direction
        )

        return bullet

        // DO NOT ADD BEFORE INTERSECTION CHECKS!!!
        // addGameObject(bullet)

    }

    fun action(movableGameObjectId: Int, player: IPlayer, actions: List<Action>) {

        /* move action:
            hasToBeRotated
                ? getRotatedPosition -> куда "выросли" габариты, там и проверяем на пересечение с объектами на поле
                    если пересеклись с твердыми, то нельзя повернуться, отменяем, если пересеклись с пулей или бонусом, то обрабатываем как обычно - убиваем или бонус даем
                : обычная обработка шага
           fire action
                создаем пульку добавляя ее через addGameObject


         */

        // val player = players[playerId]

        val subject: MovableGameObject = getObjectById(movableGameObjectId) as MovableGameObject? ?: throw Exception("Doesn't exist")

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
                            val rotatedSubject = (subject.clone() as MovableGameObject).apply { rotate(direction) }
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
                            updateGameObject(movableGameObjectId)
                        } else {
                            // обработать как движение, перед вращением, т.к. позиция сдвинулась в каком-то одном направлении
                            // TODO: process as common motion

                            // TODO: вращать или до обрабокти или после
                            subject.rotate(direction)
                            updateGameObject(movableGameObjectId)
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

                        val movedSubject = (subject.clone() as MovableGameObject).apply { move(direction, player.speed) }
                        val previousPosition = subjectEdge.getter.call(subject.position)
                        val newPosition = subjectEdge.getter.call(movedSubject.position)
                        val intersectedObjects = mapOfOrderedList[objectEdge]!!.findIntersections(movedSubject, previousPosition, newPosition)
                        val canBeMoved = intersectedObjects.none { it.type is Wall || it.type is Tank }
                        if (!canBeMoved) return // т.к. speed всегда 1, то не может оказаться между стеной и текущей позицией других
                        // элементов (пулек например), и можно ничего не делать в данном случае

                        for (intersectedObject in intersectedObjects) {
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
                            /*
                            if some is solid -> just return : can not be moved
                            else:
                                TODO: сдедует проверить столкновение с мягкими штуками как при обычном движении
                             */
                            if (intersectedObject.type is Bullet) {
                                // можно наткнуться на две пули сразу, при этом первая уже может убить, но столкновение со второй
                                // тоже засчитываем, и удаляться обе пули. Потом уже танк будет помечен дохлым
                                player.hit((intersectedObject.type as Bullet).bulletType.damage)
                                removeGameObject(intersectedObject.id)
                            }
                            /* TODO: На будущее
                            if (intersectedObject.type is Bonus) {
                                // можно наткнуться на два бонуса
                                player.bonus((intersectedObject.type as Bonus).bonusType.something)
                                removeGameObject(intersectedObject.id)
                            }
                            */
                        }
                        // мертвый танк удалять сразу не будем, он остается на поле еще ход или несколько, и просто является припятствием для других танков

                    }

                }
                Fire -> {
                    val bullet = createBullet(player, subject)
                        TODO!!!
                    /*
                    Лучше создать пульку в общем массиве объектов
                    а потом отдельно после ходов всех танков обработать ходы всех пулек.
                     */
                }
            }
        }
    }
}