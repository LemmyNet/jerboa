package com.jerboa.ui.components.comment

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.ui.focus.FocusManager
import androidx.navigation.NavController
import com.jerboa.VoteType
import com.jerboa.api.createCommentWrapper
import com.jerboa.api.likeCommentWrapper
import com.jerboa.api.saveCommentWrapper
import com.jerboa.datatypes.CommentView
import com.jerboa.datatypes.api.CreateComment
import com.jerboa.db.Account
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
