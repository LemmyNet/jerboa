package com.jerboa.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.jerboa.feat.showBlockPersonToast
import com.jerboa.findAndUpdateComment
import com.jerboa.findAndUpdatePost
import it.vercruysse.lemmyapi.datatypes.BlockPerson
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.CommunityAggregates
import it.vercruysse.lemmyapi.datatypes.CommunityFollowerView
import it.vercruysse.lemmyapi.datatypes.CommunityView
import it.vercruysse.lemmyapi.datatypes.CreateCommentLike
import it.vercruysse.lemmyapi.datatypes.CreatePostLike
import it.vercruysse.lemmyapi.datatypes.DeleteComment
import it.vercruysse.lemmyapi.datatypes.DeletePost
import it.vercruysse.lemmyapi.datatypes.PostView
import it.vercruysse.lemmyapi.datatypes.SaveComment
import it.vercruysse.lemmyapi.datatypes.SavePost
import it.vercruysse.lemmyapi.datatypes.Search
import it.vercruysse.lemmyapi.datatypes.SearchResponse
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.SearchType
import it.vercruysse.lemmyapi.dto.SortType
import it.vercruysse.lemmyapi.dto.SubscribedType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchListViewModel(
    communities: List<CommunityFollowerView>,
    initialSearchType: SearchType,
) : ViewModel() {
    private var fetchSearchJob: Job? = null
    var q by mutableStateOf("")

    var searchRes: ApiState<SearchResponse> by mutableStateOf(ApiState.Empty)
        private set

    var showingFollowedSuggestions by mutableStateOf(false)
        private set

    var currentSearchType by mutableStateOf(initialSearchType)
    var currentListing by mutableStateOf(ListingType.All)
    var currentSort by mutableStateOf(SortType.TopAll)

    var page by mutableLongStateOf(1)

    init {
        if (currentSearchType == SearchType.Communities || currentSearchType == SearchType.All) {
            setCommunityListFromFollowed(communities)
        }
    }

    fun updatePost(postView: PostView) {
        when (val existing = searchRes) {
            is ApiState.Success -> {
                val newPosts =
                    findAndUpdatePost(
                        existing.data.posts,
                        postView,
                    )
                searchRes = ApiState.Success(existing.data.copy(posts = newPosts))
            }

            else -> {}
        }
    }

    fun updateComment(commentView: CommentView) {
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

    fun updateSearch() {
        fetchSearchJob?.cancel()
        if (q.isBlank()) {
            this.page = 1
            showingFollowedSuggestions = false
            searchRes = ApiState.Empty
            return
        }
        fetchSearchJob =
            viewModelScope.launch {
                delay(DEBOUNCE_DELAY)
                search()
            }
    }

    fun onSearchChange(q: String) {
        this.q = q
        updateSearch()
    }

    private suspend fun search() {
        this.page = 1
        showingFollowedSuggestions = false
        searchRes = ApiState.Loading
        searchRes = API.getInstance().search(getForm()).toApiState()
    }

    private fun getForm(): Search =
        Search(
            q = q,
            sort = currentSort,
            type_ = currentSearchType,
            listing_type = currentListing,
            page = page,
        )

    private fun setCommunityListFromFollowed(myFollows: List<CommunityFollowerView>) {
        showingFollowedSuggestions = true

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
                            subscribers_local = 0,
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

    fun searchNextPage() {
        if (showingFollowedSuggestions) return

        viewModelScope.launch {
            val oldRes = searchRes
            searchRes = when (oldRes) {
                is ApiState.Appending -> return@launch
                is ApiState.Holder -> ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            page++

            when (val newRes = API.getInstance().search(getForm()).toApiState()) {
                is ApiState.Success -> {
                    searchRes = ApiState.Success(
                        SearchResponse(
                            type_ = newRes.data.type_,
                            communities = oldRes.data.communities + newRes.data.communities,
                            comments = oldRes.data.comments + newRes.data.comments,
                            posts = oldRes.data.posts + newRes.data.posts,
                            users = oldRes.data.users + newRes.data.users,
                        ),
                    )
                }

                else -> {
                    searchRes = ApiState.AppendingFailure(oldRes.data)
                    page--
                }
            }
        }
    }

    fun likeComment(form: CreateCommentLike) {
        viewModelScope.launch {
            val res = API.getInstance().createCommentLike(form)

            res.onSuccess {
                updateComment(it.comment_view)
            }
        }
    }

    fun deleteComment(form: DeleteComment) {
        viewModelScope.launch {
            API.getInstance().deleteComment(form).onSuccess {
                updateComment(it.comment_view)
            }
        }
    }

    fun saveComment(form: SaveComment) {
        viewModelScope.launch {
            API.getInstance().saveComment(form).onSuccess {
                updateComment(it.comment_view)
            }
        }
    }

    fun blockPerson(
        form: BlockPerson,
        ctx: Context,
    ) {
        viewModelScope.launch {
            val blockPersonRes = API.getInstance().blockPerson(form)
            withContext(Dispatchers.Main) {
                showBlockPersonToast(blockPersonRes, ctx)
            }
        }
    }

    fun likePost(form: CreatePostLike) {
        viewModelScope.launch {
            API.getInstance().createPostLike(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }

    fun savePost(form: SavePost) {
        viewModelScope.launch {
            API.getInstance().savePost(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }

    fun deletePost(form: DeletePost) {
        viewModelScope.launch {
            API.getInstance().deletePost(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }

    companion object {
        class Factory(
            private val followedCommunities: List<CommunityFollowerView>,
            private val initialSearchType: SearchType,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T = SearchListViewModel(followedCommunities, initialSearchType) as T
        }
    }
}
