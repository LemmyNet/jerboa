@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.settings.lookandfeel

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.jerboa.R
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.SimpleTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LookAndFeelActivity(
    onBack: () -> Unit,
    onClickInterface: () -> Unit,
    onClickTheme: () -> Unit,
    onClickSecurity: () -> Unit,
    onClickAccessibility: () -> Unit
) {
    Log.d("jerboa", "Got to lookAndFeel activity")

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.settings_activity_look_and_feel), onClickBack = onBack)
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                SettingsMenuLink(
                    title = { Text("Interface") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.GridView,
                            contentDescription = null,
                        )
                    },
                    onClick = onClickInterface,
                )
                SettingsMenuLink(
                    title = { Text("Theme") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = null,
                        )
                    },
                    onClick = onClickTheme,
                )
                SettingsMenuLink(
                    title = { Text("Security") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                        )
                    },
                    onClick = onClickSecurity,
                )
                SettingsMenuLink(
                    title = { Text("Accessibility") },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Translate,
                            contentDescription = null,
                        )
                    },
                    onClick = onClickAccessibility,
                )
            }
        },
    )
}