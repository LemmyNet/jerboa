package com.jerboa.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.jerboa.JerboaAppState
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.repository.AccountRepository
import com.jerboa.findAndUpdatePost
import com.jerboa.findAndUpdatePostCreator
import com.jerboa.findAndUpdatePostCreatorBannedFromCommunity
import com.jerboa.findAndUpdatePostHidden
import com.jerboa.mergePosts
import com.jerboa.toEnumSafe
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.SortType
import it.vercruysse.lemmyapi.v0x19.datatypes.CreatePostLike
import it.vercruysse.lemmyapi.v0x19.datatypes.DeletePost
import it.vercruysse.lemmyapi.v0x19.datatypes.FeaturePost
import it.vercruysse.lemmyapi.v0x19.datatypes.GetPosts
import it.vercruysse.lemmyapi.v0x19.datatypes.GetPostsResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.HidePost
import it.vercruysse.lemmyapi.v0x19.datatypes.LockPost
import it.vercruysse.lemmyapi.v0x19.datatypes.MarkPostAsRead
import it.vercruysse.lemmyapi.v0x19.datatypes.PaginationCursor
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonView
import it.vercruysse.lemmyapi.v0x19.datatypes.PostView
import it.vercruysse.lemmyapi.v0x19.datatypes.SavePost
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

open class PostsViewModel(protected val accountRepository: AccountRepository) : ViewModel() {
    var postsRes: ApiState<GetPostsResponse> by mutableStateOf(ApiState.Empty)
        private set
    private var page by mutableLongStateOf(1)
    protected var pageCursor: PaginationCursor? by mutableStateOf(null)
        private set
    var sortType by mutableStateOf(SortType.Active)
        private set
    var listingType by mutableStateOf(ListingType.Local)
        private set

    protected fun init() {
        viewModelScope.launch {
            accountRepository.currentAccount
                .asFlow()
                .map { it ?: AnonAccount }
                .collect { account ->
                    updateSortType(account.defaultSortType.toEnumSafe())
                    updateListingType(account.defaultListingType.toEnumSafe())
                    Log.d("PostsViewModel", "Fetching posts")
                    resetPosts()
                }
        }
    }

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

    fun updatePost(postView: PostView) {
        when (val existing = postsRes) {
            is ApiState.Success -> {
                val newPosts = findAndUpdatePost(existing.data.posts, postView)
                val newRes = ApiState.Success(existing.data.copy(posts = newPosts))
                postsRes = newRes
            }

            else -> {}
        }
    }

    private fun updatePostHidden(form: HidePost) {
        when (val existing = postsRes) {
            is ApiState.Success -> {
                val newPosts = findAndUpdatePostHidden(existing.data.posts, form)
                val newRes = ApiState.Success(existing.data.copy(posts = newPosts))
                postsRes = newRes
            }

            else -> {}
        }
    }

    fun updateBanned(personView: PersonView) {
        when (val existing = postsRes) {
            is ApiState.Success -> {
                val posts = findAndUpdatePostCreator(existing.data.posts, personView.person)
                val newRes = ApiState.Success(existing.data.copy(posts = posts))
                postsRes = newRes
            }

            else -> {}
        }
    }

    fun updateBannedFromCommunity(banData: BanFromCommunityData) {
        when (val existing = postsRes) {
            is ApiState.Success -> {
                val posts = findAndUpdatePostCreatorBannedFromCommunity(existing.data.posts, banData)
                val newRes = ApiState.Success(existing.data.copy(posts = posts))
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

    fun hidePost(
        form: HidePost,
        ctx: Context,
    ) {
        viewModelScope.launch {
            val msg = if (form.hide) R.string.post_hidden else R.string.post_unhidden
            API.getInstance().hidePost(form).onSuccess {
                updatePostHidden(form)
                Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun lockPost(form: LockPost) {
        viewModelScope.launch {
            API.getInstance().lockPost(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }

    fun featurePost(form: FeaturePost) {
        viewModelScope.launch {
            API.getInstance().featurePost(form).onSuccess {
                updatePost(it.post_view)
            }
        }
    }
}
