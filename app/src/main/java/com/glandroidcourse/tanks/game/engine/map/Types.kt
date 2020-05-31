package com.glandroidcourse.tanks.game.engine.map

/*
    Код в Map описывает объекты на карте. Эти объекты имеют физические характеристики:
    - границы (left, right, top, bottom)
    - направление direction
    Объекты могут быть "твердыми" и "нетвердыми"
    Объекты можно перемещать и вращать. При этих действиях учитывается можно ли пройти через объект или нет (твердый или нет)
    Также объекты умеют обрабатывать ситуации, когда один объект взаимодействует с другим (когда их контуры пересекаются).
    При взаимодествии, объекты сообщают об этом своим соответствующим игровым объектам (Player\Bullet\Wall\Bonus) и те
        обрабатывают взаимодействия должным образом
    Для простоты за контур мы считаем описанный вокруг объекта прямоугольник.
    Движения объекта рассчитываются в игровом движке исходя из скорости и времени прошедшем с прошлой итерации, в Map прилетает
        вещественная координата, но её нужно замапить на дискретную карту, поэтому сохраняем ошибку в lastStepError для учета на следующем шаге.

    OrderedMapObjects хранит отсортированный список всех объектов на карте по какой-то одной границе (left/top/right/bottom)
    При изменениях в объектах этот список нужно актуализировать.
    Работа со списком происходит быстро за счет бинарного поиска, это позволяет иметь огромную карту из действительно большого количества объектов,
        не теряя в производительности алгоритма определения взаимодействий объектов.
 */

enum class Direction { UP, DOWN, LEFT, RIGHT }

data class Position(var top: Int, var bottom: Int, var left: Int, var right: Int)

sealed class BaseObjectType
data class TankObject(val name: String) : BaseObjectType() {
    //val width = 3
    //val height = 4
}
data class BulletObject(val bulletType: BulletType) : BaseObjectType()
data class WallObject(val wallType: WallType) : BaseObjectType()
data class BonusObject(val bonusType: BonusType) : BaseObjectType()

enum class WallType(val solidity: Int) {
    SOLID(1000),
    DESTROYABLE(1),
    STRONG(2)
}

// Пули должны быть быстрее чем танки!!!
enum class BulletType(val speed: Float, val power: Int) {
    SIMPLE(3f, 1),
    HEAVY(3f, 2),
    FAST(5f, 1)
}

enum class BonusType(val extraLife: Int?, val bulletType: BulletType?, val speed: Float? ) {
    LIFE_EXTRA(3, null, null),
    WEAPON_HEAVY(null, BulletType.HEAVY, null),
    WEAPON_FAST(null, BulletType.FAST, null),
    SPEED_FAST(null, null, 2f)
}

sealed class Action
data class Motion(val direction: Direction, val step: Float) : Action()
data class Rotation(val direction: Direction) : Action()
object Fire : Action()

interface IInteractable {
    fun interactWith(interactableObject: IInteractable, currentTime: Long)
}