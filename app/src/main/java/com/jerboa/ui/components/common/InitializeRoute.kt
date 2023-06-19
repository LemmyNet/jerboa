package com.jerboa.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope

@Composable
fun InitializeRoute(initializationBlock: suspend CoroutineScope.() -> Unit) {
    // rememberSaveable persists this information across navigations
    var initialized by rememberSaveable { mutableStateOf(false) }

    // LaunchedEffect runs everytime we navigate. Therefore we need a guard.
    if (!initialized) {
        LaunchedEffect(Unit) {
            this.initializationBlock()

            // The effects might get cancelled sometimes and the initialization wouldn't have happened.
            // Therefore this has to be inside the effect and after the initialization block.
            initialized = true
        }
    }
}
