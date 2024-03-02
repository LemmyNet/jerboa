@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.settings

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Palette
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
import com.jerboa.db.entity.isAnon
import com.jerboa.model.AccountViewModel
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.getCurrentAccount

@Composable
fun SettingsActivity(
    accountViewModel: AccountViewModel,
    onBack: () -> Unit,
    onClickLookAndFeel: () -> Unit,
    onClickAccountSettings: () -> Unit,
    onClickAbout: () -> Unit,
) {
    Log.d("jerboa", "Got to settings activity")

    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    Scaffold(
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.settings_activity_settings), onClickBack = onBack)
        },
        content = { padding ->
            Column(
                modifier = Modifier.padding(padding)
                    .verticalScroll(scrollState),
            ) {
                SettingsMenuLink(
                    title = { Text(stringResource(R.string.settings_activity_look_and_feel)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = null,
                        )
                    },
                    onClick = onClickLookAndFeel,
                )
                if (!account.isAnon()) {
                    SettingsMenuLink(
                        title = {
                            Text(
                                stringResource(
                                    R.string.settings_activity_account_settings,
                                    account.name,
                                ),
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.ManageAccounts,
                                contentDescription = null,
                            )
                        },
                        onClick = onClickAccountSettings,
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
                    onClick = onClickAbout,
                )
            }
        },
    )
}
