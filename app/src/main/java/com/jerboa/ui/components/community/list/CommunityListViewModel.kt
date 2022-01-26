package com.jerboa.ui.components.community.list

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.searchWrapper
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SearchType
import com.jerboa.datatypes.SortType
import com.jerboa.db.Account
import com.jerboa.ui.components.home.SiteViewModel
import kotlinx.coroutines.launch

class CommunityListViewModel : ViewModel() {
    // This can be either CommunityView, or CommunityFollowerView
    var communityList = mutableStateListOf<Any>()
        private set
    var selectedCommunity by mutableStateOf<CommunitySafe?>(null)
        private set
    var loading = mutableStateOf(false)
        private set

    fun searchCommunities(account: Account?, ctx: Context?, query: String) {
        viewModelScope.launch {
            val communities = searchWrapper(
                account = account,
                ctx = ctx,
                sortType = SortType.TopAll,
                listingType = ListingType.All,
                query = query,
                searchType = SearchType.Communities,
            )?.communities
            communityList.clear()
            communityList.addAll(communities.orEmpty())
        }
    }

    fun selectCommunity(community: CommunitySafe) {
        selectedCommunity = community
    }

    fun setCommunityListFromFollowed(siteViewModel: SiteViewModel) {
        siteViewModel.siteRes?.my_user?.follows?.also {
            communityList.clear()
            communityList.addAll(it)
//            selectedCommunity = null
        }
    }
}
