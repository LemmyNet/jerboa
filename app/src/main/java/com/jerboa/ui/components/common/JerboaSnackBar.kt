package com.jerboa.ui.components.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable

@Composable
fun JerboaSnackbarHost(snackbarHostState: SnackbarHostState) {
    SnackbarHost(snackbarHostState) {
        Snackbar(
            snackbarData = it,
            containerColor = MaterialTheme.colorScheme.background,
            dismissActionContentColor = MaterialTheme.colorScheme.onBackground,
            contentColor = MaterialTheme.colorScheme.onBackground,
        )
    }
}
