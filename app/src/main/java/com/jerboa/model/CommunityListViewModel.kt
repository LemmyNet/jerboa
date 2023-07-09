package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.CommunityAggregates
import com.jerboa.datatypes.types.CommunityView
import com.jerboa.datatypes.types.Search
import com.jerboa.datatypes.types.SearchResponse
import com.jerboa.datatypes.types.SearchType
import com.jerboa.datatypes.types.SubscribedType
import com.jerboa.serializeToMap
import com.jerboa.ui.components.common.Initializable
import kotlinx.coroutines.launch

class CommunityListViewModel : ViewModel(), Initializable {
    override var initialized by mutableStateOf(false)

    var searchRes: ApiState<SearchResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun searchCommunities(form: Search) {
        viewModelScope.launch {
            searchRes = ApiState.Loading
            searchRes = apiWrapper(API.getInstance().search(form.serializeToMap()))
        }
    }

    fun setCommunityListFromFollowed(siteViewModel: SiteViewModel) {
        when (val siteRes = siteViewModel.siteRes) {
            is ApiState.Success -> {
                siteRes.data.my_user?.let { myUserInfo ->
                    val myFollows = myUserInfo.follows

                    // A hack to convert communityFollowerView into CommunityView
                    val followsIntoCommunityViews = myFollows.map { cfv ->
                        CommunityView(
                            community = cfv.community,
                            subscribed = SubscribedType.Subscribed,
                            blocked = false,
                            counts = CommunityAggregates(
                                id = 0,
                                community_id = cfv.community.id,
                                subscribers = 0,
                                posts = 0,
                                comments = 0,
                                published = "",
                                users_active_day = 0,
                                users_active_week = 0,
                                users_active_month = 0,
                                users_active_half_year = 0,
                            ),
                        )
                    }

                    searchRes = ApiState.Success(
                        SearchResponse(
                            type_ = SearchType.Communities,
                            communities = followsIntoCommunityViews,
                            comments = emptyList(),
                            posts = emptyList(),
                            users = emptyList(),
                        ),
                    )
                }
            }

            else -> {}
        }
    }
}
