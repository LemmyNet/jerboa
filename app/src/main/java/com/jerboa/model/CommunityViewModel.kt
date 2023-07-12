package com.jerboa.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.BlockCommunity
import com.jerboa.datatypes.types.BlockCommunityResponse
import com.jerboa.datatypes.types.BlockPerson
import com.jerboa.datatypes.types.BlockPersonResponse
import com.jerboa.datatypes.types.CommunityId
import com.jerboa.datatypes.types.CommunityResponse
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.FollowCommunity
import com.jerboa.datatypes.types.GetCommunity
import com.jerboa.datatypes.types.GetCommunityResponse
import com.jerboa.datatypes.types.GetPosts
import com.jerboa.datatypes.types.GetPostsResponse
import com.jerboa.datatypes.types.PostResponse
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.SavePost
import com.jerboa.datatypes.types.SortType
import com.jerboa.findAndUpdatePost
import com.jerboa.mergePosts
import com.jerboa.serializeToMap
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockPersonToast
import com.jerboa.ui.components.common.Initializable
import kotlinx.coroutines.launch

class CommunityViewModel : ViewModel(), Initializable {
    override var initialized by mutableStateOf(false)

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

    var sortType by mutableStateOf(SortType.Active)
        private set
    var page by mutableIntStateOf(1)
        private set

    fun updateSortType(sortType: SortType) {
        this.sortType = sortType
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

    fun getCommunity(form: GetCommunity) {
        viewModelScope.launch {
            communityRes = ApiState.Loading
            communityRes =
                apiWrapper(
                    API.getInstance().getCommunity(form.serializeToMap()),
                )
        }
    }

    fun getPosts(form: GetPosts, state: ApiState<GetPostsResponse> = ApiState.Loading) {
        viewModelScope.launch {
            postsRes = state
            postsRes =
                apiWrapper(
                    API.getInstance().getPosts(form.serializeToMap()),
                )
        }
    }

    fun appendPosts(id: CommunityId, jwt: String?) {
        viewModelScope.launch {
            val oldRes = postsRes
            when (oldRes) {
                is ApiState.Success -> postsRes = ApiState.Appending(oldRes.data)
                else -> return@launch
            }

            nextPage()
            val form = GetPosts(
                community_id = id,
                page = page,
                sort = sortType,
                auth = jwt,
            )

            val newRes = apiWrapper(API.getInstance().getPosts(form.serializeToMap()))

            postsRes = when (newRes) {
                is ApiState.Success -> {
                    if (newRes.data.posts.isEmpty()) { // Hit the end of the posts
                        prevPage()
                    }
                    ApiState.Success(GetPostsResponse(mergePosts(oldRes.data.posts, newRes.data.posts)))
                }
                else -> {
                    prevPage()
                    oldRes
                }
            }
        }
    }

    fun followCommunity(form: FollowCommunity, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            followCommunityRes = ApiState.Loading
            followCommunityRes =
                apiWrapper(API.getInstance().followCommunity(form))

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

    fun blockCommunity(form: BlockCommunity, ctx: Context) {
        viewModelScope.launch {
            blockCommunityRes = ApiState.Loading
            blockCommunityRes =
                apiWrapper(API.getInstance().blockCommunity(form))

            when (val blockCommunity = blockCommunityRes) {
                is ApiState.Success -> {
                    showBlockCommunityToast(blockCommunity, ctx)

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

    fun blockPerson(form: BlockPerson, ctx: Context) {
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
}
