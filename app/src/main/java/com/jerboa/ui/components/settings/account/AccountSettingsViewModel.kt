package com.jerboa.ui.components.settings.account

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.GetSite
import com.jerboa.datatypes.types.LoginResponse
import com.jerboa.datatypes.types.SaveUserSettings
import com.jerboa.db.Account
import com.jerboa.ui.components.home.SiteViewModel
import kotlinx.coroutines.launch

class AccountSettingsViewModel : ViewModel() {

    var saveUserSettingsRes: ApiState<LoginResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun saveSettings(
        form: SaveUserSettings,
        siteViewModel: SiteViewModel,
        account: Account?,
    ) {
        viewModelScope.launch {
            saveUserSettingsRes = ApiState.Loading
            saveUserSettingsRes = apiWrapper(API.getInstance().saveUserSettings(form))

            siteViewModel.getSite(
                GetSite(
                    auth = account?.jwt,
                ),
            )
        }
    }
}
