package com.jerboa.model

import android.content.Context
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.jerboa.api.toApiState
import com.jerboa.db.entity.AnonAccount
import com.jerboa.db.repository.AccountRepository
import com.jerboa.findAndUpdatePost
import com.jerboa.jerboaApplication
import com.jerboa.mergePosts
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockPersonToast
import com.jerboa.toEnumSafe
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.dto.SortType
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunityResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPerson
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPersonResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.CreatePostLike
import it.vercruysse.lemmyapi.v0x19.datatypes.DeletePost
import it.vercruysse.lemmyapi.v0x19.datatypes.GetPosts
import it.vercruysse.lemmyapi.v0x19.datatypes.GetPostsResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.MarkPostAsRead
import it.vercruysse.lemmyapi.v0x19.datatypes.PostResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.PostView
import it.vercruysse.lemmyapi.v0x19.datatypes.SavePost
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
    var page by mutableIntStateOf(1)
        private set

    init {
        viewModelScope.launch {
            accountRepository.currentAccount
                .asFlow()
                .map { it ?: AnonAccount }
                .collect { account ->
                    updateSortType(account.defaultSortType.toEnumSafe())
                    updateListingType(account.defaultListingType.toEnumSafe())
                    Log.d("jerboa", "Fetching posts")
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

    fun resetPage() {
        page = 1
    }

    fun nextPage() {
        page += 1
    }

    fun prevPage() {
        page -= 1
    }

    fun getPosts(
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

            nextPage()
            val newRes = API.getInstance().getPosts(getForm()).toApiState()

            postsRes =
                when (newRes) {
                    is ApiState.Success -> {
                        ApiState.Success(
                            GetPostsResponse(
                                mergePosts(
                                    oldRes.data.posts,
                                    newRes.data.posts,
                                ),
                            ),
                        )
                    }

                    else -> {
                        prevPage()
                        ApiState.AppendingFailure(oldRes.data)
                    }
                }
        }
    }

    fun likePost(form: CreatePostLike) {
        viewModelScope.launch {
            likePostRes = ApiState.Loading
            likePostRes = API.getInstance().createPostLike(form).toApiState()

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
            savePostRes = API.getInstance().savePost(form).toApiState()
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
            deletePostRes = API.getInstance().deletePost(form).toApiState()
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
            blockCommunityRes = API.getInstance().blockCommunity(form).toApiState()
            showBlockCommunityToast(blockCommunityRes, ctx)
        }
    }

    fun blockPerson(
        form: BlockPerson,
        ctx: Context,
    ) {
        viewModelScope.launch {
            blockPersonRes = ApiState.Loading
            blockPersonRes = API.getInstance().blockPerson(form).toApiState()
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
                sort = sortType,
                type_ = listingType,
            ),
        )
    }

    fun refreshPosts() {
        resetPage()
        getPosts(
            GetPosts(
                page = page,
                sort = sortType,
                type_ = listingType,
            ),
            ApiState.Refreshing,
        )
    }

    fun getForm(): GetPosts {
        return GetPosts(
            page = page,
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

            when ( API.getInstance().markPostAsRead(form).toApiState()) {
                is ApiState.Success -> {
                    updatePost(post.copy(read = form.read))
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
