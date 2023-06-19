package com.jerboa.ui.components.comment.edit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.CommentResponse
import com.jerboa.datatypes.types.CommentView
import com.jerboa.datatypes.types.EditComment
import com.jerboa.db.Account
import com.jerboa.ui.components.person.PersonProfileViewModel
import com.jerboa.ui.components.post.PostViewModel
import kotlinx.coroutines.launch

class CommentEditViewModel : ViewModel() {

    var commentView = mutableStateOf<CommentView?>(null)
        private set
    var editCommentRes: ApiState<CommentResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun initialize(
        newCommentView: CommentView,
    ) {
        commentView.value = newCommentView
    }

    fun editComment(
        content: String,
        navController: NavController,
        focusManager: FocusManager,
        account: Account,
        personProfileViewModel: PersonProfileViewModel,
        postViewModel: PostViewModel,
    ) {
        viewModelScope.launch {
            commentView.value?.also { cv ->
                val form = EditComment(
                    content = content,
                    comment_id = cv.comment.id,
                    auth = account.jwt,
                )

                editCommentRes = ApiState.Loading
                editCommentRes =
                    apiWrapper(
                        API.getInstance().editComment(form),
                    )
                focusManager.clearFocus()

                // Update all the views which might have your comment
                when (val res = editCommentRes) {
                    is ApiState.Success -> {
                        personProfileViewModel.updateComment(res.data.comment_view)
                        postViewModel.updateComment(res.data.comment_view)
                    }

                    else -> {}
                }
                navController.navigateUp()
            }
        }
    }
}
