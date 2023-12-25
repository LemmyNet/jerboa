package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import arrow.core.Either
import com.jerboa.COMMENTS_DEPTH_MAX
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.appendData
import com.jerboa.findAndUpdateComment
import com.jerboa.model.helper.CommentsHelper
import com.jerboa.model.helper.PostsHelper
import it.vercruysse.lemmyapi.dto.CommentSortType
import it.vercruysse.lemmyapi.dto.ListingType
import it.vercruysse.lemmyapi.v0x19.datatypes.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class PostViewModel(
    val id: Either<PostId, CommentId>,
) : ViewModel(), CommentsHelper, PostsHelper {
    var postRes: ApiState<GetPostResponse> by mutableStateOf(ApiState.Empty)
        private set

    var commentsRes: ApiState<GetCommentsResponse> by mutableStateOf(ApiState.Empty)
        private set
    var sortType by mutableStateOf(CommentSortType.Hot)
        private set

    val unExpandedComments = mutableStateListOf<Int>()
    val commentsWithToggledActionBar = mutableStateListOf<Int>()

    override val scope: CoroutineScope = viewModelScope

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

    override fun updateComment(commentView: CommentView) {
        when (val existing = commentsRes) {
            is ApiState.Success -> {
                val newComments =
                    findAndUpdateComment(
                        existing.data.comments,
                        commentView,
                    )
                commentsRes = ApiState.Success(existing.data.copy(comments = newComments))
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

    override fun updatePost(postView: PostView) {
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
