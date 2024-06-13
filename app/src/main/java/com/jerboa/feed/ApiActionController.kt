package com.jerboa.feed

import com.jerboa.api.ApiAction

class ApiActionController<T>(
    val idSelect: (T) -> Long,
) {
    private val controller = FeedController<ApiAction<T>>()

    val feed: List<ApiAction<T>>
        get() = controller.feed

    fun init(newItems: List<T>) {
        controller.init(newItems.map { ApiAction.Ok(it) })
    }

    fun setLoading(item: T) {
        controller.safeUpdate({ items ->
            items.indexOfFirst { idSelect(it.data) == idSelect(item) }
        }) { ApiAction.Loading(it.data) }
    }

    fun setOk(item: T) {
        controller.safeUpdate({ items ->
            items.indexOfFirst { idSelect(it.data) == idSelect(item) }
        }) { ApiAction.Ok(it.data) }
    }

    fun setFailed(
        item: T,
        error: Throwable,
    ) {
        controller.safeUpdate({ items ->
            items.indexOfFirst { idSelect(it.data) == idSelect(item) }
        }) { ApiAction.Failed(it.data, error) }
    }

    fun removeItem(item: T) {
        val index = feed.indexOfFirst { idSelect(it.data) == idSelect(item) }
        controller.removeAt(index)
    }
}
