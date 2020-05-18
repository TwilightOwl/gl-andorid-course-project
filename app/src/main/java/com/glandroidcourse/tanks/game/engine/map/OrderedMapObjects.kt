package com.glandroidcourse.tanks.game.engine.map

import java.lang.Exception
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KProperty1

class OrderedMapObjects(val orderedBy: KProperty1<Position, Int>) {
    private var objects: MutableList<IMapObject> = mutableListOf()

    private val indexById = mutableMapOf<Int, Int>()

    fun getObjectById(objectId: Int): IMapObject? {
        return indexById[objectId]?.let { objects[it] }
    }

    private fun getFoundIndices(index: Int, previousValue: Int): Array<Int> {
        var result = arrayOf<Int>(index)
        for (i in index+1..objects.size-1) {
            if (orderedBy.getter.call(objects[i].position) <= previousValue) result += i
            else break
        }
        return result
    }

    fun insert(mapObject: IMapObject): Int {
        var foundIndex = objects.binarySearchBy(orderedBy.getter.call(mapObject.position)) { orderedBy.getter.call(it.position) }
        if (foundIndex < 0) foundIndex = -(foundIndex + 1)
        objects.add(foundIndex, mapObject)
        indexById.put(mapObject.id, foundIndex)
        return foundIndex
    }

    fun findIntersections(mapObject: IMapObject, previousPosition: Int, newPosition: Int): Array<IMapObject> {
        val min = min(previousPosition, newPosition)
        val max = max(previousPosition, newPosition)
        var foundIndex = objects.binarySearchBy(min) { orderedBy.getter.call(it.position) }
        if (foundIndex < 0) foundIndex = -(foundIndex + 1)
        var indicesToCheckIntersectionWith = getFoundIndices(foundIndex, max)
        if (newPosition < previousPosition) indicesToCheckIntersectionWith.reverse()
        var result = arrayOf<IMapObject>()
        for (index in indicesToCheckIntersectionWith) {
            try {
                if (objects[index].intersectWith(mapObject)) {
                    result += objects[index]
                }
            } catch (error: Exception) {
                // Может выскочить ожидаемая ошибка на пустом массиве objects, никак не обрабатывать
            }
        }
        return result
    }

    fun remove(objectId: Int): IMapObject? {
        val index = indexById[objectId] ?: return null
        indexById.remove(objectId)
        return objects.removeAt(index)
    }

    fun update(objectId: Int): Int? {
        return remove(objectId)?.let{ insert(it) }
    }

}