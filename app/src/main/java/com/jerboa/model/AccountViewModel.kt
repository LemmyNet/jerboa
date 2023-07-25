package com.jerboa.model

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.entity.isAnon
import com.jerboa.db.entity.isReady
import com.jerboa.db.repository.AccountRepository
import com.jerboa.fetchHomePosts
import com.jerboa.fetchInitialData
import com.jerboa.jerboaApplication
import com.jerboa.util.AccountVerificationState
import kotlinx.coroutines.launch

@Stable
class AccountViewModel(private val repository: AccountRepository) : ViewModel() {

    val currentAccount = repository.currentAccount
    val allAccounts = repository.allAccounts

    fun insert(account: Account) = viewModelScope.launch {
        repository.insert(account)
    }

    fun removeCurrent() = viewModelScope.launch {
        repository.removeCurrent()
    }

    fun setCurrent(accountId: Int) = viewModelScope.launch {
        repository.setCurrent(accountId)
    }

    // Be careful when setting the verification state,
    // when the account is not ready and used, it triggers a verification check
    // which can update the siteviewmodel.siteRes, if you have logic invalidating the
    // account based on siteRes changes make sure it doesn't cause a loop
    fun setVerificationState(accountId: Int, state: Int) = viewModelScope.launch {
        repository.setVerificationState(accountId, state)
    }

    fun invalidateAccount(account: Account) {
        if (!account.isAnon() && account.isReady()) {
            setVerificationState(account.id, AccountVerificationState.NOT_CHECKED.ordinal)
        }
    }

    fun delete(account: Account) = viewModelScope.launch {
        repository.delete(account)
    }

    fun deleteAccountAndSwapCurrent(
        account: Account,
        siteViewModel: SiteViewModel,
        homeViewModel: HomeViewModel? = null,
    ) = viewModelScope.launch {
        if (account.isAnon()) return@launch

        repository.delete(account)

        val accounts = repository.allAccounts.value
        var nextAcc = accounts?.firstOrNull { it.id != account.id }

        if (nextAcc != null) {
            repository.setCurrent(nextAcc.id)
        } else {
            nextAcc = AnonAccount
        }

        fetchInitialData(nextAcc, siteViewModel)

        if (homeViewModel != null) {
            fetchHomePosts(nextAcc, homeViewModel)
        }
    }
}

object AccountViewModelFactory {
    val Factory = viewModelFactory {
        initializer {
            AccountViewModel(jerboaApplication().container.accountRepository)
        }
    }
}
