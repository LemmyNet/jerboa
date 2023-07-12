package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.GetSite
import com.jerboa.datatypes.types.LoginResponse
import com.jerboa.datatypes.types.SaveUserSettings
import com.jerboa.db.entity.Account
import com.jerboa.db.repository.AccountRepository
import com.jerboa.jerboaApplication
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AccountSettingsViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {
    var saveUserSettingsRes: ApiState<LoginResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun saveSettings(
        form: SaveUserSettings,
        siteViewModel: SiteViewModel,
        account: Account,
    ) {
        viewModelScope.launch {
            saveUserSettingsRes = ApiState.Loading
            saveUserSettingsRes = apiWrapper(API.getInstance().saveUserSettings(form))

            siteViewModel.getSite(
                GetSite(auth = account.jwt),
            )

            val newAccount = async { maybeUpdateAccountSettings(account, form) }.await()

            siteViewModel.updateFromAccount(newAccount)
        }
    }

    //     TODO Where is this used??
    private suspend fun maybeUpdateAccountSettings(
        account: Account,
        form: SaveUserSettings,
    ): Account {
        val newAccount = account.copy(
            defaultListingType = form.default_listing_type?.ordinal ?: account.defaultListingType,
            defaultSortType = form.default_sort_type?.ordinal ?: account.defaultSortType,
        )
        if (newAccount != account) {
            accountRepository.update(newAccount)
        }
        return newAccount
    }
}

object AccountSettingsViewModelFactory {
    val Factory = viewModelFactory {
        initializer {
            AccountSettingsViewModel(jerboaApplication().container.accountRepository)
        }
    }
}
