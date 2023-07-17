package com.jerboa.ui.components.common

import android.widget.Toast
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.jerboa.api.ApiState

@Composable
fun ApiErrorText(
    msg: Throwable,
) {
    msg.message?.also { Text(text = it, color = MaterialTheme.colorScheme.error) }
}

@Composable
fun ApiErrorToast(
    msg: Throwable,
) {
    msg.message?.also { Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT).show() }
}

@Composable
fun ApiEmptyText() {
    Text("Empty")
}

fun <T> ApiState<T>.isLoading(): Boolean {
    return this is ApiState.Appending || this == ApiState.Loading || this == ApiState.Refreshing
}

fun <T> ApiState<T>.isRefreshing(): Boolean {
    return this == ApiState.Refreshing
}
