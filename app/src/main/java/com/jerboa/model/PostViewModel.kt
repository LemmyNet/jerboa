package com.jerboa.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import arrow.core.Either
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.appendData
import com.jerboa.findAndUpdateComment
import com.jerboa.showBlockCommunityToast
import com.jerboa.showBlockPersonToast
import it.vercruysse.lemmyapi.dto.CommentSortType
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.v0x19.datatypes.*
import kotlinx.coroutines.launch

const val COMMENTS_DEPTH_MAX = 6

class PostViewModel(val id: Either<PostId, CommentId>) : ViewModel() {
    var postRes: ApiState<GetPostResponse> by mutableStateOf(ApiState.Empty)
        private set

    var commentsRes: ApiState<GetCommentsResponse> by mutableStateOf(ApiState.Empty)
        private set
    var sortType by mutableStateOf(CommentSortType.Hot)
        private set

    private var likeCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
    private var saveCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
    private var deleteCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var likePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var savePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var deletePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var blockCommunityRes: ApiState<BlockCommunityResponse> by mutableStateOf(ApiState.Empty)
    private var blockPersonRes: ApiState<BlockPersonResponse> by mutableStateOf(ApiState.Empty)

    val unExpandedComments = mutableStateListOf<Int>()
    val commentsWithToggledActionBar = mutableStateListOf<Int>()

    init {
        this.getData()
    }

    fun updateSortType(sortType: CommentSortType) {
        this.sortType = sortType
    }

    fun getData(state: ApiState<GetPostResponse> = ApiState.Loading) {
        viewModelScope.launch {
            // Set the commentId for the right case
            val postForm =
                id.fold({
                    GetPost(id = it)
                }, {
                    GetPost(comment_id = it)
                })

            postRes = state
            postRes = API.getInstance().getPost(postForm).toApiState()

            val commentsForm =
                id.fold({
                    GetComments(
                        max_depth = COMMENTS_DEPTH_MAX,
                        type_ = ListingType.All,
                        post_id = it,
                        sort = sortType,
                    )
                }, {
                    GetComments(
                        max_depth = COMMENTS_DEPTH_MAX,
                        type_ = ListingType.All,
                        parent_id = it,
                        sort = sortType,
                    )
                })

            commentsRes = ApiState.Loading
            commentsRes = API.getInstance().getComments(commentsForm).toApiState()
        }
    }

    fun isCommentView(): Boolean {
        return id.isRight()
    }

    fun fetchMoreChildren(commentView: CommentView) {
        viewModelScope.launch {
            val existing = commentsRes
            when (existing) {
                is ApiState.Success -> commentsRes = ApiState.Appending(existing.data)
                else -> return@launch
            }

            val commentsForm =
                GetComments(
                    parent_id = commentView.comment.id,
                    max_depth = COMMENTS_DEPTH_MAX,
                    type_ = ListingType.All,
                )

            val moreComments = API.getInstance().getComments(commentsForm).toApiState()

            when (moreComments) {
                is ApiState.Success -> {
                    // Remove the first comment, since it is a parent
                    // Actually since a bug in 18.3 that is no longer a guarantee
                    // see https://github.com/LemmyNet/lemmy/issues/3767
                    val newComments = moreComments.data.comments.toMutableList()
                    newComments.removeIf { it.comment.id == commentView.comment.id }

                    val appended = appendData(existing.data.comments, newComments.toList())

                    commentsRes = ApiState.Success(existing.data.copy(comments = appended))
                }

                else -> {}
            }
        }
    }

    fun likeComment(form: CreateCommentLike) {
        viewModelScope.launch {
            likeCommentRes = ApiState.Loading
            likeCommentRes = API.getInstance().createCommentLike(form).toApiState()

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
            deleteCommentRes = API.getInstance().deleteComment(form).toApiState()

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
            saveCommentRes = API.getInstance().saveComment(form).toApiState()

            when (val saveRes = saveCommentRes) {
                is ApiState.Success -> {
                    updateComment(saveRes.data.comment_view)
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

    fun updateComment(commentView: CommentView) {
        when (val existing = commentsRes) {
            is ApiState.Success -> {
                val newComments =
                    findAndUpdateComment(
                        existing.data.comments,
                        commentView,
                    )
                val newRes =
                    ApiState.Success(existing.data.copy(comments = newComments))
                commentsRes = newRes
            }

            else -> {}
        }
    }

    fun appendComment(commentView: CommentView) {
        when (val existing = commentsRes) {
            is ApiState.Success -> {
                val mutable = existing.data.comments.toMutableList()
                mutable.add(commentView)
                val newRes =
                    ApiState.Success(existing.data.copy(comments = mutable.toList()))
                commentsRes = newRes
            }

            else -> {}
        }
    }

    fun updatePost(postView: PostView) {
        when (val existing = postRes) {
            is ApiState.Success -> {
                val newRes = ApiState.Success(existing.data.copy(post_view = postView))
                postRes = newRes
            }

            else -> {}
        }
    }

    companion object {
        class Factory(
            private val id: Either<PostId, CommentId>,
        ) : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras,
            ): T {
                return PostViewModel(id) as T
            }
        }
    }
}
