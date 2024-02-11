// This hack is needed to depend on internal components of Cascade
// Author did not want to upstream this animated centered popup
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package com.jerboa.util.cascade

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.POPUP_MENU_WIDTH_RATIO
import me.saket.cascade.CascadeColumnScope
import me.saket.cascade.CascadeDefaults
import me.saket.cascade.CascadeState
import me.saket.cascade.internal.copy
import me.saket.cascade.rememberCascadeState
import me.saket.cascade.PopupContent as CascadePopupContent
import me.saket.cascade.internal.clickableWithoutRipple as clickableWithoutRippleCascade
import me.saket.cascade.internal.then as thenCascade

@Composable
fun CascadeCenteredDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    fixedWidth: Dp = LocalConfiguration.current.screenWidthDp.dp * POPUP_MENU_WIDTH_RATIO,
    shadowElevation: Dp = CascadeDefaults.shadowElevation,
    properties: PopupProperties = PopupProperties(focusable = true),
    state: CascadeState = rememberCascadeState(),
    shape: Shape = CascadeDefaults.shape,
    content: @Composable CascadeColumnScope.() -> Unit,
) {
    val expandedStates = remember { MutableTransitionState(false) }
    expandedStates.targetState = expanded

    if (expandedStates.currentState || expandedStates.targetState) {
        val transformOriginState = remember { mutableStateOf(TransformOrigin.Center) }

        // A full sized popup is shown so that content can render fake shadows
        // that do not suffer from https://issuetracker.google.com/issues/236109671.

        Popup(
            alignment = Alignment.Center,
            onDismissRequest = onDismissRequest,
            properties = properties.copy(usePlatformDefaultWidth = false),
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .thenCascade(properties.dismissOnClickOutside) {
                        clickableWithoutRippleCascade(onClick = onDismissRequest)
                    },
                Alignment.Center,
            ) {
                CascadePopupContent(
                    modifier =
                        Modifier
                            // Prevent clicks from leaking behind. Otherwise, they'll get picked up as outside
                            // clicks to dismiss the popup. This must be set _before_ the downstream modifiers to
                            // avoid overriding any clickable modifiers registered by the developer.
                            .clickableWithoutRippleCascade {}
                            .padding(vertical = LARGE_PADDING)
                            .then(modifier),
                    state = state,
                    fixedWidth = fixedWidth,
                    expandedStates = expandedStates,
                    transformOriginState = transformOriginState,
                    shadowElevation = shadowElevation,
                    shape = shape,
                    content = content,
                )
            }
        }
    }
}
