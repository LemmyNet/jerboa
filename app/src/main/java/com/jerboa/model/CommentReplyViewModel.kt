package com.jerboa.model

import android.content.Context
import android.os.Parcelable
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.CommentReplyView
import com.jerboa.datatypes.types.CommentResponse
import com.jerboa.datatypes.types.CommentView
import com.jerboa.datatypes.types.CreateComment
import com.jerboa.datatypes.types.PersonMentionView
import com.jerboa.datatypes.types.PostView
import com.jerboa.db.entity.Account
import com.jerboa.ui.components.common.apiErrorToast
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

@Parcelize
sealed class ReplyItem : Parcelable {
    class PostItem(val item: PostView) : ReplyItem()

    class CommentItem(val item: CommentView) : ReplyItem()

    class CommentReplyItem(val item: CommentReplyView) : ReplyItem()

    class MentionReplyItem(val item: PersonMentionView) : ReplyItem()
}

class CommentReplyViewModel : ViewModel() {
    var createCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun createComment(
        reply: ReplyItem,
        ctx: Context,
        content: String,
        account: Account,
        focusManager: FocusManager,
        onSuccess: (CommentView) -> Unit,
    ) {
        val (postId, commentParentId) =
            when (reply) {
                is ReplyItem.PostItem -> Pair(reply.item.post.id, null)
                is ReplyItem.CommentItem ->
                    Pair(
                        reply.item.post.id,
                        reply.item.comment.id,
                    )
                is ReplyItem.CommentReplyItem ->
                    Pair(
                        reply.item.post.id,
                        reply.item.comment.id,
                    )
                is ReplyItem.MentionReplyItem ->
                    Pair(
                        reply.item.post.id,
                        reply.item.comment.id,
                    )
            }

        viewModelScope.launch {
            val form =
                CreateComment(
                    content = content,
                    parent_id = commentParentId,
                    post_id = postId,
                    auth = account.jwt,
                )

            createCommentRes = ApiState.Loading
            createCommentRes = apiWrapper(API.getInstance().createComment(form))

            when (val res = createCommentRes) {
                is ApiState.Success -> {
                    val commentView = res.data.comment_view

                    focusManager.clearFocus()
                    onSuccess(commentView)
                }
                is ApiState.Failure -> {
                    Log.d("createComment", "failed", res.msg)
                    apiErrorToast(msg = res.msg, ctx = ctx)
                }
                else -> {}
            }
        }
    }
}
