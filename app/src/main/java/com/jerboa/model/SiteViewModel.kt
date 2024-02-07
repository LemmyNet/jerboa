package com.jerboa.model

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
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
import com.jerboa.db.entity.isAnon
import com.jerboa.db.repository.AccountRepository
import com.jerboa.jerboaApplication
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityFollowerView
import it.vercruysse.lemmyapi.v0x19.datatypes.GetSiteResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.GetUnreadCountResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.GetUnreadRegistrationApplicationCountResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonView
import it.vercruysse.lemmyapi.v0x19.datatypes.SaveUserSettings
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Stable
class SiteViewModel(private val accountRepository: AccountRepository) : ViewModel() {
    // Can't be private, because it needs to be set by the login viewmodel
    var siteRes: ApiState<GetSiteResponse> by mutableStateOf(ApiState.Empty)

    private var unreadCountRes: ApiState<GetUnreadCountResponse> by mutableStateOf(ApiState.Empty)
    private var unreadAppsCountRes: ApiState<GetUnreadRegistrationApplicationCountResponse> by mutableStateOf(ApiState.Empty)

    val unreadCount by derivedStateOf { getUnreadCountTotal() }
    val unreadAppsCount by derivedStateOf { getUnreadAppsCountTotal()}

    lateinit var saveUserSettings: SaveUserSettings

    init {
        viewModelScope.launch {

            val currAccount = accountRepository.getCurrentAsync()

            if (currAccount == null) {
                API.setLemmyInstance(DEFAULT_INSTANCE)
            } else {
                API.setLemmyInstanceSafe(currAccount.instance, currAccount.jwt)
            }

            // Makes sure that the site is fetched when the account is changed
            accountRepository.currentAccount
                .asFlow()
                .map { it ?: AnonAccount }
                .collect {
                    Log.d("SiteViewModel", "acc init for id: ${it.id}")
                    getSite()

                    if (it.isAnon()) { // Reset the unread counts if we're anonymous
                        unreadCountRes = ApiState.Empty
                        unreadAppsCountRes = ApiState.Empty
                    } else {
                        fetchUnreadCounts()
                        fetchUnreadAppsCount()
                    }
                }
        }
    }

    fun getSite(): Job {
        return viewModelScope.launch {
            siteRes = ApiState.Loading
            siteRes = API.getInstance().getSite().toApiState()

            when (val res = siteRes) {
                is ApiState.Success -> {
                    res.data.my_user?.local_user_view?.local_user?.let {
                        val currAcc = accountRepository.currentAccount.value
                        if (currAcc != null) {
                            val newAccount =
                                currAcc.copy(
                                    defaultListingType = it.default_listing_type.ordinal,
                                    defaultSortType = it.default_sort_type.ordinal,
                                )

                            if (currAcc != newAccount) {
                                accountRepository.update(newAccount)
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }

    fun fetchUnreadCounts() {
        viewModelScope.launch {
            viewModelScope.launch {
                unreadCountRes = ApiState.Loading
                unreadCountRes = API.getInstance().getUnreadCount().toApiState()
            }
        }
    }

    fun fetchUnreadAppsCount() {
        viewModelScope.launch {
            viewModelScope.launch {
                unreadCountRes = ApiState.Loading
                unreadCountRes = API.getInstance().getUnreadCount().toApiState()
            }
        }
    }


    private fun getUnreadCountTotal(): Long {
        return when (val res = unreadCountRes) {
            is ApiState.Success -> {
                val unreads = res.data
                unreads.mentions + unreads.private_messages + unreads.replies
            }

            else -> 0
        }
    }

    private fun getUnreadAppsCountTotal(): Long {
        return when (val res = unreadAppsCountRes) {
            is ApiState.Success -> {
                res.data.registration_applications
            }

            else -> 0
        }
    }

    fun updateUnreadCounts(
        dReplies: Int = 0,
        dMentions: Int = 0,
        dMessages: Int = 0,
    ) {
        when (val res = unreadCountRes) {
            is ApiState.Success -> {
                unreadCountRes =
                    ApiState.Success(
                        GetUnreadCountResponse(
                            private_messages = res.data.private_messages + dMessages,
                            mentions = res.data.mentions + dMentions,
                            replies = res.data.replies + dReplies,
                        ),
                    )
            }

            else -> {}
        }
    }

    fun showAvatar(): Boolean {
        return when (val res = siteRes) {
            is ApiState.Success -> res.data.my_user?.local_user_view?.local_user?.show_avatars ?: true
            else -> true
        }
    }

    fun enableDownvotes(): Boolean {
        return when (val res = siteRes) {
            is ApiState.Success -> res.data.site_view.local_site.enable_downvotes
            else -> true
        }
    }

    fun showScores(): Boolean {
        return when (val res = siteRes) {
            is ApiState.Success -> res.data.my_user?.local_user_view?.local_user?.show_scores ?: true
            else -> true
        }
    }

    fun getFollowList(): List<CommunityFollowerView> {
        return when (val res = siteRes) {
            is ApiState.Success -> res.data.my_user?.follows ?: emptyList()
            else -> emptyList()
        }
    }

    fun admins(): List<PersonView> {
        return when (val res = siteRes) {
            is ApiState.Success -> res.data.admins
            else -> emptyList()
        }
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
