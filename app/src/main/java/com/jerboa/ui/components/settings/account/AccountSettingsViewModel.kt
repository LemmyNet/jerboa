package com.jerboa.ui.components.settings.account

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jerboa.api.saveUserSettingsWrapper
import com.jerboa.datatypes.api.SaveUserSettings
import com.jerboa.db.Account
import com.jerboa.db.AccountRepository
import com.jerboa.ui.components.home.SiteViewModel
import kotlinx.coroutines.launch

class AccountSettingsViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    var loading by mutableStateOf(false)

    fun saveSettings(
        form: SaveUserSettings,
        ctx: Context,
        siteViewModel: SiteViewModel,
        account: Account?,
    ) {
        viewModelScope.launch {
            loading = true
            saveUserSettingsWrapper(form, ctx)
            siteViewModel.fetchSite(account?.jwt, ctx)
            if (account != null) {
                maybeUpdateAccountSettings(account, form)
            }
            loading = false
        }
    }
    private suspend fun maybeUpdateAccountSettings(account: Account, form: SaveUserSettings) {
        val newAccount = account.copy(
            defaultListingType = form.default_listing_type ?: account.defaultListingType,
            defaultSortType = form.default_sort_type ?: account.defaultSortType
        )
        if (newAccount != account) {
            accountRepository.update(newAccount)
        }
    }
}
class AccountSettingsViewModelFactory(
    private val repository: AccountRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AccountSettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AccountSettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
