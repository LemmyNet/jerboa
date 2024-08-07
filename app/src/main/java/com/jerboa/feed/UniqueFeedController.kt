package com.jerboa.feed

import it.vercruysse.lemmyapi.Identity

open class UniqueFeedController<T : Identity> : FeedController<T>() {
    private val ids = mutableSetOf<Long>()

    override fun add(item: T): Boolean {
        if (ids.add(item.id)) {
            items.add(item)
            return true
        }
        return false
    }

    override fun addAll(newItems: List<T>) {
        newItems.forEach {
            if (ids.add(it.id)) {
                items.add(it)
            }
        }
    }

    override fun clear() {
        super.clear()
        ids.clear()
    }

    override fun remove(item: T): Boolean {
        ids.remove(item.id)
        return super.remove(item)
    }
}
