package com.jerboa.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import com.jerboa.db.Account
import com.jerboa.db.AccountViewModel

@Composable
fun getCurrentAccount(accountViewModel: AccountViewModel): Account? {
    val accounts by accountViewModel.allAccounts.observeAsState()
    return getCurrentAccount(accounts)
}

fun getCurrentAccount(accounts: List<Account>?): Account? {
    return accounts?.firstOrNull { it.current }
}
