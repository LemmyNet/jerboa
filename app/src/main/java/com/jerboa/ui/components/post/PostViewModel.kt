package com.jerboa.ui.components.post

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.jerboa.CommentNodeData
import com.jerboa.VoteType
import com.jerboa.api.API
import com.jerboa.api.retrofitErrorHandler
import com.jerboa.buildCommentsTree
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.CommunityModeratorView
import com.jerboa.datatypes.ListingType
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.PostView
import com.jerboa.datatypes.api.GetComments
import com.jerboa.datatypes.api.GetPost
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import com.jerboa.toastException
import com.jerboa.ui.components.comment.deleteCommentRoutine
import com.jerboa.ui.components.comment.likeCommentRoutine
import com.jerboa.ui.components.comment.saveCommentRoutine
import com.jerboa.ui.components.community.blockCommunityRoutine
import com.jerboa.ui.components.person.blockPersonRoutine
import kotlinx.coroutines.launch

const val COMMENTS_DEPTH_MAX = 6

typealias PostId = Int
typealias CommentId = Int

class PostViewModel : ViewModel() {

    var postView = mutableStateOf<PostView?>(null)
        private set

    // If this is set, its a comment type view
    var commentId = mutableStateOf<Int?>(null)
        private set
    var comments = mutableStateListOf<CommentView>()
        private set
    var commentTree = mutableStateListOf<CommentNodeData>()
        private set
    var moderators = mutableStateListOf<CommunityModeratorView>()
        private set
    var loading: Boolean by mutableStateOf(false)
        private set

    fun fetchPost(
        id: Either<PostId, CommentId>,
        clear: Boolean = false,
        account: Account?,
        ctx: Context
    ) {
        val api = API.getInstance()

        // Set the commentId for the right case
        id.fold({ commentId.value = null }, { commentId.value = it })

        viewModelScope.launch {
            try {
                Log.d(
                    "jerboa",
                    "Fetching post: $id"
                )

                if (clear) {
                    postView.value = null
                }

                loading = true

                val postForm = id.fold({
                    GetPost(id = it, auth = account?.jwt)
                }, {
                    GetPost(comment_id = it, auth = account?.jwt)
                })

                val postOut = retrofitErrorHandler(api.getPost(form = postForm.serializeToMap()))
                postView.value = postOut.post_view

                val commentsForm = id.fold({
                    GetComments(
                        max_depth = COMMENTS_DEPTH_MAX,
                        type_ = ListingType.All,
                        post_id = it,
                        auth = account?.jwt
                    )
                }, {
                    GetComments(
                        max_depth = COMMENTS_DEPTH_MAX,
                        type_ = ListingType.All,
                        parent_id = it,
                        auth = account?.jwt
                    )
                })

                val commentsOut = retrofitErrorHandler(
                    api.getComments(
                        commentsForm
                            .serializeToMap()
                    )
                )
                comments.clear()
                comments.addAll(commentsOut.comments)

                commentTree.clear()
                commentTree.addAll(buildCommentsTree(comments, isCommentView()))
                moderators.clear()
                moderators.addAll(postOut.moderators)
            } catch (e: Exception) {
                toastException(ctx, e)
            } finally {
                loading = false
            }
        }
    }

    fun isCommentView(): Boolean {
        return commentId.value != null
    }

    fun fetchMoreChildren(
        commentView: CommentView,
        account: Account?,
        ctx: Context
    ) {
        val api = API.getInstance()
        val commentId = commentView.comment.id

        viewModelScope.launch {
            try {
                Log.d(
                    "jerboa",
                    "Fetching more children for comment: $commentId"
                )
                val commentsForm = GetComments(
                    parent_id = commentView.comment.id,
                    max_depth = COMMENTS_DEPTH_MAX,
                    type_ = ListingType.All,
                    auth = account?.jwt
                )
                val commentsOut = retrofitErrorHandler(
                    api.getComments(
                        commentsForm
                            .serializeToMap()
                    )
                )

                // Remove the first comment, since it is a parent
                val newComments = commentsOut.comments.toMutableList()
                newComments.removeAt(0)

                comments.addAll(newComments)
                commentTree.clear()
                commentTree.addAll(buildCommentsTree(comments, isCommentView()))
            } catch (e: Exception) {
                toastException(ctx, e)
            } // TODO do the more comments loading
//            finally {
//            loading = false
//        }
        }
    }

    fun likeComment(
        commentView: CommentView,
        voteType: VoteType,
        account: Account,
        ctx: Context
    ) {
        likeCommentRoutine(
            commentView = mutableStateOf(commentView),
            voteType = voteType,
            commentTree = commentTree,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }

    fun deleteComment(commentView: CommentView, account: Account, ctx: Context) {
        deleteCommentRoutine(
            commentView = mutableStateOf(commentView),
            commentTree = commentTree,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }

    fun savePost(
        account: Account,
        ctx: Context
    ) {
        savePostRoutine(postView = postView, account = account, ctx = ctx, scope = viewModelScope)
    }

    fun deletePost(account: Account, ctx: Context) {
        deletePostRoutine(
            postView = postView,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }

    fun saveComment(
        commentView: CommentView,
        account: Account,
        ctx: Context
    ) {
        saveCommentRoutine(
            commentView = mutableStateOf(commentView),
            commentTree = commentTree,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }

    fun blockCommunity(
        account: Account,
        ctx: Context
    ) {
        postView.value?.community?.also {
            blockCommunityRoutine(
                community = it,
                block = true,
                account = account,
                ctx = ctx,
                scope = viewModelScope
            )
        }
    }

    fun blockCreator(
        creator: PersonSafe,
        account: Account,
        ctx: Context
    ) {
        blockPersonRoutine(
            person = creator,
            block = true,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }

    fun likePost(voteType: VoteType, account: Account?, ctx: Context) {
        likePostRoutine(
            postView = postView,
            voteType = voteType,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }
}
