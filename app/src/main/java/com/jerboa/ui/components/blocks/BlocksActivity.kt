package com.jerboa.ui.components.blocks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
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
import com.jerboa.model.AccountViewModel
import com.jerboa.ui.components.common.JerboaSnackbarHost
import com.jerboa.ui.components.common.SimpleTopAppBar
import me.zhanghai.compose.preference.Preference
import me.zhanghai.compose.preference.ProvidePreferenceTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlocksActivity(
    accountViewModel: AccountViewModel,
    onBack: () -> Unit,
    onClickUsers: () -> Unit,
    onClickCommunities: () -> Unit,
    onClickInstances: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { JerboaSnackbarHost(snackbarHostState) },
        topBar = {
            SimpleTopAppBar(text = stringResource(R.string.settings_activity_settings), onClickBack = onBack)
        },
        content = { padding ->
            Column(modifier = Modifier.padding(padding)) {
                ProvidePreferenceTheme {
                    Preference(
                        title = { Text("Users") },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "User icon",
                            )
                        },
                        onClick = onClickUsers,
                    )
                    Preference(
                        title = { Text("Communities") },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "User icon",
                            )
                        },
                        onClick = onClickCommunities,
                    )
                    Preference(
                        title = { Text("Instances") },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = "User icon",
                            )
                        },
                        onClick = onClickInstances,
                    )
                }
            }
        },
    )
}
