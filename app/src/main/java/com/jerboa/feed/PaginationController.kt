package com.jerboa.feed

import it.vercruysse.lemmyapi.v0x19.datatypes.PaginationCursor

class PaginationController(
    var page: Long = 1,
    var pageCursor: PaginationCursor? = null,
) {
    fun reset() {
        page = 1
        pageCursor = null
    }

    fun nextPage(pageCursor: PaginationCursor?){
        page++
        this.pageCursor = pageCursor
    }
}