package com.jerboa.ui.components.community.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.jerboa.DEBOUNCE_DELAY
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.Community
import com.jerboa.datatypes.types.CommunityAggregates
import com.jerboa.datatypes.types.CommunityView
import com.jerboa.datatypes.types.Search
import com.jerboa.datatypes.types.SearchResponse
import com.jerboa.datatypes.types.SearchType
import com.jerboa.datatypes.types.SortType
import com.jerboa.datatypes.types.SubscribedType
import com.jerboa.db.AppSettingsRepository
import com.jerboa.db.SearchHistory
import com.jerboa.db.SearchHistoryRepository
import com.jerboa.serializeToMap
import com.jerboa.ui.components.common.Initializable
import com.jerboa.ui.components.home.SiteViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.Instant

class CommunityListViewModelFactory(
    private val searchHistoryRepository: SearchHistoryRepository,
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommunityListViewModel(searchHistoryRepository, appSettingsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CommunityListViewModel(
    private val searchHistoryRepository: SearchHistoryRepository,
    private val appSettingsRepository: AppSettingsRepository,
) : ViewModel(), Initializable {
    override var initialized by mutableStateOf(false)
    init {
        viewModelScope.launch {
            appSettingsRepository.appSettings.observeForever { appSettings.value = it }
        }
    }
    var searchRes: ApiState<SearchResponse> by mutableStateOf(ApiState.Empty)
        private set

    var selectedCommunity: Community? by mutableStateOf(null)
        private set

    val searchHistory: Flow<List<SearchHistory>> = searchHistoryRepository.history()
        .filter { appSettings.value?.saveSearchHistory == true }
        .map { history -> history.sortedByDescending { it.timestamp } }

    var communities: List<CommunityView> by mutableStateOf(emptyList())

    private val appSettings = MutableStateFlow(appSettingsRepository.appSettings.value)

    private var fetchCommunitiesJob: Job? = null
    fun searchCommunities(form: Search, debounce: Boolean = false) {
        fetchCommunitiesJob?.cancel()
        fetchCommunitiesJob = viewModelScope.launch {
            if (debounce) delay(DEBOUNCE_DELAY)
            searchRes = ApiState.Loading
            searchRes = apiWrapper(API.getInstance().search(form.serializeToMap()))
            form.q.takeIf { query ->
                appSettings.value?.saveSearchHistory == true && query.isNotEmpty()
            }?.let { query ->
                searchHistoryRepository.insert(
                    SearchHistory(query, Instant.now().epochSecond),
                )
            }
        }
    }

    fun searchAllCommunities(
        query: String,
        jwt: String? = null,
        debounce: Boolean = false,
    ) {
        searchCommunities(
            Search(
                q = query,
                type_ = SearchType.Communities,
                sort = SortType.TopAll,
                auth = jwt,
            ),
            debounce,
        )
    }

    fun resetSearch() {
        searchRes = ApiState.Empty
    }
    suspend fun deleteSearchHistory(item: SearchHistory) {
        searchHistoryRepository.delete(item)
    }

    fun selectCommunity(community: Community) {
        selectedCommunity = community
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

                    communities = followsIntoCommunityViews
                }
            }

            else -> {}
        }
    }
}
