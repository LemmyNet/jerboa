package com.jerboa.util.cascade.internal

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.LayoutDirection.Ltr
import com.jerboa.util.cascade.BackStackSnapshot

internal fun AnimatedContentTransitionScope<BackStackSnapshot>.cascadeTransitionSpec(
    layoutDirection: LayoutDirection,
): ContentTransform {
    val navigatingForward = targetState.backStackSize > initialState.backStackSize

    val inverseMultiplier = if (layoutDirection == Ltr) 1 else -1
    val initialOffset = { width: Int ->
        inverseMultiplier * if (navigatingForward) width else -width / 4
    }
    val targetOffset = { width: Int ->
        inverseMultiplier * if (navigatingForward) -width / 4 else width
    }

    val duration = 350
    return ContentTransform(
        targetContentEnter = slideInHorizontally(tween(duration), initialOffset),
        initialContentExit = slideOutHorizontally(tween(duration), targetOffset),
        targetContentZIndex = targetState.backStackSize.toFloat(),
        sizeTransform = SizeTransform(sizeAnimationSpec = { _, _ -> tween(duration) }),
    )
}
