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
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.CommunityFollowerView
import com.jerboa.datatypes.types.GetSiteResponse
import com.jerboa.datatypes.types.GetUnreadCountResponse
import com.jerboa.datatypes.types.SaveUserSettings
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.entity.isAnon
import com.jerboa.db.repository.AccountRepository
import com.jerboa.jerboaApplication
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Stable
class SiteViewModel(private val accountRepository: AccountRepository) : ViewModel() {
    // Can't be private, because it needs to be set by the login viewmodel
    var siteRes: ApiState<GetSiteResponse> by mutableStateOf(ApiState.Empty)

    private var unreadCountRes: ApiState<GetUnreadCountResponse> by mutableStateOf(ApiState.Empty)

    val unreadCount by derivedStateOf { getUnreadCountTotal(unreadCountRes) }
    lateinit var saveUserSettings: SaveUserSettings

    init {
        viewModelScope.launch {
            accountRepository.currentAccount
                .asFlow()
                .map { it ?: AnonAccount }
                .collect {
                    Log.d("Jerboa", "acc init for id: ${it.id}")

                    if (it.isAnon()) {
                        API.changeLemmyInstance(DEFAULT_INSTANCE, null)
                    } else {
                        API.changeLemmyInstance(it.instance, it.jwt)
                    }

                    getSite()

                    if (!it.isAnon()) {
                        fetchUnreadCounts()
                    } else { // Reset the unread count if we're anonymous
                        unreadCountRes = ApiState.Empty
                    }
                }
        }
    }

    fun getSite(): Job {
        return viewModelScope.launch {
            siteRes = ApiState.Loading
            siteRes = apiWrapper(API.getInstance().getSite())

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
                unreadCountRes = apiWrapper(API.getInstance().getUnreadCount())
            }
        }
    }

    private fun getUnreadCountTotal(unreadCountRes: ApiState<GetUnreadCountResponse>): Int {
        return when (val res = unreadCountRes) {
            is ApiState.Success -> {
                val unreads = res.data
                unreads.mentions + unreads.private_messages + unreads.replies
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

    fun getFollowList(): ImmutableList<CommunityFollowerView> {
        return when (val res = siteRes) {
            is ApiState.Success -> res.data.my_user?.follows?.toImmutableList() ?: persistentListOf()
            else -> persistentListOf()
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
