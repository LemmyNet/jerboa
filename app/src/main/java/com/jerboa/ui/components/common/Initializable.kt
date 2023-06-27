package com.jerboa.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import com.jerboa.gson
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

@Composable
inline fun<reified T> NavController.ConsumeReturn(
    key: String,
    crossinline consumeBlock: (T) -> Unit,
) {
    LaunchedEffect(key) {
        val savedStateHandle = currentBackStackEntry?.savedStateHandle
        if (savedStateHandle?.contains(key) == true) {
            consumeBlock(gson.fromJson(savedStateHandle.get<String>(key), T::class.java))
            savedStateHandle.remove<String>(key)
        }
    }
}

fun<T> NavController.addReturn(key: String, value: T) {
    previousBackStackEntry?.savedStateHandle?.set(key, gson.toJson(value))
}
