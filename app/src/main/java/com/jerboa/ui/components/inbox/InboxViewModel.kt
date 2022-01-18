package com.jerboa.ui.components.post

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.api.API
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.PersonMentionView
import com.jerboa.datatypes.PrivateMessageView
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.CreateComment
import com.jerboa.datatypes.api.CreatePrivateMessage
import com.jerboa.datatypes.api.MarkAllAsRead
import com.jerboa.db.Account
import com.jerboa.toastException
import com.jerboa.ui.components.comment.*
import kotlinx.coroutines.launch

class InboxViewModel : ViewModel() {

    var replies = mutableStateListOf<CommentView>()
        private set
    var mentions = mutableStateListOf<PersonMentionView>()
        private set
    var messages = mutableStateListOf<PrivateMessageView>()
        private set
    var page = mutableStateOf(1)
        private set
    var sortType = mutableStateOf(SortType.Active)
        private set
    var unreadOnly = mutableStateOf(true)
        private set
    var loading = mutableStateOf(false)
        private set
    var replyToPrivateMessageView: PrivateMessageView? = null
    var privateMessageReplyLoading = mutableStateOf(false)
        private set

    fun fetchReplies(
        account: Account,
        nextPage: Boolean = false,
        clear: Boolean = false,
        changeSortType: SortType? = null,
        changeUnreadOnly: Boolean? = null,
        ctx: Context,
    ) {
        fetchRepliesRoutine(
            replies = replies,
            loading = loading,
            page = page,
            unreadOnly = unreadOnly,
            sortType = sortType,
            nextPage = nextPage,
            clear = clear,
            changeUnreadOnly = changeUnreadOnly,
            changeSortType = changeSortType,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun fetchPersonMentions(
        account: Account,
        nextPage: Boolean = false,
        clear: Boolean = false,
        changeSortType: SortType? = null,
        changeUnreadOnly: Boolean? = null,
        ctx: Context,
    ) {
        fetchPersonMentionsRoutine(
            mentions = mentions,
            loading = loading,
            page = page,
            unreadOnly = unreadOnly,
            sortType = sortType,
            nextPage = nextPage,
            clear = clear,
            changeUnreadOnly = changeUnreadOnly,
            changeSortType = changeSortType,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun fetchPrivateMessages(
        account: Account,
        nextPage: Boolean = false,
        clear: Boolean = false,
        changeUnreadOnly: Boolean? = null,
        ctx: Context,
    ) {
        fetchPrivateMessagesRoutine(
            messages = messages,
            loading = loading,
            page = page,
            unreadOnly = unreadOnly,
            nextPage = nextPage,
            clear = clear,
            changeUnreadOnly = changeUnreadOnly,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun likeComment(
        commentView: CommentView,
        voteType: VoteType,
        account: Account,
        ctx: Context,
    ) {
        likeCommentRoutine(
            commentView = mutableStateOf(commentView),
            voteType = voteType,
            // TODO find a way to get this to set the mention likes too
            comments = replies,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun createComment(
        form: CreateComment,
        ctx: Context,
        navController: NavController,
        focusManager: FocusManager,
    ) {
        createCommentRoutine(
            comments = replies,
            loading = loading,
            form = form,
            ctx = ctx,
            scope = viewModelScope,
            navController = navController,
            focusManager = focusManager,
        )
    }

    fun saveComment(
        commentView: CommentView,
        account: Account,
        ctx: Context,
    ) {
        saveCommentRoutine(
            commentView = mutableStateOf(commentView),
            comments = replies,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun markReplyAsRead(
        commentView: CommentView,
        account: Account,
        ctx: Context,
    ) {
        markCommentAsReadRoutine(
            commentView = mutableStateOf(commentView),
            comments = replies,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun markPersonMentionAsRead(
        personMentionView: PersonMentionView,
        account: Account,
        ctx: Context,
    ) {
        markPersonMentionAsReadRoutine(
            personMentionView = mutableStateOf(personMentionView),
            mentions = mentions,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun markPrivateMessageAsRead(
        privateMessageView: PrivateMessageView,
        account: Account,
        ctx: Context,
    ) {
        markPrivateMessageAsReadRoutine(
            privateMessageView = mutableStateOf(privateMessageView),
            messages = messages,
            account = account,
            ctx = ctx,
            scope = viewModelScope,
        )
    }

    fun createPrivateMessage(
        form: CreatePrivateMessage,
        ctx: Context,
        navController: NavController,
        focusManager: FocusManager,
    ) {
        createPrivateMessageRoutine(
            messages = messages,
            loading = privateMessageReplyLoading,
            form = form,
            ctx = ctx,
            scope = viewModelScope,
            navController = navController,
            focusManager = focusManager,
        )
    }

    fun markAllAsRead(account: Account, ctx: Context) {
        viewModelScope.launch {
            val api = API.getInstance()
            try {
                val form = MarkAllAsRead(
                    auth = account.jwt
                )
                api.markAllAsRead(form)
            } catch (e: Exception) {
                toastException(ctx = ctx, error = e)
            }

            if (unreadOnly.value) {
                replies.clear()
                messages.clear()
                mentions.clear()
            } else {
                for (i in replies.indices) {
                    val commentView = replies[i]
                    val updatedComment = commentView.comment.copy(read = true)
                    val updatedCv = commentView.copy(comment = updatedComment)
                    replies[i] = updatedCv
                }
                for (i in mentions.indices) {
                    val pmv = mentions[i]
                    val updatedComment = pmv.comment.copy(read = true)
                    val updatedPmv = pmv.copy(comment = updatedComment)
                    mentions[i] = updatedPmv
                }
                for (i in messages.indices) {
                    val pmv = messages[i]
                    val updatedPm = pmv.private_message.copy(read = true)
                    val updatedPmv = pmv.copy(private_message = updatedPm)
                    messages[i] = updatedPmv
                }
            }
        }
    }
}
