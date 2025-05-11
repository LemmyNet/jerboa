package com.jerboa.model

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.ui.components.common.apiErrorToast
import it.vercruysse.lemmyapi.datatypes.CommentReplyView
import it.vercruysse.lemmyapi.datatypes.CommentResponse
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.CreateComment
import it.vercruysse.lemmyapi.datatypes.PersonMentionView
import it.vercruysse.lemmyapi.datatypes.PostView
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
sealed class ReplyItem {
    @Serializable
    class PostItem(
        val item: PostView,
    ) : ReplyItem()

    @Serializable
    class CommentItem(
        val item: CommentView,
    ) : ReplyItem()

    @Serializable
    class CommentReplyItem(
        val item: CommentReplyView,
    ) : ReplyItem()

    @Serializable
    class MentionReplyItem(
        val item: PersonMentionView,
    ) : ReplyItem()
}

class CommentReplyViewModel : ViewModel() {
    var createCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun createComment(
        reply: ReplyItem,
        ctx: Context,
        content: String,
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
                )

            createCommentRes = ApiState.Loading
            createCommentRes = API.getInstance().createComment(form).toApiState()

            when (val res = createCommentRes) {
                is ApiState.Success -> {
                    val commentView = res.data.comment_view

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
