package com.jerboa.feed

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import it.vercruysse.lemmyapi.datatypes.PaginationCursor
import java.util.Stack

class PaginationController {

    val previousPageCursors = Stack<PaginationCursor?>()
    var currentPageCursor: PaginationCursor? = null
    var nextPageCursor: PaginationCursor? = null
    var page by mutableLongStateOf(1)

    fun appendPage(nextPage: PaginationCursor?) {
        page++
        this.nextPageCursor = nextPage
    }

    fun reset() {
        page = 1
        currentPageCursor = null
        nextPageCursor = null
        previousPageCursors.clear()
    }

    fun nextPage(nextPage: PaginationCursor?) {
        page++
        previousPageCursors.push(this.currentPageCursor)
        this.currentPageCursor = this.nextPageCursor
        this.nextPageCursor = nextPage
    }

    fun canMoveToPreviousPage(): Boolean = page > 1 && previousPageCursors.isNotEmpty()
}