package com.jerboa.ui.components.settings

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.SimpleTopAppBar
import com.jerboa.ui.components.common.getCurrentAccount

@Composable
fun SettingsActivity(
    navController: NavController,
    accountViewModel: AccountViewModel
) {
    Log.d("jerboa", "Got to settings activity")

    val scaffoldState = rememberScaffoldState()
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                SimpleTopAppBar(text = "Settings", navController = navController)
            },
            content = { padding ->
                Column(modifier = Modifier.padding(padding)) {
                    SettingsMenuLink(
                        title = { Text("Look and feel") },
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Palette,
                                contentDescription = "TODO"
                            )
                        },
                        onClick = { navController.navigate("lookAndFeel") }
                    )
                    account?.also { acct ->
                        SettingsMenuLink(
                            title = { Text("${acct.name} settings") },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.ManageAccounts,
                                    contentDescription = "TODO"
                                )
                            },
                            onClick = { navController.navigate("accountSettings") }
                        )
                    }
                }
            }
        )
    }
}
