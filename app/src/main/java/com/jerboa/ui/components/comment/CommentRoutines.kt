package com.jerboa.ui.components.comment

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.ui.focus.FocusManager
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.api.API
import com.jerboa.api.createCommentWrapper
import com.jerboa.api.likeCommentWrapper
import com.jerboa.api.saveCommentWrapper
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.SortType
import com.jerboa.datatypes.api.CreateComment
import com.jerboa.datatypes.api.GetReplies
import com.jerboa.db.Account
import com.jerboa.serializeToMap
import com.jerboa.toastException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun likeCommentRoutine(
    commentView: MutableState<CommentView?>,
    comments: MutableList<CommentView>? = null,
    voteType: VoteType,
    account: Account?,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        account?.also { account ->
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
}

fun saveCommentRoutine(
    commentView: MutableState<CommentView?>,
    comments: MutableList<CommentView>? = null,
    account: Account?,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        account?.also { account ->
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

fun findAndUpdateComment(comments: MutableList<CommentView>, updatedCommentView: CommentView?) {
    updatedCommentView?.also { ucv ->
        val foundIndex = comments.indexOfFirst {
            it.comment.id == ucv.comment.id
        }
        if (foundIndex != -1) {
            comments[foundIndex] = ucv
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
    account: Account?,
    ctx: Context,
    scope: CoroutineScope,
) {
    scope.launch {
        account?.also { account ->
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
}
