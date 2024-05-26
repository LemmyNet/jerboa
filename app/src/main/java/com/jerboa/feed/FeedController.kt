package com.jerboa.feed

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

open class FeedController<T> {

    protected val items = mutableStateListOf<T>()

    val feed: SnapshotStateList<T> = items


    fun updateALl(
        selector: (List<T>) -> List<Int>,
        transformer: (T) -> T,
    ) {
        selector(items).forEach {
            update(it, transformer)
        }
    }

    fun update(
        index: Int,
        transformer: (T) -> T,
    ) {
        update(index, transformer(items[index]))
    }

    fun update(
        selector: (List<T>) -> Int,
        transformer: (T) -> T,
    ) {
        update(selector(items), transformer)
    }

    /**
     * Update the item at the given index with the new item.
     *
     * If given -1 or an index that is out of bounds, the update will not be performed.
     * It assumes that the item couldn't be found because the list has changed.
     * Example: a network request to update an item succeeded after the list has changed.
     * So, we ignore it
     */
    fun update(index: Int, new: T) {
        if (index >= 0 && index < items.size) {
            items[index] = new
        } else {
            Log.d("FeedController", "OoB item not updated $new")
        }
    }

    fun add(item: T) {
        items.add(item)
    }

    fun remove(item: T) {
        items.remove(item)
    }

    fun clear() {
        items.clear()
    }

    fun addAll(newItems: List<T>) {
        items.addAll(newItems)
    }

    protected inline fun <E> Iterable<E>.indexesOf(predicate: (E) -> Boolean)
            = mapIndexedNotNull{ index, elem -> index.takeIf{ predicate(elem) } }

}