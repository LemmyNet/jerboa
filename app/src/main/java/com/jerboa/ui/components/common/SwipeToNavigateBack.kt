package com.jerboa.ui.components.common

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SwipeToNavigateBack(
    useSwipeBack: Boolean = false,
    navController: NavController,
    content: @Composable () -> Unit
) {
    if (useSwipeBack) {
        val startX = remember { mutableStateOf(0f) }
        val screenWidth = LocalConfiguration.current.screenWidthDp.dp

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        if (change.position.x == 0f) {
                            startX.value = change.position.x
                        }

                        val deltaX = change.position.x - startX.value
                        val swipeThreshold: Dp = (screenWidth.value * 0.7).dp
                        when {
                            deltaX >= swipeThreshold.value -> {
                                navController.navigateUp()
                            }
                        }
                    }
                }
        ) {
            content()
        }
    } else {
        content()
    }
}
