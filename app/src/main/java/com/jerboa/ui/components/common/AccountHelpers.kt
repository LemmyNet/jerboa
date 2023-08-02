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

val SpecialAccount = Account(
    id = -2,
    name = "Don't use this",
    defaultSortType = 0,
    jwt = "",
    defaultListingType = 0,
    current = false,
    verificationState = 0,
    instance = "",
)

/**
 * This functions only purpose is to be able to differentiate between the initial Account (Placeholder)
 * and the actual account. This way we can do separate behaviour for them.
 * Which is mostly ignore the placeholder.
 *
 * For example, without this we always would pull lemmy.ml/site before
 * it read the actual account and did instance/site.
 *
 * You can filter based on `.current` or `account !== SpecialAccount`
 */
@Composable
fun getSpecialCurrentAccount(accountViewModel: AccountViewModel): Account {
    val currentAccount by accountViewModel.currentAccount.observeAsState(SpecialAccount)

    // DeriveState prevents unnecessary recompositions
    val acc by remember {
        derivedStateOf { currentAccount ?: AnonAccount }
    }

    return acc
}

fun getPostViewMode(appSettingsViewModel: AppSettingsViewModel): PostViewMode {
    return getEnumFromIntSetting<PostViewMode>(appSettingsViewModel.appSettings) { it.postViewMode }
}
