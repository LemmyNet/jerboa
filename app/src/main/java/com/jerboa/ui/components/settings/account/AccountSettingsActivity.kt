
package com.jerboa.ui.components.settings.account

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.jerboa.R
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.SiteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsActivity(
    navController: NavController,
    accountSettingsViewModel: AccountSettingsViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
) {
    Log.d("jerboa", "Got to settings activity")

    val account = getCurrentAccount(accountViewModel = accountViewModel)!!
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.account_settings_activity_account_settings), navController = navController)
        },
        content = { padding ->
            account.also {
                SettingsForm(
                    accountSettingsViewModel,
                    onClickSave = { form ->
                        accountSettingsViewModel.saveSettings(
                            form,
                            siteViewModel = siteViewModel,
                            account = account,
                        )
                    },
                    siteViewModel = siteViewModel,
                    account = account,
                    padding = padding,
                )
            }
        },
    )
}
