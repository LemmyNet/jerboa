package com.jerboa.model

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jerboa.JerboaAppState
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockCommunityResponse
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.BlockPersonResponse
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.GetPosts
import com.jerboa.datatypes.types.GetPostsResponse
import com.jerboa.datatypes.types.MarkPostAsRead
import com.jerboa.datatypes.types.PaginationCursor
import com.jerboa.datatypes.types.PostResponse
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.SavePost
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.repository.AccountRepository
import com.jerboa.findAndUpdatePost
import com.jerboa.jerboaApplication
import com.jerboa.mergePosts
import com.jerboa.serializeToMap
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockPersonToast
import com.jerboa.toEnumSafe
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val accountRepository: AccountRepository) : ViewModel() {
    var postsRes: ApiState<GetPostsResponse> by mutableStateOf(ApiState.Empty)
        private set

    private var likePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var savePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var deletePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var blockCommunityRes: ApiState<BlockCommunityResponse> by mutableStateOf(ApiState.Empty)
    private var blockPersonRes: ApiState<BlockPersonResponse> by mutableStateOf(ApiState.Empty)

    val lazyListState = LazyListState()

    var sortType by mutableStateOf(SortType.Active)
        private set
    var listingType by mutableStateOf(ListingType.Local)
        private set
    private var pageCursor: PaginationCursor? by mutableStateOf(null)

    init {
        viewModelScope.launch {
            accountRepository.currentAccount
                .asFlow()
                .map { it ?: AnonAccount }
                .collect { account ->
                    updateSortType(account.defaultSortType.toEnumSafe())
                    updateListingType(account.defaultListingType.toEnumSafe())
                    Log.d("homeviewmodel", "Fetching posts")
                    resetPosts()
                }
        }
    }

    fun updateSortType(sortType: SortType) {
        this.sortType = sortType
    }

    fun updateListingType(listingType: ListingType) {
        this.listingType = listingType
    }

    private fun resetPage() {
        pageCursor = null
    }

    private fun getPosts(
        form: GetPosts,
        state: ApiState<GetPostsResponse> = ApiState.Loading,
    ) {
        viewModelScope.launch {
            postsRes = state
            postsRes =
                apiWrapper(
                    API.getInstance().getPosts(form.serializeToMap()),
                )
        }
    }

    fun appendPosts() {
        viewModelScope.launch {
            val oldRes = postsRes
            postsRes =
                when (oldRes) {
                    is ApiState.Appending -> return@launch
                    is ApiState.Holder -> {
                        ApiState.Appending(oldRes.data)
                    }
                    else -> return@launch
                }

            // Update the page cursor before fetching again
            pageCursor = oldRes.data.next_page
            val newRes = apiWrapper(API.getInstance().getPosts(getForm().serializeToMap()))

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
                        ApiState.AppendingFailure(oldRes.data)
                    }
                }
        }
    }

    fun likePost(form: CreatePostLike) {
        viewModelScope.launch {
            likePostRes = ApiState.Loading
            likePostRes = apiWrapper(API.getInstance().likePost(form))

            when (val likeRes = likePostRes) {
                is ApiState.Success -> {
                    updatePost(likeRes.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun savePost(form: SavePost) {
        viewModelScope.launch {
            savePostRes = ApiState.Loading
            savePostRes = apiWrapper(API.getInstance().savePost(form))
            when (val saveRes = savePostRes) {
                is ApiState.Success -> {
                    updatePost(saveRes.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun deletePost(form: DeletePost) {
        viewModelScope.launch {
            deletePostRes = ApiState.Loading
            deletePostRes = apiWrapper(API.getInstance().deletePost(form))
            when (val deletePost = deletePostRes) {
                is ApiState.Success -> {
                    updatePost(deletePost.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun blockCommunity(
        form: BlockCommunity,
        ctx: Context,
    ) {
        viewModelScope.launch {
            blockCommunityRes = ApiState.Loading
            blockCommunityRes =
                apiWrapper(API.getInstance().blockCommunity(form))
            showBlockCommunityToast(blockCommunityRes, ctx)
        }
    }

    fun blockPerson(
        form: BlockPerson,
        ctx: Context,
    ) {
        viewModelScope.launch {
            blockPersonRes = ApiState.Loading
            blockPersonRes = apiWrapper(API.getInstance().blockPerson(form))
            showBlockPersonToast(blockPersonRes, ctx)
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

    fun resetPosts() {
        resetPage()
        getPosts(
            GetPosts(
                page_cursor = pageCursor,
                sort = sortType,
                type_ = listingType,
            ),
        )
    }

    fun refreshPosts() {
        resetPage()
        getPosts(
            GetPosts(
                page_cursor = pageCursor,
                sort = sortType,
                type_ = listingType,
            ),
            ApiState.Refreshing,
        )
    }

    private fun getForm(): GetPosts {
        return GetPosts(
            page_cursor = pageCursor,
            sort = sortType,
            type_ = listingType,
        )
    }

    fun markPostAsRead(
        form: MarkPostAsRead,
        appState: JerboaAppState,
    ) {
        appState.coroutineScope.launch {
            when (val markRes = apiWrapper(API.getInstance().markAsRead(form))) {
                is ApiState.Success -> {
                    updatePost(markRes.data.post_view)
                }

                else -> {}
            }
        }
    }

    companion object {
        val Factory =
            viewModelFactory {
                initializer {
                    HomeViewModel(jerboaApplication().container.accountRepository)
                }
            }
    }
}
