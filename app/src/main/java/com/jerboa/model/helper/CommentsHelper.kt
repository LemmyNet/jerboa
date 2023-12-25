package com.jerboa.model.helper

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.toApiState
import com.jerboa.showBlockPersonToast
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPerson
import it.vercruysse.lemmyapi.v0x19.datatypes.CommentView
import it.vercruysse.lemmyapi.v0x19.datatypes.CreateCommentLike
import it.vercruysse.lemmyapi.v0x19.datatypes.DeleteComment
import it.vercruysse.lemmyapi.v0x19.datatypes.SaveComment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface CommentsHelper {

    val scope: CoroutineScope
    fun updateComment(commentView: CommentView)
    fun likeComment(form: CreateCommentLike) {
        scope.launch {
            val res = API.getInstance().createCommentLike(form)

            res.onSuccess {
                updateComment(it.comment_view)
            }
        }
    }

    fun deleteComment(form: DeleteComment) {
        scope.launch {
            API.getInstance().deleteComment(form).onSuccess {
                updateComment(it.comment_view)
            }
        }
    }

    fun saveComment(form: SaveComment) {
        scope.launch {
            API.getInstance().saveComment(form).onSuccess {
                updateComment(it.comment_view)
            }
        }
    }

    fun blockPerson(
        form: BlockPerson,
        ctx: Context,
    ) {
        scope.launch {
            val blockPersonRes = API.getInstance().blockPerson(form).toApiState()
            withContext(Dispatchers.Main) {
                showBlockPersonToast(blockPersonRes, ctx)
            }
        }
    }
}