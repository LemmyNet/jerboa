package com.jerboa.ui.components.inbox

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.api.API
import com.jerboa.datatypes.CommentReplyView
import com.jerboa.datatypes.CommentSortType
import com.jerboa.datatypes.CommunitySafe
import com.jerboa.datatypes.PersonMentionView
import com.jerboa.datatypes.PersonSafe
import com.jerboa.datatypes.PrivateMessageView
import com.jerboa.datatypes.api.CreatePrivateMessage
import com.jerboa.datatypes.api.MarkAllAsRead
import com.jerboa.db.Account
import com.jerboa.toastException
import com.jerboa.ui.components.comment.createPrivateMessageRoutine
import com.jerboa.ui.components.comment.fetchPersonMentionsRoutine
import com.jerboa.ui.components.comment.fetchRepliesRoutine
import com.jerboa.ui.components.comment.likeCommentReplyRoutine
import com.jerboa.ui.components.comment.markCommentReplyAsReadRoutine
import com.jerboa.ui.components.comment.markPersonMentionAsReadRoutine
import com.jerboa.ui.components.comment.saveCommentReplyRoutine
import com.jerboa.ui.components.community.blockCommunityRoutine
import com.jerboa.ui.components.person.blockPersonRoutine
import com.jerboa.ui.components.person.fetchPrivateMessagesRoutine
import com.jerboa.ui.components.person.markPrivateMessageAsReadRoutine
import kotlinx.coroutines.launch

class InboxViewModel : ViewModel() {

    var replies = mutableStateListOf<CommentReplyView>()
        private set
    var mentions = mutableStateListOf<PersonMentionView>()
        private set
    var messages = mutableStateListOf<PrivateMessageView>()
        private set
    var page = mutableStateOf(1)
        private set
    var sortType = mutableStateOf(CommentSortType.New)
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
        changeSortType: CommentSortType? = null,
        changeUnreadOnly: Boolean? = null,
        ctx: Context
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
            scope = viewModelScope
        )
    }

    fun fetchPersonMentions(
        account: Account,
        nextPage: Boolean = false,
        clear: Boolean = false,
        changeSortType: CommentSortType? = null,
        changeUnreadOnly: Boolean? = null,
        ctx: Context
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
            scope = viewModelScope
        )
    }

    fun fetchPrivateMessages(
        account: Account,
        nextPage: Boolean = false,
        clear: Boolean = false,
        changeUnreadOnly: Boolean? = null,
        ctx: Context
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
            scope = viewModelScope
        )
    }

    fun likeCommentReply(
        commentReplyView: CommentReplyView,
        voteType: VoteType,
        account: Account,
        ctx: Context
    ) {
        likeCommentReplyRoutine(
            commentReplyView = commentReplyView,
            replies = replies,
            voteType = voteType,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }

    fun saveCommentReply(
        commentReplyView: CommentReplyView,
        account: Account,
        ctx: Context
    ) {
        saveCommentReplyRoutine(
            commentReplyView = commentReplyView,
            replies = replies,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }

    fun markReplyAsRead(
        commentReplyView: CommentReplyView,
        account: Account,
        ctx: Context
    ) {
        markCommentReplyAsReadRoutine(
            commentReplyView = commentReplyView,
            replies = replies,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }

    // TODO add this
    fun markPersonMentionAsRead(
        personMentionView: PersonMentionView,
        account: Account,
        ctx: Context
    ) {
        markPersonMentionAsReadRoutine(
            personMentionView = mutableStateOf(personMentionView),
            mentions = mentions,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }

    fun markPrivateMessageAsRead(
        privateMessageView: PrivateMessageView,
        account: Account,
        ctx: Context
    ) {
        markPrivateMessageAsReadRoutine(
            privateMessageView = mutableStateOf(privateMessageView),
            messages = messages,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
    }

    fun createPrivateMessage(
        form: CreatePrivateMessage,
        ctx: Context,
        navController: NavController,
        focusManager: FocusManager
    ) {
        createPrivateMessageRoutine(
            messages = messages,
            loading = privateMessageReplyLoading,
            form = form,
            ctx = ctx,
            scope = viewModelScope,
            navController = navController,
            focusManager = focusManager
        )
    }

    fun blockCommunity(
        community: CommunitySafe,
        account: Account,
        ctx: Context
    ) {
        blockCommunityRoutine(
            community = community,
            block = true,
            account = account,
            ctx = ctx,
            scope = viewModelScope
        )
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
                    val updatedReply = commentView.comment_reply.copy(read = true)
                    val updatedReplyView = commentView.copy(comment_reply = updatedReply)
                    replies[i] = updatedReplyView
                }
                for (i in mentions.indices) {
                    val pmv = mentions[i]
                    val updatedMention = pmv.person_mention.copy(read = true)
                    val updatedMentionView = pmv.copy(person_mention = updatedMention)
                    mentions[i] = updatedMentionView
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
