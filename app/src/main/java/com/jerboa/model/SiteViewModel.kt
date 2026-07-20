package com.jerboa.model

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.DEFAULT_INSTANCE
import com.jerboa.api.toApiState
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.repository.AccountRepository
import com.jerboa.jerboaApplication
import it.vercruysse.lemmyapi.datatypes.GetSiteResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Stable
class SiteViewModel(
    private val accountRepository: AccountRepository,
) : ViewModel() {
    var siteRes: ApiState<GetSiteResponse> by mutableStateOf(ApiState.Empty)

    init {
        viewModelScope.launch {

            val currAccount = accountRepository.getCurrentAsync()

            if (currAccount == null) {
                API.setLemmyInstance(DEFAULT_INSTANCE)
            } else {
                API.setLemmyInstanceSafe(currAccount.instance, currAccount.jwt)
            }

            // Makes sure that the site is fetched when the account is changed
            // TODO instead, both the siteViewModel and myUserInfoViewModel
            // should be moved into the accountViewModel
            accountRepository.currentAccount
                .asFlow()
                .map { it ?: AnonAccount }
                .collect {
                    Log.d("SiteViewModel", "acc init for id: ${it.id}")
                    getSite()
                }
        }
    }

    fun getSite(loadingState: ApiState<GetSiteResponse> = ApiState.Loading): Job =
        viewModelScope.launch {
            siteRes = loadingState
            siteRes = API.getInstance().getSite().toApiState()
        }

    companion object {
        val Factory =
            viewModelFactory {
                initializer {
                    SiteViewModel(jerboaApplication().container.accountRepository)
                }
            }
    }
}
