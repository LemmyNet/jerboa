package com.jerboa.ui.components.post

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.PersonMentionView
import com.jerboa.datatypes.PrivateMessageView
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.CreateComment
import com.jerboa.db.Account
import com.jerboa.ui.components.comment.createCommentRoutine
import com.jerboa.ui.components.comment.fetchRepliesRoutine
import com.jerboa.ui.components.comment.likeCommentRoutine
import com.jerboa.ui.components.comment.saveCommentRoutine

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
    var unreadOnly = mutableStateOf(false)
        private set
    var loading = mutableStateOf(false)
        private set

    fun fetchReplies(
        account: Account?,
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

    fun likeComment(
        commentView: CommentView,
        voteType: VoteType,
        account: Account?,
        ctx: Context,
    ) {
        likeCommentRoutine(
            commentView = mutableStateOf(commentView),
            voteType = voteType,
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
        account: Account?,
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
}
