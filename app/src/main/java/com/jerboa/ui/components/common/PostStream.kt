package com.jerboa.ui.components.common

import com.jerboa.datatypes.types.PostId
import com.jerboa.db.entity.Account

interface PostStream {
    fun getNextPost(current: PostId?, account: Account): PostId?
    fun getPreviousPost(current: PostId?, account: Account): PostId?
    fun isFetchingMore(): Boolean

    companion object {
        /**
         * The minimum number of posts to pre-load in a PostStream to prevent
         * the user running out of posts during a swipe left.
         */
        const val POST_BUFFER_COUNT = 5
    }
}
