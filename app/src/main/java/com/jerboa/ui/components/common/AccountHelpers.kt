package com.jerboa.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.jerboa.PostViewMode
import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.db.entity.Account
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AppSettingsViewModel

@Composable
fun getCurrentAccount(accountViewModel: AccountViewModel): Account? {
    val currentAccount by accountViewModel.currentAccount.observeAsState(
        initial = Account(
            -1,
            false,
            "",
            "",
            "",
            0,
            0,
        ),
    )
    return currentAccount
}

fun getPostViewMode(appSettingsViewModel: AppSettingsViewModel): PostViewMode {
    return PostViewMode.values()[(appSettingsViewModel.appSettings.value ?: APP_SETTINGS_DEFAULT).postViewMode]
}
