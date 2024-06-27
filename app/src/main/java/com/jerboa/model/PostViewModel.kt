package com.jerboa.model

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import arrow.core.Either
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.appendData
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.feat.showBlockPersonToast
import com.jerboa.findAndUpdateComment
import com.jerboa.findAndUpdateCommentCreator
import com.jerboa.findAndUpdateCommentCreatorBannedFromCommunity
import it.vercruysse.lemmyapi.datatypes.BlockPerson
import it.vercruysse.lemmyapi.datatypes.BlockPersonResponse
import it.vercruysse.lemmyapi.datatypes.CommentId
import it.vercruysse.lemmyapi.datatypes.CommentResponse
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.CreateCommentLike
import it.vercruysse.lemmyapi.datatypes.CreatePostLike
import it.vercruysse.lemmyapi.datatypes.DeleteComment
import it.vercruysse.lemmyapi.datatypes.DeletePost
import it.vercruysse.lemmyapi.datatypes.DistinguishComment
import it.vercruysse.lemmyapi.datatypes.FeaturePost
import it.vercruysse.lemmyapi.datatypes.GetComments
import it.vercruysse.lemmyapi.datatypes.GetCommentsResponse
import it.vercruysse.lemmyapi.datatypes.GetPost
import it.vercruysse.lemmyapi.datatypes.GetPostResponse
import it.vercruysse.lemmyapi.datatypes.HidePost
import it.vercruysse.lemmyapi.datatypes.LockPost
import it.vercruysse.lemmyapi.datatypes.PersonView
import it.vercruysse.lemmyapi.datatypes.PostId
import it.vercruysse.lemmyapi.datatypes.PostResponse
import it.vercruysse.lemmyapi.datatypes.PostView
import it.vercruysse.lemmyapi.datatypes.SaveComment
import it.vercruysse.lemmyapi.datatypes.SavePost
import it.vercruysse.lemmyapi.dto.CommentSortType
import it.vercruysse.lemmyapi.dto.ListingType
import kotlinx.coroutines.launch

class PostViewModel(
    val id: Either<PostId, CommentId>,
) : ViewModel() {
    var postRes: ApiState<GetPostResponse> by mutableStateOf(ApiState.Empty)
        private set

    var commentsRes: ApiState<GetCommentsResponse> by mutableStateOf(ApiState.Empty)
        private set
    var sortType by mutableStateOf(CommentSortType.Hot)
        private set

    private var likeCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
    private var saveCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
    private var deleteCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
    private var distinguishCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)

    private var likePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var savePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var deletePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var hidePostRes: ApiState<(Unit)> by mutableStateOf(ApiState.Empty)
    private var lockPostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var featurePostRes: ApiState<PostResponse> by mutableStateOf(ApiState.Empty)
    private var blockPersonRes: ApiState<BlockPersonResponse> by mutableStateOf(ApiState.Empty)

    val unExpandedComments = mutableStateListOf<CommentId>()
    val commentsWithToggledActionBar = mutableStateListOf<CommentId>()

    init {
        this.getData()
    }

    fun updateSortType(sortType: CommentSortType) {
        this.sortType = sortType
    }

    fun getData(state: ApiState<GetPostResponse> = ApiState.Loading) {
        val postForm =
            id.fold({
                GetPost(id = it)
            }, {
                GetPost(comment_id = it)
            })

        postRes = state
        viewModelScope.launch {
            postRes = API.getInstance().getPost(postForm).toApiState()
        }

        getComments()
    }

    fun getComments() {
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
        viewModelScope.launch {
            commentsRes = API.getInstance().getComments(commentsForm).toApiState()
        }
    }

    fun isCommentView(): Boolean = id.isRight()

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

            when (val moreComments = API.getInstance().getComments(commentsForm).toApiState()) {
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

    fun distinguishComment(form: DistinguishComment) {
        viewModelScope.launch {
            distinguishCommentRes = ApiState.Loading
            distinguishCommentRes = API.getInstance().distinguishComment(form).toApiState()

            when (val distinguishRes = distinguishCommentRes) {
                is ApiState.Success -> {
                    updateComment(distinguishRes.data.comment_view)
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

    fun hidePost(
        form: HidePost,
        ctx: Context,
    ) {
        viewModelScope.launch {
            hidePostRes = ApiState.Loading
            hidePostRes = API.getInstance().hidePost(form).toApiState()
            val msg = if (form.hide) R.string.post_hidden else R.string.post_unhidden
            when (hidePostRes) {
                is ApiState.Success -> {
                    updatePostHidden(form.hide)
                    Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show()
                }
                else -> {}
            }
        }
    }

    fun lockPost(form: LockPost) {
        viewModelScope.launch {
            lockPostRes = ApiState.Loading
            lockPostRes = API.getInstance().lockPost(form).toApiState()
            when (val lockPost = lockPostRes) {
                is ApiState.Success -> {
                    updatePost(lockPost.data.post_view)
                }

                else -> {}
            }
        }
    }

    fun featurePost(form: FeaturePost) {
        viewModelScope.launch {
            featurePostRes = ApiState.Loading
            featurePostRes = API.getInstance().featurePost(form).toApiState()
            when (val featurePost = featurePostRes) {
                is ApiState.Success -> {
                    updatePost(featurePost.data.post_view)
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
            val res = API.getInstance().blockPerson(form)
            blockPersonRes = res.toApiState()
            showBlockPersonToast(res, ctx)
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

    fun updatePostHidden(hidden: Boolean) {
        when (val existing = postRes) {
            is ApiState.Success -> {
                val newPostView = existing.data.post_view.copy(hidden = hidden)
                val newRes = ApiState.Success(existing.data.copy(post_view = newPostView))
                postRes = newRes
            }

            else -> {}
        }
    }

    fun updateBanned(personView: PersonView) {
        when (val existing = postRes) {
            is ApiState.Success -> {
                val data = existing.data
                // Only update the creator if it matches
                val newPostView =
                    if (data.post_view.creator.id == personView.person.id) {
                        data.post_view.copy(creator = personView.person)
                    } else {
                        data.post_view
                    }
                val newRes = ApiState.Success(data.copy(post_view = newPostView))
                postRes = newRes
            }
            else -> {}
        }

        // Also do all the comments
        when (val existing = commentsRes) {
            is ApiState.Success -> {
                val comments = findAndUpdateCommentCreator(existing.data.comments, personView.person)
                val newRes = ApiState.Success(existing.data.copy(comments = comments))
                commentsRes = newRes
            }
            else -> {}
        }
    }

    fun updateBannedFromCommunity(banData: BanFromCommunityData) {
        when (val existing = postRes) {
            is ApiState.Success -> {
                val data = existing.data
                // Only update the creator if it matches
                val newPostView =
                    if (data.post_view.creator.id == banData.person.id && data.post_view.community.id == banData.community.id) {
                        data.post_view.copy(creator_banned_from_community = banData.banned)
                    } else {
                        data.post_view
                    }
                val newRes = ApiState.Success(data.copy(post_view = newPostView))
                postRes = newRes
            }
            else -> {}
        }

        // Also do all the comments
        when (val existing = commentsRes) {
            is ApiState.Success -> {
                val comments = findAndUpdateCommentCreatorBannedFromCommunity(existing.data.comments, banData)
                val newRes = ApiState.Success(existing.data.copy(comments = comments))
                commentsRes = newRes
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
            ): T = PostViewModel(id) as T
        }

        const val COMMENTS_DEPTH_MAX = 6L
    }
}
