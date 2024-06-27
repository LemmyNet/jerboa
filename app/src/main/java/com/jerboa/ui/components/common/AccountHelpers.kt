package com.jerboa.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import com.jerboa.PostViewMode
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.getEnumFromIntSetting
import com.jerboa.model.AccountViewModel
import com.jerboa.model.AppSettingsViewModel

/**
 * Returns the current Account or the AnonAccount if there is no set current Account
 */
@Composable
fun getCurrentAccount(accountViewModel: AccountViewModel): Account {
    val currentAccount by accountViewModel.currentAccount.observeAsState()

    // DeriveState prevents unnecessary recompositions
    val acc by remember {
        derivedStateOf { currentAccount ?: AnonAccount }
    }

    return acc
}

fun getPostViewMode(appSettingsViewModel: AppSettingsViewModel): PostViewMode =
    getEnumFromIntSetting<PostViewMode>(appSettingsViewModel.appSettings) {
        it.postViewMode
    }

val GuardAccount = AnonAccount.copy()
