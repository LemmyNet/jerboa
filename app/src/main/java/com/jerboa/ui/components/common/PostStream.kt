package com.jerboa.ui.components.common

import com.jerboa.datatypes.types.PostId
import com.jerboa.db.entity.Account

interface PostStream {
    fun getNextPost(current: PostId?, account: Account? = null): PostId?
    fun getPreviousPost(current: PostId?, account: Account? = null): PostId?
    fun isFetchingMore(): Boolean
}
