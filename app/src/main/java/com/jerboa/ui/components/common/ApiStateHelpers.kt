package com.jerboa.ui.components.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ApiErrorText(
    msg: Throwable,
) {
    msg.message?.also { Text(it) }
}

@Composable
fun ApiEmptyText() {
    Text("Empty")
}
