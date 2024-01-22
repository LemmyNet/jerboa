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
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.jerboa.feat.SwipeToActionType

@Composable
@ExperimentalMaterial3Api
fun SwipeToAction(
    leftActions: List<SwipeToActionType>,
    rightActions: List<SwipeToActionType>,
    swipeableContent: @Composable RowScope.() -> Unit,
    swipeState: DismissState,
) {
    val haptic = LocalHapticFeedback.current

    val leftActionsRanges =
        remember(leftActions) { SwipeToActionType.getActionToRangeList(leftActions) }
    val rightActionsRanges =
        remember(rightActions) { SwipeToActionType.getActionToRangeList(rightActions) }

    fun actionByState(state: DismissState): Pair<OpenEndRange<Float>, SwipeToActionType>? {
        return when (state.targetValue) {
            DismissValue.DismissedToEnd -> {
                leftActionsRanges.findLast { swipeState.progress in it.first }
            }

            DismissValue.DismissedToStart -> {
                rightActionsRanges.findLast { swipeState.progress in it.first }
            }

            else -> null
        }
    }

    val swipeAction =
        remember(swipeState.progress, swipeState.targetValue) { actionByState(swipeState) }

    SwipeToDismiss(
        directions =
            remember(leftActions, rightActions) {
                setOfNotNull(
                    if (leftActions.isNotEmpty()) DismissDirection.StartToEnd else null,
                    if (rightActions.isNotEmpty()) DismissDirection.EndToStart else null,
                )
            },
        state = swipeState,
        background = {
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
                    spring(stiffness = 1000f)
                },
                label = "swipe color animation",
                targetValueByState = { state ->
                    val currentAction = actionByState(state)
                    currentAction?.second?.getActionColor() ?: Color.Transparent
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
                            .align(if (swipeState.targetValue == DismissValue.DismissedToStart) Alignment.TopEnd else Alignment.TopStart),
                    contentAlignment = if (swipeState.targetValue == DismissValue.DismissedToStart) Alignment.CenterStart else Alignment.CenterEnd,
                ) {
                    val tint = Color.White
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
        dismissContent = { swipeableContent() },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSwipeActionState(
    leftActions: List<SwipeToActionType>,
    rightActions: List<SwipeToActionType>,
    onAction: (action: SwipeToActionType) -> Unit,
): DismissState {
    /*
    This hacky solution is required because confirmValueChange lambda doesn't pass progress state
     */
    val leftActionsRanges =
        remember(leftActions) { SwipeToActionType.getActionToRangeList(leftActions) }
    val rightActionsRanges =
        remember(rightActions) { SwipeToActionType.getActionToRangeList(rightActions) }
    val progressState = remember { mutableFloatStateOf(1.0f) }
    val dismissState =
        rememberDismissState(
            positionalThreshold = { 0f },
            confirmValueChange = { dismissValue ->
                val action =
                    when (dismissValue) {
                        DismissValue.DismissedToEnd -> {
                            leftActionsRanges.findLast { progressState.floatValue in it.first }
                        }

                        DismissValue.DismissedToStart -> {
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
            },
        )
    progressState.floatValue = dismissState.progress
    return dismissState
}
