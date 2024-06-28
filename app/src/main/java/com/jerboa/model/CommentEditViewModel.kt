package com.jerboa.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.toApiState
import it.vercruysse.lemmyapi.datatypes.CommentResponse
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.EditComment
import kotlinx.coroutines.launch

class CommentEditViewModel : ViewModel() {
    var editCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun editComment(
        commentView: CommentView,
        content: String,
        focusManager: FocusManager,
        onSuccess: (CommentView) -> Unit,
    ) {
        viewModelScope.launch {
            val form =
                EditComment(
                    content = content,
                    comment_id = commentView.comment.id,
                )

            editCommentRes = ApiState.Loading
            editCommentRes = API.getInstance().editComment(form).toApiState()
            focusManager.clearFocus()

            // Update all the views which might have your comment
            when (val res = editCommentRes) {
                is ApiState.Success -> {
                    onSuccess(res.data.comment_view)
                }

                else -> {}
            }
        }
    }
}
