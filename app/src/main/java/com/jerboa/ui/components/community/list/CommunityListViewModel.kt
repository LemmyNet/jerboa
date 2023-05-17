package com.jerboa.ui.components.community.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.Search
import com.jerboa.datatypes.types.SearchResponse
import com.jerboa.serializeToMap
import kotlinx.coroutines.launch

class CommunityListViewModel : ViewModel() {
    var searchRes: ApiState<SearchResponse> by mutableStateOf(ApiState.Empty)
        private set

    var selectedCommunity: Community? by mutableStateOf(null)
        private set

    fun searchCommunities(form: Search) {
        viewModelScope.launch {
            searchRes = ApiState.Loading
            searchRes = apiWrapper(API.getInstance().search(form.serializeToMap()))
        }
    }

    fun selectCommunity(community: Community) {
        selectedCommunity = community
    }

//    @Deprecated("Don use")
    // TODO your follows are communityfollowerview, not a search result
//    fun setCommunityListFromFollowed(siteViewModel: SiteViewModel) {
//        siteViewModel.siteRes?.my_user?.follows?.also { communities ->
//            when (val searchRes = state.searchRes) {
//                ApiState.Empty -> TODO()
//                is ApiState.Failure -> TODO()
//                ApiState.Loading -> TODO()
//                is ApiState.Success -> {
//                    val newSearchRes = searchRes.data.copy(communities = communities)
//
//                }
//            }
//            state = state.copy(searchRes = state.searchRes.copy(ApiState.Success(SearchResponse()))
//
//            communityList.clear()
//            communityList.addAll(it)
//        }
//    }
}
