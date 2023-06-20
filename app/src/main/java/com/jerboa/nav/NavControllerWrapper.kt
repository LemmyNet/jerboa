package com.jerboa.nav

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

abstract class NavControllerWrapper {
    protected abstract val navController: NavController

    fun canPop() = navController.previousBackStackEntry != null

    fun navigateUp() = navController.navigateUp()
}

// https://stackoverflow.com/a/69533584/13390651
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PaddingValues.bottomIfKeyboardNotOpen(): State<Dp> {
    val bottomPadding = if (WindowInsets.isImeVisible) 0.dp else calculateBottomPadding()
    return rememberUpdatedState(bottomPadding)
}
