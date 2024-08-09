package com.jerboa.feed

import android.util.Log
import androidx.compose.runtime.mutableStateListOf

open class FeedController<T> {
    protected val items = mutableStateListOf<T>()

    val feed: List<T> = items

    open fun updateAll(
        selector: (List<T>) -> List<Int>,
        transformer: (T) -> T,
    ) {
        selector(items).forEach {
            safeUpdate(it, transformer)
        }
    }

    open fun safeUpdate(
        index: Int,
        transformer: (T) -> T,
    ) {
        if (!isValidIndex(index)) {
            Log.d("FeedController", "OoB item not updated $index")
            return
        }

        safeUpdate(index, transformer(items[index]))
    }

    open fun safeUpdate(
        selector: (List<T>) -> Int,
        transformer: (T) -> T,
    ) {
        safeUpdate(selector(items), transformer)
    }

    /**
     * Update the item at the given index with the new item.
     *
     * If given -1 or an index that is out of bounds, the update will not be performed.
     * It assumes that the item couldn't be found because the list has changed.
     * Example: a network request to update an item succeeded after the list has changed.
     * So, we ignore it
     */
    open fun safeUpdate(
        index: Int,
        new: T,
    ) {
        if (isValidIndex(index)) {
            items[index] = new
        } else {
            Log.d("FeedController", "OoB item not updated $new")
        }
    }

    open fun init(newItems: List<T>) {
        clear()
        addAll(newItems)
    }

    open fun get(index: Int): T? = items.getOrNull(index)

    open fun add(item: T) = items.add(item)

    open fun remove(item: T) = items.remove(item)

    open fun removeAt(index: Int) {
        if (isValidIndex(index)) {
            items.removeAt(index)
        }
    }

    open fun clear() = items.clear()

    open fun addAll(newItems: List<T>) {
        items.addAll(newItems)
    }

    protected inline fun <E> Iterable<E>.indexesOf(predicate: (E) -> Boolean) =
        mapIndexedNotNull { index, elem ->
            index.takeIf {
                predicate(elem)
            }
        }

    private fun isValidIndex(index: Int) = index >= 0 && index < items.size
}
