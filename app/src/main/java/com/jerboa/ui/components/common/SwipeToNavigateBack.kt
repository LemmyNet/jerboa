package com.jerboa.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jerboa.feat.PostNavigationGestureMode

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToNavigateBack(
    useSwipeBack: Int,
    onSwipeBack: () -> Unit,
    content: @Composable () -> Unit,
) {
    if (useSwipeBack == PostNavigationGestureMode.SwipeLeft.ordinal) {
        val dismissState = rememberDismissState(
            confirmStateChange = {
                when (it) {
                    DismissValue.DismissedToEnd -> {
                        onSwipeBack()
                        true
                    }

                    else -> {
                        false
                    }
                }
            },
        )
        SwipeToDismiss(
            state = dismissState,
            background = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                )
            },
            dismissContent = {
                content()
            },
            directions = setOf(DismissDirection.StartToEnd),
            dismissThresholds = { FractionalThreshold(0.8f) },
        )
    } else {
        content()
    }
}
