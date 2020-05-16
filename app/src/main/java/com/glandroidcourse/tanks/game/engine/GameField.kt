package com.glandroidcourse.tanks.game.engine

import com.glandroidcourse.tanks.game.engine.map.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

var id = 0
fun getNextId() = id++


class GameField {

    val players = mutableListOf<IPlayer>()
    val bullets = mutableListOf<IBullet>()
    val map = Map()


    fun initPlayers(count: Int): List<Int> {
        return List(count) { addPlayer(it.toString()).id }
    }

    private fun addPlayer(name: String): IPlayer {
        val id = getNextId()
        fun removePlayer(player: IPlayer) {
            map.removeMapObject(player.id)
            players.remove(player)
        }
        val player = Player(
            id,
            doFire = { addBullet(it) },
            removePlayer = { removePlayer(it) }
        )
        map.createTankMapObject(id, name, player)
        return player
    }

    private fun addBullet(player: IPlayer): IBullet {
        val tankMapObject: MovableMapObject = map.getObjectById(player.id) as MovableMapObject? ?: throw Exception("Doesn't exist")
        fun removeBullet(bullet: IBullet) {
            map.removeMapObject(bullet.id)
            bullets.remove(bullet)
        }
        val id = getNextId()
        val bullet = Bullet(
            id,
            player.weapon,
            player.direction,
            removeBullet = { removeBullet(it) }
        )
        bullets.add(bullet)
        map.createBulletMapObject(id, player.weapon, bullet, tankMapObject)
        return bullet
    }







    fun nextGameTick(currentTime: Int, deltaTime: Int, actionsByPlayer: Map<Int, List<ControllerAction>>) {
        val players: List<IPlayer> = listOf<IPlayer>()

        for (player in players) {
            val actions = actionsByPlayer[player.id]!!
            val motionAction: ControllerMotion? = actions.find { it is ControllerMotion } as ControllerMotion
            val mapAction: Action? = player.processMotion(currentTime, deltaTime, motionAction)
            if (mapAction != null) {
                // interactions inside!!!
                map.processTankMotion(player.id, mapAction, currentTime)
            }
        }
        for (player in players) {
            val actions = actionsByPlayer[player.id]!!
            val fireAction: ControllerFire? = actions.find { it is ControllerFire } as ControllerFire
            if (fireAction != null) {
                // Просто насоздаем пулек, действий на карте не требуется пока
                player.fire()
            }
        }
        //TODO: двигаем пульки
        for (bullet in bullets) {
            val action: Action? = bullet.go(bullet.direction, deltaTime)
            if (action != null && action is Motion) {
                map.processBulletMotion(bullet.id, action, currentTime)
            }
        }

        for (player in players) {
            player.processDeath(currentTime)
        }
        for (bullet in bullets) {
            bullet.processDeath()
        }

    }


                                            fun processOtherObjects(deltaTime: Int) {
                                                // TODO
                                            }

    //TODO: переделать на один экшен
    fun doPlayerActionOnMap(player: IPlayer, actions: List<Action>, deltaTime: Int) {
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
                                if (intersectedObject.type is WallObject || intersectedObject.type is TankObject) {
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
                            if (intersectedObject.type is WallObject || intersectedObject.type is TankObject) {
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
                            if (intersectedObject.type is BulletObject) {
                                // можно наткнуться на две пули сразу, при этом первая уже может убить, но столкновение со второй
                                // тоже засчитываем, и удаляться обе пули. Потом уже танк будет помечен дохлым
                                player.hit((intersectedObject.type as BulletObject).bulletType.damage)
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
                    BulletObject(bullet.id) {
                        removeMapObject(
                            it
                        )
                    }
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