package com.jerboa.ui.components.settings

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.db.entity.isAnon
import com.jerboa.model.AccountViewModel
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.getCurrentAccount
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    accountViewModel: AccountViewModel,
    onBack: () -> Unit,
    onClickLookAndFeel: () -> Unit,
    onClickAccountSettings: () -> Unit,
    onClickBlocks: () -> Unit,
    onClickAbout: () -> Unit,
    onClickBackupAndRestore: () -> Unit,
) {
    Log.d("jerboa", "Got to settings screen")

    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.settings_screen_settings), onClickBack = onBack)
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                ProvidePreferenceTheme {
                    Preference(
                        title = { Text(stringResource(R.string.settings_screen_look_and_feel)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Palette,
                                contentDescription = null,
                            )
                        },
                        onClick = onClickLookAndFeel,
                    )

                    if (!account.isAnon()) {
                        Preference(
                            title = {
                                Text(
                                    stringResource(
                                        R.string.settings_screen_account_settings,
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
                    if (!account.isAnon()) {
                        Preference(
                            title = {
                                Text(
                                    stringResource(id = R.string.blocks),
                                )
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Block,
                                    contentDescription = null,
                                )
                            },
                            onClick = onClickBlocks,
                        )
                    }
                    Preference(
                        title = { Text(stringResource(R.string.settings_screen_backup_and_restore)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Restore,
                                contentDescription = null,
                            )
                        },
                        onClick = onClickBackupAndRestore,
                    )
                    Preference(
                        title = { Text(stringResource(R.string.settings_screen_about)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                            )
                        },
                        onClick = onClickAbout,
                    )
                }
            }
        },
    )
}
