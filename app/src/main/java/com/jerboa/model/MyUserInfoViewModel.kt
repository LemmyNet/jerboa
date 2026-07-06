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
import com.jerboa.api.toApiState
import com.jerboa.api.toOpt
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.entity.isAnon
import com.jerboa.db.repository.AccountRepository
import com.jerboa.jerboaApplication
import it.vercruysse.lemmyapi.datatypes.CommunityFollowerView
import it.vercruysse.lemmyapi.datatypes.CommunityId
import it.vercruysse.lemmyapi.datatypes.MyUserInfo
import it.vercruysse.lemmyapi.datatypes.SaveUserSettings
import it.vercruysse.lemmyapi.datatypes.UnreadCountsResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@Stable
class MyUserInfoViewModel(
    private val accountRepository: AccountRepository
) : ViewModel() {

    // Can't be private, because it needs to be set by the login viewmodel
    var myUserRes: ApiState<MyUserInfo> by mutableStateOf(ApiState.Empty)

    var unreadCountsRes: ApiState<UnreadCountsResponse> by mutableStateOf(ApiState.Empty)
        private set

    lateinit var saveUserSettings: SaveUserSettings

    init {
        viewModelScope.launch {

            // Makes sure that the user is fetched when the account is changed
            accountRepository.currentAccount
                .asFlow()
                .map { it ?: AnonAccount }
                .collect {
                    Log.d("MyUserInfoViewModel", "acc init for id: ${it.id}")
                    getMyUser()

                    if (it.isAnon()) { // Reset the unread counts if we're anonymous
                        unreadCountsRes = ApiState.Empty
                    } else {
                        fetchUnreadCounts()
                    }
                }
        }
    }

    fun getMyUser(loadingState: ApiState<MyUserInfo> = ApiState.Loading): Job =
        viewModelScope.launch {
            myUserRes = loadingState
            myUserRes = API.getInstance().getMyUser().toApiState()

            when (val res = myUserRes) {
                is ApiState.Success -> {
                    val mui = res.data
                        val currAcc = accountRepository.currentAccount.value
                        val localUser = mui.local_user_view.local_user
                        if (currAcc != null) {
                            val newAccount =
                                currAcc.copy(
                                    defaultListingType = localUser.default_listing_type.ordinal,
                                    defaultSortType = localUser.default_post_sort_type.ordinal,
                                    isAdmin = localUser.admin,
                                    isMod = mui.moderates.isNotEmpty(),
                                )

                            if (currAcc != newAccount) {
                                accountRepository.update(newAccount)
                            }
                        }
                    }

                else -> {}
            }
        }


    fun fetchUnreadCounts() {
        viewModelScope.launch {
            unreadCountsRes = ApiState.Loading
            unreadCountsRes = API.getInstance().getUnreadCounts().toApiState()
        }
    }

    fun showAvatar(): Boolean =
        myUserRes.toOpt()?.
                    local_user_view
                    ?.local_user
                    ?.show_avatars ?: true

    fun moderatedCommunities(): List<CommunityId>? =
        myUserRes.toOpt()
                    ?.moderates
                    ?.map { it.community.id }

    fun getFollowList(): List<CommunityFollowerView> =
            myUserRes.toOpt()?.follows ?: emptyList()

    companion object {
        val Factory =
            viewModelFactory {
                initializer {
                    MyUserInfoViewModel(jerboaApplication().container.accountRepository)
                }
            }
    }
}