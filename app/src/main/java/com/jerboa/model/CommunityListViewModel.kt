package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.jerboa.DEBOUNCE_DELAY
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.nowBoolean
import it.vercruysse.lemmyapi.datatypes.CommunityActions
import it.vercruysse.lemmyapi.datatypes.CommunityFollowerView
import it.vercruysse.lemmyapi.datatypes.CommunityView
import it.vercruysse.lemmyapi.datatypes.ListCommunities
import it.vercruysse.lemmyapi.datatypes.PagedResponse
import it.vercruysse.lemmyapi.enums.CommunityFollowerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class CommunityListViewModel(
    communities: List<CommunityFollowerView>,
) : ViewModel() {
    var listRes: ApiState<PagedResponse<CommunityView>> by mutableStateOf(ApiState.Empty)
        private set
    private var fetchCommunitiesJob: Job? = null

    fun listCommunities(form: ListCommunities) {
        fetchCommunitiesJob?.cancel()
        fetchCommunitiesJob = viewModelScope.launch {
            delay(DEBOUNCE_DELAY.milliseconds)
            listRes = ApiState.Loading
            listRes = API.getInstance().listCommunities(form).toApiState()
        }
    }

    init {
        setCommunityListFromFollowed(communities)
    }

    private fun setCommunityListFromFollowed(myFollows: List<CommunityFollowerView>) {
        // A hack to convert communityFollowerView into CommunityView
        val followsIntoCommunityViews =
            myFollows.map { cfv ->
                CommunityView(
                    community = cfv.community,
                    community_actions = CommunityActions(
                        followed_at = nowBoolean(true),
                        follow_state = CommunityFollowerState.Accepted,
                    ),
                    can_mod = false,
                    tags = emptyList(),
                )
            }

        listRes =
            ApiState.Success(
                PagedResponse(
                    items = followsIntoCommunityViews,
                ),
            )
    }

    companion object {
        class Factory(
            private val followedCommunities: List<CommunityFollowerView>,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = CommunityListViewModel(followedCommunities) as T
        }
    }
}
