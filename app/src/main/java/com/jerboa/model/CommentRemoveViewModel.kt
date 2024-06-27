package com.jerboa.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import com.jerboa.ui.components.common.apiErrorToast
import it.vercruysse.lemmyapi.datatypes.CommentId
import it.vercruysse.lemmyapi.datatypes.CommentResponse
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.RemoveComment
import kotlinx.coroutines.launch

class CommentRemoveViewModel : ViewModel() {
    var commentRemoveRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun removeOrRestoreComment(
        commentId: CommentId,
        removed: Boolean,
        reason: String,
        ctx: Context,
        focusManager: FocusManager,
        onSuccess: (CommentView) -> Unit,
    ) {
        viewModelScope.launch {
            val form =
                RemoveComment(
                    comment_id = commentId,
                    removed = removed,
                    reason = reason,
                )

            commentRemoveRes = ApiState.Loading
            commentRemoveRes = API.getInstance().removeComment(form).toApiState()

            when (val res = commentRemoveRes) {
                is ApiState.Failure -> {
                    Log.d("removeComment", "failed", res.msg)
                    apiErrorToast(msg = res.msg, ctx = ctx)
                }

                is ApiState.Success -> {
                    val message =
                        if (removed) {
                            ctx.getString(R.string.comment_removed)
                        } else {
                            ctx.getString(R.string.comment_restored)
                        }
                    val commentView = res.data.comment_view
                    Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()

                    focusManager.clearFocus()
                    onSuccess(commentView)
                }
                else -> {}
            }
        }
    }
}
