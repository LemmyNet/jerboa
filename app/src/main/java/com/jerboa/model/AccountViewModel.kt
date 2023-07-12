package com.jerboa.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jerboa.db.entity.Account
import com.jerboa.db.repository.AccountRepository
import com.jerboa.jerboaApplication
import kotlinx.coroutines.launch

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

    fun delete(account: Account) = viewModelScope.launch {
        repository.delete(account)
    }
}

object AccountViewModelFactory {
    val Factory = viewModelFactory {
        initializer {
            AccountViewModel(jerboaApplication().container.accountRepository)
        }
    }
}
