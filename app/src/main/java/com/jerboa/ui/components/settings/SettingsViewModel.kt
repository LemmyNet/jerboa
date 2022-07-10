package com.jerboa.ui.components.settings

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.saveUserSettingsWrapper
import com.jerboa.datatypes.api.SaveUserSettings
import com.jerboa.db.Account
import com.jerboa.ui.components.home.SiteViewModel
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {

    var loading by mutableStateOf(false)

    fun saveSettings(
        form: SaveUserSettings,
        ctx: Context,
        siteViewModel: SiteViewModel,
        account: Account?
    ) {

        viewModelScope.launch {
            loading = true
            saveUserSettingsWrapper(form, ctx)
            siteViewModel.fetchSite(account?.jwt, ctx)
            loading = false
        }
    }
}
