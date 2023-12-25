package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.JerboaAppState
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.findAndUpdatePost
import com.jerboa.mergePosts
import com.jerboa.model.helper.PostsHelper
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.SortType
import it.vercruysse.lemmyapi.v0x19.datatypes.GetPosts
import it.vercruysse.lemmyapi.v0x19.datatypes.GetPostsResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.MarkPostAsRead
import it.vercruysse.lemmyapi.v0x19.datatypes.PaginationCursor
import it.vercruysse.lemmyapi.v0x19.datatypes.PostView
import kotlinx.coroutines.launch

open class PostsViewModel : ViewModel(), PostsHelper {
    var postsRes: ApiState<GetPostsResponse> by mutableStateOf(ApiState.Empty)
        private set
    private var page by mutableIntStateOf(1)
    protected var pageCursor: PaginationCursor? by mutableStateOf(null)
        private set
    var sortType by mutableStateOf(SortType.Active)
        private set
    var listingType by mutableStateOf(ListingType.Local)
        private set

    override val scope = viewModelScope

    protected fun nextPage() {
        page += 1
    }

    protected fun prevPage() {
        page -= 1
    }

    protected fun resetPage() {
        page = 1
        pageCursor = null
    }

    protected fun getPosts(
        form: GetPosts,
        state: ApiState<GetPostsResponse> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            postsRes = state
            postsRes = API.getInstance().getPosts(form).toApiState()
        }
    }

    fun appendPosts() {
        viewModelScope.launch {
            val oldRes = postsRes
            postsRes =
                when (oldRes) {
                    is ApiState.Appending -> return@launch
                    is ApiState.Holder -> ApiState.Appending(oldRes.data)
                    else -> return@launch
                }

            // Update the page cursor before fetching again
            pageCursor = oldRes.data.next_page
            nextPage()
            val newRes = API.getInstance().getPosts(getForm()).toApiState()

            postsRes =
                when (newRes) {
                    is ApiState.Success -> {
                        val res =
                            GetPostsResponse(
                                posts =
                                    mergePosts(
                                        oldRes.data.posts,
                                        newRes.data.posts,
                                    ),
                                next_page = newRes.data.next_page,
                            )
                        ApiState.Success(
                            res,
                        )
                    }

                    else -> {
                        prevPage()
                        ApiState.AppendingFailure(oldRes.data)
                    }
                }
        }
    }

    override fun updatePost(postView: PostView) {
        when (val existing = postsRes) {
            is ApiState.Success -> {
                val newPosts = findAndUpdatePost(existing.data.posts, postView)
                val newRes = ApiState.Success(existing.data.copy(posts = newPosts))
                postsRes = newRes
            }

            else -> {}
        }
    }

    fun resetPosts() {
        resetPage()
        getPosts(
            getForm(),
        )
    }

    fun refreshPosts() {
        resetPage()
        getPosts(
            getForm(),
            ApiState.Refreshing,
        )
    }

    protected open fun getForm(): GetPosts {
        return GetPosts(
            page = page,
            page_cursor = pageCursor,
            sort = sortType,
            type_ = listingType,
        )
    }

    fun markPostAsRead(
        form: MarkPostAsRead,
        post: PostView,
        appState: JerboaAppState,
    ) {
        appState.coroutineScope.launch {
            API.getInstance().markPostAsRead(form).onSuccess {
                updatePost(post.copy(read = form.read))
            }
        }
    }

    fun updateSortType(sortType: SortType) {
        this.sortType = sortType
    }

    fun updateListingType(listingType: ListingType) {
        this.listingType = listingType
    }
}
