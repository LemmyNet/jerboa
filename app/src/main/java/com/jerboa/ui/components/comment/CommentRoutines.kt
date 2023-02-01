package com.jerboa.ui.components.comment

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.focus.FocusManager
import androidx.navigation.NavController
import com.jerboa.CommentNodeData
import com.jerboa.VoteType
import com.jerboa.api.API
import com.jerboa.api.createCommentWrapper
import com.jerboa.api.createPrivateMessageWrapper
import com.jerboa.api.deleteCommentWrapper
import com.jerboa.api.editCommentWrapper
import com.jerboa.api.likeCommentWrapper
import com.jerboa.api.markCommentReplyAsReadWrapper
import com.jerboa.api.markPersonMentionAsReadWrapper
import com.jerboa.api.retrofitErrorHandler
import com.jerboa.api.saveCommentWrapper
import com.jerboa.datatypes.CommentReplyView
import com.jerboa.datatypes.CommentSortType
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.PersonMentionView
import com.jerboa.datatypes.PrivateMessageView
import com.jerboa.datatypes.api.CreateComment
import com.jerboa.datatypes.api.CreatePrivateMessage
import com.jerboa.datatypes.api.DeleteComment
import com.jerboa.datatypes.api.EditComment
import com.jerboa.datatypes.api.GetPersonMentions
import com.jerboa.datatypes.api.GetReplies
import com.jerboa.db.Account
import com.jerboa.findAndUpdateCommentInTree
import com.jerboa.insertCommentIntoTree
import com.jerboa.serializeToMap
import com.jerboa.toastException
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun likeCommentRoutine(
    commentView: MutableState<CommentView?>,
    comments: MutableList<CommentView>? = null,
    commentTree: SnapshotStateList<CommentNodeData>? = null,
    voteType: VoteType,
    account: Account,
    ctx: Context,
    scope: CoroutineScope
) {
    scope.launch {
        commentView.value?.also { cv ->
            val updatedCommentView = likeCommentWrapper(
                cv.comment.id,
                cv.my_vote,
                voteType,
                account,
                ctx
            )?.comment_view
            commentView.value = updatedCommentView
            comments?.also {
                findAndUpdateCommentView(comments, updatedCommentView)
            }
            commentTree?.also {
                findAndUpdateCommentInTree(commentTree, updatedCommentView)
            }
        }
    }
}

fun likeCommentReplyRoutine(
    commentReplyView: CommentReplyView,
    replies: MutableList<CommentReplyView>? = null,
    voteType: VoteType,
    account: Account,
    ctx: Context,
    scope: CoroutineScope
) {
    scope.launch {
        val updatedCommentView = likeCommentWrapper(
            commentReplyView.comment.id,
            commentReplyView.my_vote,
            voteType,
            account,
            ctx
        )?.comment_view
        if (updatedCommentView != null) {
            findAndUpdateCommentReplyView(replies, commentReplyView, updatedCommentView)
        }
    }
}

fun saveCommentRoutine(
    commentView: MutableState<CommentView?>,
    comments: MutableList<CommentView>? = null,
    commentTree: SnapshotStateList<CommentNodeData>? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope
) {
    scope.launch {
        commentView.value?.also { cv ->
            val updatedCommentView = saveCommentWrapper(
                cv.comment.id,
                cv.saved,
                account,
                ctx
            )?.comment_view
            commentView.value = updatedCommentView

            comments?.also {
                findAndUpdateCommentView(comments, updatedCommentView)
            }
            commentTree?.also {
                findAndUpdateCommentInTree(commentTree, updatedCommentView)
            }
        }
    }
}

fun saveCommentReplyRoutine(
    commentReplyView: CommentReplyView,
    replies: MutableList<CommentReplyView>? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope
) {
    scope.launch {
        val updatedCommentView = saveCommentWrapper(
            commentReplyView.comment.id,
            commentReplyView.saved,
            account,
            ctx
        )?.comment_view
        if (updatedCommentView != null) {
            findAndUpdateCommentReplyView(replies, commentReplyView, updatedCommentView)
        }
    }
}

fun markCommentReplyAsReadRoutine(
    commentReplyView: CommentReplyView,
    replies: MutableList<CommentReplyView>? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope
) {
    scope.launch {
        markCommentReplyAsReadWrapper(
            commentReplyView,
            account,
            ctx
        )
        val foundIndex = replies?.indexOfFirst {
            it.comment_reply.id == commentReplyView.comment_reply.id
        }
        if (foundIndex != -1 && foundIndex != null) {
            val cr = replies[foundIndex].comment_reply
            replies[foundIndex] = replies[foundIndex].copy(
                comment_reply = cr.copy(read = !cr.read)
            )
        }
    }
}

fun markPersonMentionAsReadRoutine(
    personMentionView: MutableState<PersonMentionView?>,
    mentions: MutableList<PersonMentionView>? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope
) {
    scope.launch {
        personMentionView.value?.also { pmv ->
            val updatedPmv = markPersonMentionAsReadWrapper(
                pmv,
                account,
                ctx
            )?.person_mention_view
            personMentionView.value = updatedPmv
            mentions?.also {
                findAndUpdateMention(mentions, updatedPmv)
            }
        }
    }
}

fun createCommentRoutine(
    loading: MutableState<Boolean>,
    content: String,
    commentParentId: Int?,
    postId: Int,
    ctx: Context,
    scope: CoroutineScope,
    navController: NavController,
    focusManager: FocusManager,
    account: Account,
    postViewModel: PostViewModel,
    personProfileViewModel: PersonProfileViewModel
) {
    scope.launch {
        loading.value = true
        val form = CreateComment(
            content = content,
            parent_id = commentParentId,
            post_id = postId,
            auth = account.jwt
        )
        val commentView = createCommentWrapper(form, ctx)?.comment_view

        loading.value = false
        focusManager.clearFocus()

        // Add to all the views which might have your comment
        if (commentView != null) {
            insertCommentIntoTree(postViewModel.commentTree, commentView, postViewModel.isCommentView())

            // Maybe a back button would view this page.
            if (account.id == personProfileViewModel.res?.person_view?.person?.id) {
                addCommentToMutableList(personProfileViewModel.comments, commentView)
            }
        }

        navController.navigateUp()
    }
}

fun editCommentRoutine(
    commentView: MutableState<CommentView?>,
    loading: MutableState<Boolean>,
    content: String,
    ctx: Context,
    scope: CoroutineScope,
    navController: NavController,
    focusManager: FocusManager,
    account: Account,
    personProfileViewModel: PersonProfileViewModel,
    postViewModel: PostViewModel
) {
    scope.launch {
        commentView.value?.also { cv ->
            loading.value = true
            val form = EditComment(
                content = content,
                comment_id = cv.comment.id,
                auth = account.jwt
            )
            commentView.value = editCommentWrapper(form, ctx)?.comment_view
            loading.value = false
            focusManager.clearFocus()

            // Update all the views which might have your comment
            findAndUpdateCommentView(personProfileViewModel.comments, commentView.value)
            findAndUpdateCommentInTree(postViewModel.commentTree, commentView.value)

            navController.navigateUp()
        }
    }
}

fun deleteCommentRoutine(
    commentView: MutableState<CommentView?>,
    comments: MutableList<CommentView>? = null,
    commentTree: SnapshotStateList<CommentNodeData>? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope
) {
    scope.launch {
        commentView.value?.also { cv ->
            val form = DeleteComment(
                comment_id = cv.comment.id,
                deleted = !cv.comment.deleted,
                auth = account.jwt
            )
            val deletedCommentView = deleteCommentWrapper(form, ctx)?.comment_view
            commentView.value = deletedCommentView
            comments?.also {
                findAndUpdateCommentView(comments, deletedCommentView)
            }
            commentTree?.also {
                findAndUpdateCommentInTree(commentTree, deletedCommentView)
            }
        }
    }
}

fun createPrivateMessageRoutine(
    messages: MutableList<PrivateMessageView>? = null,
    loading: MutableState<Boolean>,
    form: CreatePrivateMessage,
    ctx: Context,
    scope: CoroutineScope,
    navController: NavController,
    focusManager: FocusManager
) {
    scope.launch {
        loading.value = true
        val pmView = createPrivateMessageWrapper(form, ctx)?.private_message_view
        pmView?.let { messages?.add(0, it) }
        loading.value = false
        focusManager.clearFocus()
        navController.navigateUp()
    }
}

fun findAndUpdateCommentReplyView(
    replies: MutableList<CommentReplyView>?,
    commentReplyView: CommentReplyView,
    updatedCommentView: CommentView
) {
    val foundIndex = replies?.indexOfFirst {
        it.comment_reply.id == commentReplyView.comment_reply.id
    }
    if (foundIndex != -1 && foundIndex != null) {
        replies[foundIndex] = replies[foundIndex].copy(
            my_vote = updatedCommentView.my_vote,
            counts = updatedCommentView.counts,
            saved = updatedCommentView.saved,
            comment = updatedCommentView.comment
        )
    }
}

fun findAndUpdateCommentView(
    comments: MutableList<CommentView>,
    updatedCommentView: CommentView?
) {
    updatedCommentView?.also { ucv ->
        val foundIndex = comments.indexOfFirst {
            it.comment.id == ucv.comment.id
        }
        if (foundIndex != -1) {
            comments[foundIndex] = ucv
        }
    }
}

fun addCommentToMutableList(
    comments: MutableList<CommentView>,
    newCommentView: CommentView
) {
    comments.add(0, newCommentView)
}

fun findAndUpdateMention(
    mentions: MutableList<PersonMentionView>,
    updatedPersonMentionView: PersonMentionView?
) {
    updatedPersonMentionView?.also { ucv ->
        val foundIndex = mentions.indexOfFirst {
            it.person_mention.id == ucv.person_mention.id
        }
        if (foundIndex != -1) {
            mentions[foundIndex] = ucv
        }
    }
}

fun fetchRepliesRoutine(
    replies: MutableList<CommentReplyView>,
    loading: MutableState<Boolean>,
    page: MutableState<Int>,
    unreadOnly: MutableState<Boolean>,
    sortType: MutableState<CommentSortType>,
    nextPage: Boolean = false,
    clear: Boolean = false,
    changeUnreadOnly: Boolean? = null,
    changeSortType: CommentSortType? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope
) {
    scope.launch {
        val api = API.getInstance()
        try {
            loading.value = true

            if (nextPage) {
                page.value++
            }

            if (clear) {
                page.value = 1
            }

            changeUnreadOnly?.also {
                unreadOnly.value = it
            }

            changeSortType?.also {
                sortType.value = it
            }

            val form = GetReplies(
                sort = sortType.value,
                page = page.value,
                unread_only = unreadOnly.value,
                auth = account.jwt
            )
            Log.d(
                "jerboa",
                "Fetching unread replies: $form"
            )
            val newReplies = retrofitErrorHandler(api.getReplies(form = form.serializeToMap()))
                .replies

            if (clear) {
                replies.clear()
            }
            replies.addAll(newReplies)
        } catch (e: Exception) {
            toastException(ctx = ctx, error = e)
        } finally {
            loading.value = false
        }
    }
}

fun fetchPersonMentionsRoutine(
    mentions: MutableList<PersonMentionView>,
    loading: MutableState<Boolean>,
    page: MutableState<Int>,
    unreadOnly: MutableState<Boolean>,
    sortType: MutableState<CommentSortType>,
    nextPage: Boolean = false,
    clear: Boolean = false,
    changeUnreadOnly: Boolean? = null,
    changeSortType: CommentSortType? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope
) {
    scope.launch {
        val api = API.getInstance()
        try {
            loading.value = true

            if (nextPage) {
                page.value++
            }

            if (clear) {
                page.value = 1
            }

            changeUnreadOnly?.also {
                unreadOnly.value = it
            }

            changeSortType?.also {
                sortType.value = it
            }

            val form = GetPersonMentions(
                sort = sortType.value,
                page = page.value,
                unread_only = unreadOnly.value,
                auth = account.jwt
            )
            Log.d(
                "jerboa",
                "Fetching unread replies: $form"
            )
            val newMentions = retrofitErrorHandler(
                api.getPersonMentions(
                    form = form
                        .serializeToMap()
                )
            ).mentions

            if (clear) {
                mentions.clear()
            }
            mentions.addAll(newMentions)
        } catch (e: Exception) {
            toastException(ctx = ctx, error = e)
        } finally {
            loading.value = false
        }
    }
}
