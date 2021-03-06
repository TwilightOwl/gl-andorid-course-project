package com.glandroidcourse.tanks.game.engine.map

import java.lang.Exception
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KProperty1

class OrderedMapObjects(val orderedBy: KProperty1<Position, Int>) {
    private var objects: MutableList<IMapObject> = mutableListOf()

    private val indexById = mutableMapOf<Int, Int>()

    // Это кастомная функция быстрого поиска, посколько нужно найти первый по порядку элемент в массиве
    // удовлетворяющий условию, а binarySearcBy останавливается на первом попавшемся элементе (бинарный
    // поиск делит по полам текущий сегмент)
    fun <T> searchLowerBound(list: List<T>, value: Int, extract: (T) -> Int ): Int {
        var l = -1
        var r = list.size
        while (l + 1 < r) {
            val m = (l + r) ushr 1
            if (extract(list[m]) >= value) {
                r = m
            } else {
                l = m
            }
        }
        return r
    }

    fun getIndexById(objectId: Int): Int? {
        val index = objects.indexOfFirst { it.id === objectId }
        return if (index == -1) null else index
        // objects.find { it.id === objectId }
    }

    fun getObjectById(objectId: Int): IMapObject? {
        return getIndexById(objectId)?.let { objects[it] }
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
        var foundIndex = searchLowerBound(objects, orderedBy.getter.call(mapObject.position), { orderedBy.getter.call(it.position) })
        indexById.put(mapObject.id, foundIndex)
        objects.add(foundIndex, mapObject)
        if (objects.size != indexById.size) {
            val l = 0
        }
        return foundIndex
    }

    fun findIntersections(mapObject: IMapObject, previousPosition: Int, newPosition: Int): Array<IMapObject> {
        val min = min(previousPosition, newPosition)
        val max = max(previousPosition, newPosition)
        var foundIndex = searchLowerBound(objects, min, { orderedBy.getter.call(it.position) })
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
        // val index = indexById[objectId] ?: return null
        val index = getIndexById(objectId) ?: return null


        val r = objects.removeAt(index)
        indexById.remove(objectId)
        if (objects.size != indexById.size) {
            val l = 0
        }
        return r
    }

    fun update(objectId: Int): Int? {
        //return 1
        val prev = indexById.size
        val r = remove(objectId)?.let{
            if (it == null) {
                val l = 0
            }
            insert(it)
        }
        if (prev != indexById.size || objects.size != indexById.size) {
            val f = 0
        }
        return r
    }

}