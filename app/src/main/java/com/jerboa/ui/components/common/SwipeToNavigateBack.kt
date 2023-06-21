package com.jerboa.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.navigation.NavController
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.offset
import androidx.compose.material.FractionalThreshold
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToNavigateBack(
    navController: NavController,
    content: @Composable () -> Unit,
) {
    val swipeableState = rememberSwipeableState(0)
    val screenWidthPx = with(LocalDensity.current) { LocalConfiguration.current.screenWidthDp.dp.toPx() }

    if (swipeableState.isAnimationRunning) {
        DisposableEffect(Unit) {
            onDispose {
                if (swipeableState.currentValue == 1) {
                    navController.navigateUp();
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .background(Color.Transparent)
            .fillMaxSize()
            .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
            .swipeable(
                state = swipeableState,
                anchors = mapOf(0f to 0, screenWidthPx to 1),
                thresholds = { _, _ -> FractionalThreshold(0.9f) },
                orientation = Orientation.Horizontal,
            )
    ) {
        content()
    }
}
