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
import it.vercruysse.lemmyapi.datatypes.CommunityAggregates
import it.vercruysse.lemmyapi.datatypes.CommunityFollowerView
import it.vercruysse.lemmyapi.datatypes.CommunityView
import it.vercruysse.lemmyapi.datatypes.Search
import it.vercruysse.lemmyapi.datatypes.SearchResponse
import it.vercruysse.lemmyapi.dto.SearchType
import it.vercruysse.lemmyapi.dto.SubscribedType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CommunityListViewModel(
    communities: List<CommunityFollowerView>,
) : ViewModel() {
    var searchRes: ApiState<SearchResponse> by mutableStateOf(ApiState.Empty)
        private set
    private var fetchCommunitiesJob: Job? = null

    fun searchCommunities(form: Search) {
        fetchCommunitiesJob?.cancel()
        fetchCommunitiesJob = viewModelScope.launch {
            delay(DEBOUNCE_DELAY)
            searchRes = ApiState.Loading
            searchRes = API.getInstance().search(form).toApiState()
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
                    subscribed = SubscribedType.Subscribed,
                    blocked = false,
                    counts =
                        CommunityAggregates(
                            community_id = cfv.community.id,
                            subscribers = 0,
                            subscribers_local = 0,
                            posts = 0,
                            comments = 0,
                            published = "",
                            users_active_day = 0,
                            users_active_week = 0,
                            users_active_month = 0,
                            users_active_half_year = 0,
                        ),
                    banned_from_community = false,
                )
            }

        searchRes =
            ApiState.Success(
                SearchResponse(
                    type_ = SearchType.Communities,
                    communities = followsIntoCommunityViews,
                    comments = emptyList(),
                    posts = emptyList(),
                    users = emptyList(),
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
