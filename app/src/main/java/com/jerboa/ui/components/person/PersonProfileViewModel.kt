package com.jerboa.ui.components.person

import android.content.Context
import androidx.compose.runtime.getValue
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
import com.jerboa.datatypes.types.CommentResponse
import com.jerboa.datatypes.types.CommentView
import com.jerboa.datatypes.types.CreateCommentLike
import com.jerboa.datatypes.types.CreatePostLike
import com.jerboa.datatypes.types.DeleteComment
import com.jerboa.datatypes.types.DeletePost
import com.jerboa.datatypes.types.GetPersonDetails
import com.jerboa.datatypes.types.GetPersonDetailsResponse
import com.jerboa.datatypes.types.PostResponse
import com.jerboa.datatypes.types.PostView
import com.jerboa.datatypes.types.SaveComment
import com.jerboa.datatypes.types.SavePost
import com.jerboa.datatypes.types.SortType
import com.jerboa.findAndUpdateComment
import com.jerboa.findAndUpdatePost
import com.jerboa.serializeToMap
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockPersonToast
import com.jerboa.ui.components.common.Initializable
import kotlinx.coroutines.launch

class PersonProfileViewModel : ViewModel(), Initializable {
    override var initialized by mutableStateOf(false)

    var personDetailsRes: ApiState<GetPersonDetailsResponse> by mutableStateOf(
        ApiState.Empty,
    )
        private set

    private var likePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var savePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var deletePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var blockCommunityRes: ApiState<BlockCommunityResponse> by
        mutableStateOf(ApiState.Empty)
    private var blockPersonRes: ApiState<BlockPersonResponse> by mutableStateOf(ApiState.Empty)

    private var likeCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
    private var saveCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
    private var deleteCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var fetchingMore by mutableStateOf(false)

    var sortType by mutableStateOf(SortType.New)
        private set
    var page by mutableStateOf(1)
        private set
    var savedOnly by mutableStateOf(false)
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

    fun updateSavedOnly(savedOnly: Boolean) {
        this.savedOnly = savedOnly
    }

    fun getPersonDetails(
        form: GetPersonDetails,
    ) {
        viewModelScope.launch {
            personDetailsRes = ApiState.Loading
            personDetailsRes =
                apiWrapper(
                    API.getInstance().getPersonDetails(form.serializeToMap()),
                )
        }
    }

    fun appendData(
        form: GetPersonDetails,
    ) {
        viewModelScope.launch {
            fetchingMore = true
            val more =
                apiWrapper(
                    API.getInstance().getPersonDetails(form.serializeToMap()),
                )

            // Only append when both new and existing are Successes
            when (val existing = personDetailsRes) {
                is ApiState.Success -> {
                    when (more) {
                        is ApiState.Success -> {
                            val appendedPosts = existing.data.posts.toMutableList()
                            appendedPosts.addAll(more.data.posts)

                            val appendedComments = existing.data.comments.toMutableList()
                            appendedComments.addAll(more.data.comments)

                            val newRes = ApiState.Success(
                                existing.data.copy(
                                    posts = appendedPosts,
                                    comments = appendedComments,
                                ),
                            )
                            personDetailsRes = newRes
                            fetchingMore = false
                        }

                        is ApiState.Failure -> {
                            fetchingMore = false
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
            showBlockCommunityToast(blockCommunityRes, ctx)
        }
    }

    fun blockPerson(form: BlockPerson, ctx: Context) {
        viewModelScope.launch {
            blockPersonRes = ApiState.Loading
            blockPersonRes = apiWrapper(API.getInstance().blockPerson(form))
            showBlockPersonToast(blockPersonRes, ctx)
        }
    }

    fun likeComment(form: CreateCommentLike) {
        viewModelScope.launch {
            likeCommentRes = ApiState.Loading
            likeCommentRes = apiWrapper(API.getInstance().likeComment(form))

            when (val likeRes = likeCommentRes) {
                is ApiState.Success -> {
                    updateComment(likeRes.data.comment_view)
                }

                else -> {}
            }
        }
    }

    fun deleteComment(form: DeleteComment) {
        viewModelScope.launch {
            deleteCommentRes = ApiState.Loading
            deleteCommentRes = apiWrapper(API.getInstance().deleteComment(form))

            when (val deleteRes = deleteCommentRes) {
                is ApiState.Success -> {
                    updateComment(deleteRes.data.comment_view)
                }

                else -> {}
            }
        }
    }

    fun saveComment(form: SaveComment) {
        viewModelScope.launch {
            saveCommentRes = ApiState.Loading
            saveCommentRes = apiWrapper(API.getInstance().saveComment(form))

            when (val saveRes = saveCommentRes) {
                is ApiState.Success -> {
                    updateComment(saveRes.data.comment_view)
                }

                else -> {}
            }
        }
    }

    fun updatePost(postView: PostView) {
        when (val existing = personDetailsRes) {
            is ApiState.Success -> {
                val newPosts =
                    findAndUpdatePost(existing.data.posts, postView)
                val newRes = ApiState.Success(existing.data.copy(posts = newPosts))
                personDetailsRes = newRes
            }

            else -> {}
        }
    }

    fun updateComment(commentView: CommentView) {
        when (val existing = personDetailsRes) {
            is ApiState.Success -> {
                val newComments =
                    findAndUpdateComment(
                        existing.data.comments,
                        commentView,
                    )
                val newRes =
                    ApiState.Success(existing.data.copy(comments = newComments))
                personDetailsRes = newRes
            }

            else -> {}
        }
    }

    fun insertComment(commentView: CommentView) {
        when (val existing = personDetailsRes) {
            is ApiState.Success -> {
                val mutable = existing.data.comments.toMutableList()
                mutable.add(0, commentView)
                val newRes =
                    ApiState.Success(existing.data.copy(comments = mutable.toList()))
                personDetailsRes = newRes
            }

            else -> {}
        }
    }
}
