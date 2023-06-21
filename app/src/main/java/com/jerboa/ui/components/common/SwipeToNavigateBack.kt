package com.jerboa.ui.components.common

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.math.roundToInt

@Composable
fun SwipeToNavigateBack(
    useSwipeBack: Boolean = false,
    navController: NavController,
    content: @Composable () -> Unit
) {
    if (useSwipeBack) {
        val offsetX = remember { mutableStateOf(0f) }
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        if (dragAmount.x > 0) {
                            offsetX.value = dragAmount.x
                        }
                    }
                }
        ) {
            content()

            val swipeThreshold: Dp = (screenWidth.value * 0.8).dp
            if (offsetX.value > swipeThreshold.value) {
                navController.navigateUp()
            }
        }
    } else {
        content()
    }
}
