package com.jerboa.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.jerboa.findAndUpdateComment
import com.jerboa.model.helper.CommentsHelper
import com.jerboa.ui.components.common.apiErrorToast
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.SearchType
import it.vercruysse.lemmyapi.dto.SortType
import it.vercruysse.lemmyapi.dto.SubscribedType
import it.vercruysse.lemmyapi.v0x19.datatypes.CommentView
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityAggregates
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityFollowerView
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityView
import it.vercruysse.lemmyapi.v0x19.datatypes.Search
import it.vercruysse.lemmyapi.v0x19.datatypes.SearchResponse
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchListViewModel(
    communities: ImmutableList<CommunityFollowerView>,
    selectCommunityMode: Boolean,
) : ViewModel(), CommentsHelper {
    private var fetchSearchJob: Job? = null
    var q by mutableStateOf("")

    var searchRes: ApiState<SearchResponse> by mutableStateOf(ApiState.Empty)
        private set

    var currentSearchType by mutableStateOf(SearchType.All)
    var currentListing by mutableStateOf(ListingType.All)
    var currentSort by mutableStateOf(SortType.New)

    var page by mutableIntStateOf(1)

    override val scope = viewModelScope

    override fun updateComment(commentView: CommentView) {
        when (val existing = searchRes) {
            is ApiState.Success -> {
                val newComments =
                    findAndUpdateComment(
                        existing.data.comments,
                        commentView,
                    )
                searchRes = ApiState.Success(existing.data.copy(comments = newComments))
            }

            else -> {}
        }
    }

    init {
        if (selectCommunityMode) {
            setCommunityListFromFollowed(communities)
            currentSearchType = SearchType.Communities
            currentSort = SortType.TopAll
            currentListing = ListingType.Local
        }
    }

    fun updateSearch() {
        fetchSearchJob?.cancel()
        fetchSearchJob =
            viewModelScope.launch {
                delay(DEBOUNCE_DELAY)
                search()
            }
    }

    fun onSearchChange(q: String) {
        this.q = q
        this.page = 1
        updateSearch()
    }

    private fun search() {
        viewModelScope.launch {
            searchRes = ApiState.Loading
            searchRes = API.getInstance().search(getForm()).toApiState()
        }
    }

    private fun getForm(): Search {
        return Search(
            q = q,
            sort = currentSort,
            type_ = currentSearchType,
            listing_type = currentListing,
            page = page,
        )
    }

    private fun setCommunityListFromFollowed(myFollows: ImmutableList<CommunityFollowerView>) {
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

    fun searchNextPage(ctx: Context) {
        viewModelScope.launch {
            page++
            val res = API.getInstance().search(getForm())

            res.onSuccess {
                when (val oldRes = searchRes) {
                    is ApiState.Success -> {
                        searchRes = ApiState.Success(
                            SearchResponse(
                                type_ = SearchType.Communities,
                                communities = oldRes.data.communities + it.communities,
                                comments = oldRes.data.comments + it.comments,
                                posts = oldRes.data.posts + it.posts,
                                users = oldRes.data.users + it.users,
                            ),
                        )
                    }

                    else -> {}
                }
            }.onFailure {
                page--
                apiErrorToast(ctx, it)
            }
        }
    }

    companion object {
        class Factory(
            private val followedCommunities: ImmutableList<CommunityFollowerView>,
            private val selectCommunityMode: Boolean,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                return SearchListViewModel(followedCommunities, selectCommunityMode) as T
            }
        }
    }
}
