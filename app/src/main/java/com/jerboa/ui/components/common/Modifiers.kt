package com.jerboa.ui.components.common

import android.os.Build
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.Autofill
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillTree
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp

inline fun Modifier.ifDo(
    predicate: Boolean,
    modifier: Modifier.() -> Modifier,
): Modifier = if (predicate) modifier() else this

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
    autofillTypes: List<AutofillType>,
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
        }.onFocusChanged { focusState ->
            autofill?.run {
                if (focusState.isFocused) {
                    requestAutofillForNode(autofillNode)
                } else {
                    cancelAutofillForNode(autofillNode)
                }
            }
        }
}

fun Modifier.customMarquee(): Modifier = this.basicMarquee(initialDelayMillis = 4_000)

fun Modifier.fadingEdge(brush: Brush) =
    this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            drawRect(brush = brush, blendMode = BlendMode.DstIn)
        }
