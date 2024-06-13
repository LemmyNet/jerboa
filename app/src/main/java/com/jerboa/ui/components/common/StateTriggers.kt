package com.jerboa.ui.components.common

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.jerboa.isScrolledToEnd

@Composable
fun TriggerWhenReachingEnd(
    listState: LazyListState,
    showPostAppendRetry: Boolean,
    loadMorePosts: () -> Unit,
) {
    // observer when reached end of list
    val endOfListReached by remember {
        derivedStateOf {
            listState.isScrolledToEnd()
        }
    }

    // Act when end of list reached
    if (endOfListReached && !showPostAppendRetry) {
        LaunchedEffect(Unit) {
            loadMorePosts()
        }
    }
}
