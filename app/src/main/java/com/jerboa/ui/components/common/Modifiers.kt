package com.jerboa.ui.components.common

import android.os.Build
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabPosition
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.Autofill
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillTree
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import kotlinx.collections.immutable.ImmutableList

inline fun Modifier.ifDo(
    predicate: Boolean,
    modifier: Modifier.() -> Modifier,
): Modifier {
    return if (predicate) modifier() else this
}

fun Modifier.getBlurredOrRounded(
    blur: Boolean,
    rounded: Boolean = false,
): Modifier {
    var lModifier = this

    if (rounded) {
        lModifier = lModifier.clip(RoundedCornerShape(12f))
    }
    if (blur && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        lModifier = lModifier.blur(radius = 100.dp)
    }
    return lModifier
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.onAutofill(
    tree: AutofillTree,
    autofill: Autofill?,
    autofillTypes: ImmutableList<AutofillType>,
    onFill: (String) -> Unit,
): Modifier {
    val autofillNode =
        AutofillNode(
            autofillTypes = autofillTypes,
            onFill = onFill,
        )
    tree += autofillNode

    return this
        .onGloballyPositioned {
            autofillNode.boundingBox = it.boundsInWindow()
        }
        .onFocusChanged { focusState ->
            autofill?.run {
                if (focusState.isFocused) {
                    requestAutofillForNode(autofillNode)
                } else {
                    cancelAutofillForNode(autofillNode)
                }
            }
        }
}

@OptIn(ExperimentalFoundationApi::class)
fun Modifier.pagerTabIndicatorOffset2(
    pagerState: PagerState,
    tabPositions: List<TabPosition>,
    pageIndexMapping: (Int) -> Int = { it },
): Modifier =
    layout { measurable, constraints ->
        if (tabPositions.isEmpty()) {
            // If there are no pages, nothing to show
            layout(constraints.maxWidth, 0) {}
        } else {
            val currentPage =
                minOf(tabPositions.lastIndex, pageIndexMapping(pagerState.currentPage))
            val currentTab = tabPositions[currentPage]
            val previousTab = tabPositions.getOrNull(currentPage - 1)
            val nextTab = tabPositions.getOrNull(currentPage + 1)
            val fraction = pagerState.currentPageOffsetFraction
            val indicatorWidth =
                if (fraction > 0 && nextTab != null) {
                    lerp(currentTab.width, nextTab.width, fraction).roundToPx()
                } else if (fraction < 0 && previousTab != null) {
                    lerp(currentTab.width, previousTab.width, -fraction).roundToPx()
                } else {
                    currentTab.width.roundToPx()
                }
            val indicatorOffset =
                if (fraction > 0 && nextTab != null) {
                    lerp(currentTab.left, nextTab.left, fraction).roundToPx()
                } else if (fraction < 0 && previousTab != null) {
                    lerp(currentTab.left, previousTab.left, -fraction).roundToPx()
                } else {
                    currentTab.left.roundToPx()
                }
            val placeable =
                measurable.measure(
                    Constraints(
                        minWidth = indicatorWidth,
                        maxWidth = indicatorWidth,
                        minHeight = 0,
                        maxHeight = constraints.maxHeight,
                    ),
                )
            layout(constraints.maxWidth, maxOf(placeable.height, constraints.minHeight)) {
                placeable.placeRelative(
                    indicatorOffset,
                    maxOf(constraints.minHeight - placeable.height, 0),
                )
            }
        }
    }
