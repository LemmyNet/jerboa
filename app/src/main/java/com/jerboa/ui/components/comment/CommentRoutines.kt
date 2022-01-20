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
            ).comment_view
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
            ).comment_view
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
            ).comment_view
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
            ).person_mention_view
            personMentionView.value = updatedPmv
            mentions?.also {
                findAndUpdateMention(mentions, updatedPmv)
            }
        }
    }
}

fun markPrivateMessageAsReadRoutine(
    privateMessageView: MutableState<PrivateMessageView?>,
    messages: MutableList<PrivateMessageView>? = null,
    account: Account,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        privateMessageView.value?.also { pmv ->
            val updatedPmv = markPrivateMessageAsReadWrapper(
                pmv,
                account,
                ctx,
            ).private_message_view
            privateMessageView.value = updatedPmv
            messages?.also {
                findAndUpdatePrivateMessage(messages, updatedPmv)
            }
        }
    }
}

fun createCommentRoutine(
    comments: MutableList<CommentView>? = null,
    loading: MutableState<Boolean>,
    form: CreateComment,
    ctx: Context,
    scope: CoroutineScope,
    navController: NavController,
    focusManager: FocusManager,
) {
    scope.launch {
        loading.value = true
        val commentView = createCommentWrapper(form, ctx).comment_view
        comments?.add(0, commentView)
        loading.value = false
        focusManager.clearFocus()
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
            commentView.value = editCommentWrapper(form, ctx).comment_view
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
        val pmView = createPrivateMessageWrapper(form, ctx).private_message_view
        messages?.add(0, pmView)
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

fun findAndUpdatePrivateMessage(
    messages: MutableList<PrivateMessageView>,
    updatedMessageView: PrivateMessageView?
) {
    updatedMessageView?.also { ucv ->
        val foundIndex = messages.indexOfFirst {
            it.private_message.id == ucv.private_message.id
        }
        if (foundIndex != -1) {
            messages[foundIndex] = ucv
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
            val newReplies = api.getReplies(form = form.serializeToMap()).replies

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
            val newMentions = api.getPersonMentions(form = form.serializeToMap()).mentions

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

fun fetchPrivateMessagesRoutine(
    messages: MutableList<PrivateMessageView>,
    loading: MutableState<Boolean>,
    page: MutableState<Int>,
    unreadOnly: MutableState<Boolean>,
    nextPage: Boolean = false,
    clear: Boolean = false,
    changeUnreadOnly: Boolean? = null,
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

            val form = GetPrivateMessages(
                page = page.value,
                unread_only = unreadOnly.value,
                auth = account.jwt,
            )
            Log.d(
                "jerboa",
                "Fetching unread replies: $form"
            )
            val newMessages = api.getPrivateMessages(form = form.serializeToMap()).private_messages

            if (clear) {
                messages.clear()
            }
            messages.addAll(newMessages)
        } catch (e: Exception) {
            toastException(ctx = ctx, error = e)
        } finally {
            loading.value = false
        }
    }
}
