package com.jerboa.model

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jerboa.api.API
import com.jerboa.api.DEFAULT_INSTANCE
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.isAnon
import com.jerboa.db.entity.isReady
import com.jerboa.db.repository.AccountRepository
import com.jerboa.feat.AccountVerificationState
import com.jerboa.jerboaApplication
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Stable
class AccountViewModel(
    private val repository: AccountRepository,
) : ViewModel() {
    val currentAccount = repository.currentAccount
    val allAccounts = repository.allAccounts

    fun insert(account: Account) =
        viewModelScope.launch {
            // Remove the default account
            if (account.current) {
                repository.removeCurrent()
            }
            repository.insert(account)
        }

    fun removeCurrent(toAnon: Boolean = false): Job =
        viewModelScope.launch {
            if (toAnon) {
                API.setLemmyInstanceSafe(DEFAULT_INSTANCE)
            }
            repository.removeCurrent()
        }

    fun updateCurrent(account: Account): Job =
        viewModelScope.launch {
            API.setLemmyInstanceSafe(account.instance, account.jwt)
            repository.updateCurrent(account.id)
        }

    // Be careful when setting the verification state,
    // when the account is not ready and used, it triggers a verification check
    // which can update the siteviewmodel.siteRes, if you have logic invalidating the
    // account based on siteRes changes make sure it doesn't cause a loop
    fun setVerificationState(
        accountId: Long,
        state: Int,
    ) = viewModelScope.launch {
        repository.setVerificationState(accountId, state)
    }

    fun invalidateAccount(account: Account) {
        if (!account.isAnon() && account.isReady()) {
            setVerificationState(account.id, AccountVerificationState.NOT_CHECKED.ordinal)
        }
    }

    fun delete(account: Account) =
        viewModelScope.launch {
            repository.delete(account)
        }

    fun deleteAccountAndSwapCurrent(
        account: Account,
        swapToAnon: Boolean = false,
    ): Job =
        viewModelScope.launch {
            if (account.isAnon()) return@launch

            val api = API.getInstance()
            if (api.FF.logout()) {
                api.logout() // Invalidates the jwt
            }

            val accounts = repository.allAccounts.value
            val nextAcc = accounts?.firstOrNull { it.id != account.id }

            if (!swapToAnon && nextAcc != null) {
                updateCurrent(nextAcc).join()
            } else {
                removeCurrent(true).join()
            }

            repository.delete(account)
        }
}

object AccountViewModelFactory {
    val Factory =
        viewModelFactory {
            initializer {
                AccountViewModel(jerboaApplication().container.accountRepository)
            }
        }
}
