package com.jerboa.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jerboa.feat.PostNavigationGestureMode

@Composable
fun SwipeToNavigateBack(
    useSwipeBack: PostNavigationGestureMode,
    onSwipeBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (useSwipeBack == PostNavigationGestureMode.SwipeRight) {
        val swipeState = rememberSwipeToDismissBoxState()

        when (swipeState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> {
                onSwipeBack()
            }

            else -> {
            }
        }

        SwipeToDismissBox(
            state = swipeState,
            enableDismissFromEndToStart = false,
            backgroundContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                )
            },
        ) {
            content()
        }
    } else {
        content()
    }
}
