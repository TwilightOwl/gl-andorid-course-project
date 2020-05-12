package com.glandroidcourse.tanks.game.engine

import java.lang.Exception
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KProperty1

class OrderedGameObjects(val orderedBy: KProperty1<Position, Int>) {
    private var objects: MutableList<IGameObject> = mutableListOf(
//        GameObject(1, Position(1,  0,0, 0)),
//        GameObject(2, Position(5,  0,0, 0)),
//        GameObject(3, Position(10,  0,0, 0)),
//        GameObject(4, Position(15,  0,0, 0)),
//        GameObject(5, Position(20,  0,0, 0))
    )

    private val indexById = mutableMapOf<Int, Int>()

    fun getObjectById(objectId: Int): IGameObject? {
        return indexById[objectId]?.let { objects[it] }
    }

    private fun getFoundIndices(index: Int, previousValue: Int): Array<Int> {
        var result = arrayOf<Int>(index)
        for (i in index+1..objects.size-1) {
            // if (orderedBy.getter.call(objects[i].position) == foundValue) result += i
            if (orderedBy.getter.call(objects[i].position) <= previousValue) result += i
            else break
        }
        return result
    }

    fun insert(gameObject: IGameObject): Int {
        // for (i in objects.in)
        var foundIndex = objects.binarySearchBy(orderedBy.getter.call(gameObject.position)) { orderedBy.getter.call(it.position) }
        if (foundIndex < 0) foundIndex = -(foundIndex + 1)
        objects.add(foundIndex, gameObject)
        indexById.put(gameObject.id, foundIndex)
        return foundIndex
    }

    // left movement - left moving edge find right of other objects
    //
    fun findIntersections(gameObject: IGameObject, previousPosition: Int, newPosition: Int): Array<IGameObject> {
        /*
        this ordered is by right edges. object is moving to left. newPosition new left edge, prevPos - old left edge

        min = min(prev, new)
        max = max(prev, new)

        ind = find( by min )
        ind to positive
        indicesToCheckIntersectWith = getFoundIndices(ind, max)  .reverse() if new < prev (to left or to down)
         */

        val min = min(previousPosition, newPosition)
        val max = max(previousPosition, newPosition)
        var foundIndex = objects.binarySearchBy(min) { orderedBy.getter.call(it.position) }
        if (foundIndex < 0) foundIndex = -(foundIndex + 1)
        var indicesToCheckIntersectionWith = getFoundIndices(foundIndex, max)
        if (newPosition < previousPosition) indicesToCheckIntersectionWith.reverse()
        var result = arrayOf<IGameObject>()
        for (index in indicesToCheckIntersectionWith) {
            try {
                if (objects[index].intersectWith(gameObject)) {
                    result += objects[index]
                }
            } catch (error: Exception) {
                // Может выскочить ожидаемая ошибка на пустом массиве objects, никак не обрабатывать
            }
        }
        return result
    }

    fun remove(objectId: Int): IGameObject? {
        val index = indexById[objectId] ?: return null
        indexById.remove(objectId)
        return objects.removeAt(index)
    }

    fun update(objectId: Int): Int? {
        return remove(objectId)?.let{ insert(it) }
    }

}