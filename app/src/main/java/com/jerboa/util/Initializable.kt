package com.jerboa.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.CoroutineScope

interface Initializable {

    var initialized: Boolean
}

@Composable
fun InitializeRoute(obj: Initializable, initBlock: suspend CoroutineScope.() -> Unit) {
    if (!obj.initialized) {
        LaunchedEffect(Unit) {
            initBlock()
            obj.initialized = true
        }
    }
}
