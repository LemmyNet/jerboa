package com.jerboa.ui.components.comment

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.ui.focus.FocusManager
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.api.*
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.PersonMentionView
import com.jerboa.datatypes.PrivateMessageView
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.*
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import com.jerboa.toastException
import com.jerboa.ui.components.inbox.InboxViewModel
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun likeCommentRoutine(
    commentView: MutableState<CommentView?>,
    comments: MutableList<CommentView>? = null,
    voteType: VoteType,
    account: Account,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        commentView.value?.also { cv ->
            val updatedCommentView = likeCommentWrapper(
                cv, voteType, account,
                ctx
            )?.comment_view
            commentView.value = updatedCommentView
            comments?.also {
                findAndUpdateComment(comments, updatedCommentView)
            }
        }
    }
}

fun saveCommentRoutine(
    commentView: MutableState<CommentView?>,
    comments: MutableList<CommentView>? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        commentView.value?.also { cv ->
            val updatedCommentView = saveCommentWrapper(
                cv,
                account,
                ctx,
            )?.comment_view
            commentView.value = updatedCommentView
            comments?.also {
                findAndUpdateComment(comments, updatedCommentView)
            }
        }
    }
}

fun markCommentAsReadRoutine(
    commentView: MutableState<CommentView?>,
    comments: MutableList<CommentView>? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        commentView.value?.also { cv ->
            val updatedCommentView = markCommentAsReadWrapper(
                cv,
                account,
                ctx,
            )?.comment_view
            commentView.value = updatedCommentView
            comments?.also {
                findAndUpdateComment(comments, updatedCommentView)
            }
        }
    }
}

fun markPersonMentionAsReadRoutine(
    personMentionView: MutableState<PersonMentionView?>,
    mentions: MutableList<PersonMentionView>? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        personMentionView.value?.also { pmv ->
            val updatedPmv = markPersonMentionAsReadWrapper(
                pmv,
                account,
                ctx,
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
    parentCommentView: CommentView?,
    postId: Int,
    ctx: Context,
    scope: CoroutineScope,
    navController: NavController,
    focusManager: FocusManager,
    account: Account,
    postViewModel: PostViewModel,
    personProfileViewModel: PersonProfileViewModel,
    inboxViewModel: InboxViewModel,
) {
    scope.launch {
        loading.value = true
        val form = CreateComment(
            content = content,
            parent_id = parentCommentView?.comment?.id,
            post_id = postId,
            auth = account.jwt
        )
        val commentView = createCommentWrapper(form, ctx)?.comment_view

        loading.value = false
        focusManager.clearFocus()

        // Add to all the views which might have your comment
        if (commentView != null) {
            addCommentToMutableList(postViewModel.comments, commentView)

            // Maybe a back button would view this page.
            if (account.id == personProfileViewModel.personId.value) {
                addCommentToMutableList(personProfileViewModel.comments, commentView)
            }
        }

        // Mark as read if you replied to it, and the grandparent is you
        parentCommentView?.also { pcv ->
            if (listOf(pcv.comment.parent_id, pcv.post.creator_id).contains(account.id)) {
                val readCommentView = pcv.copy(comment = pcv.comment.copy(read = true))
                findAndUpdateComment(inboxViewModel.replies, readCommentView)
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
    postViewModel: PostViewModel,
    inboxViewModel: InboxViewModel,
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
            findAndUpdateComment(personProfileViewModel.comments, commentView.value)
            findAndUpdateComment(postViewModel.comments, commentView.value)
            findAndUpdateComment(inboxViewModel.replies, commentView.value)

            navController.navigateUp()
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
    focusManager: FocusManager,
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

fun findAndUpdateComment(
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
    replies: MutableList<CommentView>,
    loading: MutableState<Boolean>,
    page: MutableState<Int>,
    unreadOnly: MutableState<Boolean>,
    sortType: MutableState<SortType>,
    nextPage: Boolean = false,
    clear: Boolean = false,
    changeUnreadOnly: Boolean? = null,
    changeSortType: SortType? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope,
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
                sort = sortType.value.toString(),
                page = page.value,
                unread_only = unreadOnly.value,
                auth = account.jwt,
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
    sortType: MutableState<SortType>,
    nextPage: Boolean = false,
    clear: Boolean = false,
    changeUnreadOnly: Boolean? = null,
    changeSortType: SortType? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope,
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
                sort = sortType.value.toString(),
                page = page.value,
                unread_only = unreadOnly.value,
                auth = account.jwt,
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
