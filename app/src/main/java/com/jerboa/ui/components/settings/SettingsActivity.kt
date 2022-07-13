package com.jerboa.ui.components.settings

import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.home.SiteViewModel

@Composable
fun SettingsActivity(
    navController: NavController,
    settingsViewModel: SettingsViewModel,
    accountViewModel: AccountViewModel,
    siteViewModel: SiteViewModel,
) {
    Log.d("jerboa", "Got to settings activity")

    val scaffoldState = rememberScaffoldState()
    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                SettingsHeader(
                    navController = navController,
                )
            },
            content = {
                account.also {
                    SettingsForm(
                        settingsViewModel,
                        onClickSave = { form ->
                            settingsViewModel.saveSettings(
                                form,
                                ctx,
                                siteViewModel = siteViewModel,
                                account = account,
                            )
                        },
                        siteViewModel = siteViewModel,
                        account = account,
                    )
                }
            }
        )
    }
}
