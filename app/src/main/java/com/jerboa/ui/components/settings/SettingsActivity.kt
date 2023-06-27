@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.settings

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.jerboa.R
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.common.toAbout
import com.jerboa.ui.components.common.toAccountSettings
import com.jerboa.ui.components.common.toLookAndFeel

@Composable
fun SettingsActivity(
    navController: NavController,
    accountViewModel: AccountViewModel,
) {
    Log.d("jerboa", "Got to settings activity")

    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.settings_activity_settings), navController = navController)
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                SettingsMenuLink(
                    title = { Text(stringResource(R.string.settings_activity_look_and_feel)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = null,
                        )
                    },
                    onClick = { navController.toLookAndFeel() },
                )
                account?.also { acct ->
                    SettingsMenuLink(
                        title = {
                            Text(
                                stringResource(
                                    R.string.settings_activity_account_settings,
                                    acct.name,
                                ),
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.ManageAccounts,
                                contentDescription = null,
                            )
                        },
                        onClick = { navController.toAccountSettings() },
                    )
                }
                SettingsMenuLink(
                    title = { Text(stringResource(R.string.settings_activity_about)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = null,
                        )
                    },
                    onClick = { navController.toAbout() },
                )
            }
        },
    )
}
