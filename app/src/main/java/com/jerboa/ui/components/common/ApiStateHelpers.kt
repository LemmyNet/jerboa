package com.jerboa.ui.components.common

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.jerboa.api.ApiState

@Composable
fun ApiErrorText(
    msg: Throwable,
    paddingValues: PaddingValues = PaddingValues(),
) {
    msg.message?.also {
        ApiErrorText(it, paddingValues = paddingValues)
    }
}

@Composable
fun ApiErrorText(
    msg: String,
    modifier: Modifier = Modifier,
    paddingValues: PaddingValues = PaddingValues(),
) {
    Text(
        text = msg,
        modifier = modifier.padding(paddingValues),
        color = MaterialTheme.colorScheme.onError,
    )
}

fun apiErrorToast(
    ctx: Context,
    msg: Throwable,
) {
    msg.message?.also {
        Log.e("apiErrorToast", it)
        Toast.makeText(ctx, it, Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun ApiEmptyText() {
    Text("Empty")
}

fun <T> ApiState<T>.isLoading(): Boolean = this is ApiState.Appending || this == ApiState.Loading || this == ApiState.Refreshing

fun <T> ApiState<T>.isRefreshing(): Boolean = this == ApiState.Refreshing
