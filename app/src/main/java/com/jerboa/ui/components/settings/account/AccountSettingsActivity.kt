
package com.jerboa.ui.components.settings.account

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.model.AccountSettingsViewModel
import com.jerboa.model.AccountViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.getCurrentAccount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsActivity(
    accountSettingsViewModel: AccountSettingsViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
    onBack: () -> Unit,
) {
    Log.d("jerboa", "Got to settings activity")
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.account_settings_activity_account_settings), onClickBack = onBack)
        },
        content = { padding ->
            SettingsForm(
                accountSettingsViewModel,
                onClickSave = { form ->
                    accountSettingsViewModel.saveSettings(
                        form,
                        siteViewModel = siteViewModel,
                        account = account,
                        ctx,
                    )
                },
                siteViewModel = siteViewModel,
                account = account,
                padding = padding,
            )
        },
    )
}
