package com.jerboa.ui.components.common

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.jerboa.feat.SwipeToActionPreset
import com.jerboa.feat.SwipeToActionType
import com.jerboa.feat.SwipeToActionType.Companion.START_THRESHOLD

@Composable
@ExperimentalMaterial3Api
fun SwipeToAction(
    swipeToActionPreset: SwipeToActionPreset,
    enableDownVotes: Boolean,
    swipeableContent: @Composable RowScope.() -> Unit,
    swipeState: SwipeToDismissBoxState,
) {
    val haptic = LocalHapticFeedback.current

    val leftActionsRanges =
        remember(swipeToActionPreset, enableDownVotes) {
            SwipeToActionType.getActionToRangeList(
                swipeToActionPreset.leftActions
                    .filter { !(it == SwipeToActionType.Downvote && !enableDownVotes) },
            )
        }
    val rightActionsRanges =
        remember(swipeToActionPreset, enableDownVotes) {
            SwipeToActionType.getActionToRangeList(
                swipeToActionPreset.rightActions
                    .filter { !(it == SwipeToActionType.Downvote && !enableDownVotes) },
            )
        }

    fun actionByState(state: SwipeToDismissBoxState): Pair<OpenEndRange<Float>, SwipeToActionType>? =
        when (state.targetValue) {
            SwipeToDismissBoxValue.StartToEnd -> {
                leftActionsRanges.findLast { swipeState.progress in it.first }
            }

            SwipeToDismissBoxValue.EndToStart -> {
                rightActionsRanges.findLast { swipeState.progress in it.first }
            }

            else -> null
        }

    val swipeAction =
        remember(swipeState.progress, swipeState.targetValue) { actionByState(swipeState) }

    SwipeToDismissBox(
        enableDismissFromStartToEnd = leftActionsRanges.isNotEmpty(),
        enableDismissFromEndToStart = rightActionsRanges.isNotEmpty(),
        state = swipeState,
        backgroundContent = {
            val lastSwipeAction = remember { mutableStateOf<SwipeToActionType?>(null) }
            val transition = updateTransition(swipeState, label = "swipe state")
            val color by transition.animateColor(
                transitionSpec = {
                    val currentAction = actionByState(this.targetState)
                    // vibrates when icon changes
                    if (lastSwipeAction.value != currentAction?.second) {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        lastSwipeAction.value = currentAction?.second
                    }
                    spring(stiffness = 2000F)
                },
                label = "swipe color animation",
                targetValueByState = { state ->
                    val currentAction = actionByState(state)
                    currentAction?.second?.getActionColor()?.background ?: Color.Transparent
                },
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxSize(),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth(if (swipeState.progress != 1f) swipeState.progress else 0f)
                            .fillMaxHeight()
                            .background(color = color)
                            .align(
                                if (swipeState.targetValue == SwipeToDismissBoxValue.EndToStart) Alignment.TopEnd else Alignment.TopStart,
                            ),
                    contentAlignment =
                        if (swipeState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                            Alignment.CenterStart
                        } else {
                            Alignment.CenterEnd
                        },
                ) {
                    val tint = swipeAction?.second?.getActionColor()?.tint ?: Color.Transparent
                    val modifier =
                        Modifier
                            .padding(10.dp)
                            .requiredWidth(35.dp)
                            .fillMaxHeight()

                    swipeAction?.second?.getImageVector()?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tint,
                            modifier = modifier,
                        )
                    }
                }
            }
        },
        content = { swipeableContent() },
    )
}

@Composable
fun rememberSwipeActionState(
    swipeToActionPreset: SwipeToActionPreset,
    enableDownVotes: Boolean,
    rememberKey: Any? = Unit,
    onAction: (action: SwipeToActionType) -> Unit,
): SwipeToDismissBoxState {
    /*
    This hacky solution is required because confirmValueChange lambda doesn't pass progress state
    They didn't fix it with new SwipeToDismissBoxState
     */
    val density = LocalDensity.current

    val leftActionsRanges =
        remember(swipeToActionPreset, enableDownVotes) {
            SwipeToActionType.getActionToRangeList(
                swipeToActionPreset.leftActions
                    .filter { !(it == SwipeToActionType.Downvote && !enableDownVotes) },
            )
        }
    val rightActionsRanges =
        remember(swipeToActionPreset, enableDownVotes) {
            SwipeToActionType.getActionToRangeList(
                swipeToActionPreset.rightActions
                    .filter { !(it == SwipeToActionType.Downvote && !enableDownVotes) },
            )
        }

    val progressState = remember { mutableFloatStateOf(1.0f) }

    val confirmValueChange: (SwipeToDismissBoxValue) -> Boolean = { dismissValue ->
        val action =
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    leftActionsRanges.findLast { progressState.floatValue in it.first }
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    rightActionsRanges.findLast { progressState.floatValue in it.first }
                }

                else -> {
                    null
                }
            }
        action?.second?.let { actionType ->
            onAction(actionType)
        }
        false // do not dismiss
    }

    val positionalThreshold: (totalDistance: Float) -> Float = { totalDistance -> totalDistance * START_THRESHOLD }

    val dismissState = rememberSaveable(
        saver = SwipeToDismissBoxState.Saver(
            confirmValueChange = confirmValueChange,
            density = density,
            positionalThreshold = { totalDistance -> totalDistance * START_THRESHOLD },
        ),
        inputs = arrayOf(rememberKey),
    ) {
        SwipeToDismissBoxState(SwipeToDismissBoxValue.Settled, density, confirmValueChange, positionalThreshold)
    }
    progressState.floatValue = dismissState.progress
    return dismissState
}
