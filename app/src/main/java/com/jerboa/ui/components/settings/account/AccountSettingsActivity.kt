package com.jerboa.ui.components.settings.account

import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.SiteViewModel

@Composable
fun AccountSettingsActivity(
    navController: NavController,
    accountSettingsViewModel: AccountSettingsViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel
) {
    Log.d("jerboa", "Got to settings activity")

    val scaffoldState = rememberScaffoldState()
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                SimpleTopAppBar(text = "Account Settings", navController = navController)
            },
            content = { padding ->
                account.also {
                    SettingsForm(
                        accountSettingsViewModel,
                        onClickSave = { form ->
                            accountSettingsViewModel.saveSettings(
                                form,
                                ctx,
                                siteViewModel = siteViewModel,
                                account = account
                            )
                        },
                        siteViewModel = siteViewModel,
                        account = account,
                        padding = padding
                    )
                }
            }
        )
    }
}
