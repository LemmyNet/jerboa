package com.jerboa.model

import android.content.Context
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
import it.vercruysse.lemmyapi.datatypes.CommentId
import it.vercruysse.lemmyapi.datatypes.CommentReportResponse
import it.vercruysse.lemmyapi.datatypes.CreateCommentReport
import it.vercruysse.lemmyapi.datatypes.CreatePostReport
import it.vercruysse.lemmyapi.datatypes.PostId
import it.vercruysse.lemmyapi.datatypes.PostReportResponse
import kotlinx.coroutines.launch

class CreateReportViewModel : ViewModel() {
    var commentReportRes: ApiState<CommentReportResponse> by mutableStateOf(ApiState.Empty)
        private set
    var postReportRes: ApiState<PostReportResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun createCommentReport(
        commentId: CommentId,
        reason: String,
        ctx: Context,
        focusManager: FocusManager,
        onBack: () -> Unit,
    ) {
        viewModelScope.launch {
            val form =
                CreateCommentReport(
                    comment_id = commentId,
                    reason = reason,
                )

            commentReportRes = ApiState.Loading
            commentReportRes = API.getInstance().createCommentReport(form).toApiState()

            val message =
                when (val res = commentReportRes) {
                    is ApiState.Failure -> ctx.getString(R.string.create_report_view_model_report_fail, res.msg.message)
                    is ApiState.Success -> ctx.getString(R.string.create_report_view_model_report_created)
                    else -> {
                        null
                    }
                }

            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
            focusManager.clearFocus()
            onBack()
        }
    }

    fun createPostReport(
        postId: PostId,
        reason: String,
        ctx: Context,
        focusManager: FocusManager,
        onBack: () -> Unit,
    ) {
        viewModelScope.launch {
            val form =
                CreatePostReport(
                    post_id = postId,
                    reason = reason,
                )

            postReportRes = ApiState.Loading
            postReportRes = API.getInstance().createPostReport(form).toApiState()

            val message =
                when (val res = postReportRes) {
                    is ApiState.Failure -> ctx.getString(R.string.create_report_view_model_report_fail, res.msg.message)
                    is ApiState.Success -> ctx.getString(R.string.create_report_view_model_report_created)
                    else -> {
                        null
                    }
                }

            Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show()
            focusManager.clearFocus()
            onBack()
        }
    }
}
