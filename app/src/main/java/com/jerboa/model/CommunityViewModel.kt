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
import arrow.core.Either
import com.jerboa.JerboaAppState
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.findAndUpdatePost
import com.jerboa.mergePosts
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockPersonToast
import it.vercruysse.lemmyapi.dto.SortType
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunityResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPerson
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPersonResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityId
import it.vercruysse.lemmyapi.v0x19.datatypes.CommunityResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.CreatePostLike
import it.vercruysse.lemmyapi.v0x19.datatypes.DeletePost
import it.vercruysse.lemmyapi.v0x19.datatypes.FollowCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.GetCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.GetCommunityResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.GetPosts
import it.vercruysse.lemmyapi.v0x19.datatypes.GetPostsResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.MarkPostAsRead
import it.vercruysse.lemmyapi.v0x19.datatypes.PostResponse
import it.vercruysse.lemmyapi.v0x19.datatypes.PostView
import it.vercruysse.lemmyapi.v0x19.datatypes.SavePost
import kotlinx.coroutines.launch

class CommunityViewModel(communityArg: Either<CommunityId, String>) : ViewModel() {
    var communityRes: ApiState<GetCommunityResponse> by mutableStateOf(ApiState.Empty)
        private set

    private var followCommunityRes: ApiState<CommunityResponse> by
        mutableStateOf(ApiState.Empty)

    var postsRes: ApiState<GetPostsResponse> by mutableStateOf(ApiState.Empty)
        private set

    private var likePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var savePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var deletePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var blockCommunityRes: ApiState<BlockCommunityResponse> by
        mutableStateOf(ApiState.Empty)
    private var blockPersonRes: ApiState<BlockPersonResponse> by mutableStateOf(ApiState.Empty)
    private var markPostRes: ApiState<Unit> by mutableStateOf(ApiState.Empty)

    var sortType by mutableStateOf(SortType.Active)
        private set

    var page by mutableIntStateOf(1)
        private set
    private var pageCursor: PaginationCursor? by mutableStateOf(null)

    private var communityId: Int? by mutableStateOf(null)
    private var communityName: String? by mutableStateOf(null)

    fun updateSortType(sortType: SortType) {
        this.sortType = sortType
    }

    private fun resetPage() {
        page = 1
        pageCursor = null
    }

    private fun getCommunity(form: GetCommunity) {
        viewModelScope.launch {
            communityRes = ApiState.Loading
            communityRes = API.getInstance().getCommunity(form).toApiState()
        }
    }

    private fun getPosts(
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
                        ApiState.Success(
                            GetPostsResponse(
                                posts =
                                    mergePosts(
                                        oldRes.data.posts,
                                        newRes.data.posts,
                                    ),
                                next_page = newRes.data.next_page,
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

    fun followCommunity(
        form: FollowCommunity,
        onSuccess: () -> Unit = {},
    ) {
        viewModelScope.launch {
            followCommunityRes = ApiState.Loading
            followCommunityRes = API.getInstance().followCommunity(form).toApiState()

            // Copy that response to the communityRes
            when (val followRes = followCommunityRes) {
                is ApiState.Success -> {
                    val cv = followRes.data.community_view
                    when (val cRes = communityRes) {
                        is ApiState.Success -> {
                            val newCRes = cRes.data.copy(community_view = cv)
                            communityRes = ApiState.Success(newCRes)
                            onSuccess()
                        }

                        else -> {}
                    }
                }

                else -> {}
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

            when (val blockCommunity = blockCommunityRes) {
                is ApiState.Success -> {
                    when (val existing = communityRes) {
                        is ApiState.Success -> {
                            val newRes =
                                ApiState.Success(
                                    existing.data.copy(
                                        community_view =
                                            blockCommunity.data.community_view,
                                    ),
                                )
                            communityRes = newRes
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
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

    fun markPostAsRead(
        form: MarkPostAsRead,
        post: PostView,
        appState: JerboaAppState,
    ) {
        appState.coroutineScope.launch {
            markPostRes = ApiState.Loading
            markPostRes = API.getInstance().markPostAsRead(form).toApiState()


            when (markPostRes) {
                is ApiState.Success -> {
                    updatePost(post.copy(read = form.read))
                }

                else -> {}
            }
        }
    }

    init {
        communityId = communityArg.fold({ it }, { null })
        communityName = communityArg.fold({ null }, { it })

        this.resetPage()

        this.getCommunity(
            form =
                GetCommunity(
                    id = communityId,
                    name = communityName,
                ),
        )
        this.getPosts(
            getForm(),
        )
    }

    private fun getForm(): GetPosts {
        return GetPosts(
            community_id = communityId,
            community_name = communityName,
            page_cursor = pageCursor,
            sort = sortType,
        )
    }

    companion object {
        class Factory(
            private val id: Either<CommunityId, String>,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                return CommunityViewModel(id) as T
            }
        }
    }
}
