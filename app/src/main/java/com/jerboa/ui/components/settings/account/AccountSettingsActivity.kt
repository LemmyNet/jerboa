
package com.jerboa.ui.components.settings.account

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.model.AccountSettingsViewModel
import com.jerboa.model.AccountViewModel
import com.jerboa.model.SiteViewModel
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.ActionTopBar
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

    val loading = when (accountSettingsViewModel.saveUserSettingsRes) {
        ApiState.Loading -> true
        else -> false
    }

    Scaffold(
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            ActionTopBar(
                onBackClick = onBack,
                onSaveClick = {
                    accountSettingsViewModel.saveSettings(
                        siteViewModel.saveUserSettings,
                        siteViewModel = siteViewModel,
                        account = account,
                        ctx,
                        onSuccess = onBack,
                    )
                },
                loading = loading,
                title = stringResource(R.string.account_settings_activity_account_settings),
                saveText = R.string.account_settings_save_settings,
            )
        },
        content = { padding ->
            SettingsForm(
                siteViewModel = siteViewModel,
                account = account,
                padding = padding,
            )
        },
    )
}
