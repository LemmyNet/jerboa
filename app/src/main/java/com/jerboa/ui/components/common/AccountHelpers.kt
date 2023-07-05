package com.jerboa.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.jerboa.PostViewMode
import com.jerboa.db.APP_SETTINGS_DEFAULT
import com.jerboa.db.AccountViewModel
import com.jerboa.db.AppSettingsViewModel
import com.jerboa.db.entity.Account

@Composable
fun getCurrentAccount(accountViewModel: AccountViewModel): Account? {
    val currentAccount by accountViewModel.currentAccount.observeAsState()
    return currentAccount
}

fun getPostViewMode(appSettingsViewModel: AppSettingsViewModel): PostViewMode {
    return PostViewMode.values()[(appSettingsViewModel.appSettings.value ?: APP_SETTINGS_DEFAULT).postViewMode]
}
