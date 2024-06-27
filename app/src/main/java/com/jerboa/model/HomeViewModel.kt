package com.jerboa.model

import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jerboa.db.repository.AccountRepository
import com.jerboa.jerboaApplication

class HomeViewModel(
    accountRepository: AccountRepository,
) : PostsViewModel(accountRepository) {
    init {
        init()
    }

    companion object {
        val Factory =
            viewModelFactory {
                initializer {
                    HomeViewModel(jerboaApplication().container.accountRepository)
                }
            }
    }
}
