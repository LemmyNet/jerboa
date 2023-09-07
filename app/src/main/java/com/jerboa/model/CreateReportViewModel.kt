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
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.CommentReportResponse
import com.jerboa.datatypes.types.CreateCommentReport
import com.jerboa.datatypes.types.CreatePostReport
import com.jerboa.datatypes.types.PostReportResponse
import com.jerboa.db.entity.Account
import kotlinx.coroutines.launch

class CreateReportViewModel : ViewModel() {

    var commentReportRes: ApiState<CommentReportResponse> by mutableStateOf(ApiState.Empty)
        private set
    var postReportRes: ApiState<PostReportResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun createCommentReport(
        commentId: Int,
        reason: String,
        account: Account,
        ctx: Context,
        focusManager: FocusManager,
        onBack: () -> Unit,
    ) {
        viewModelScope.launch {
            val form = CreateCommentReport(
                comment_id = commentId,
                reason = reason,
                auth = account.jwt,
            )

            commentReportRes = ApiState.Loading
            commentReportRes =
                apiWrapper(
                    API.getInstance().createCommentReport(form),
                )

            val message = when (val res = commentReportRes) {
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
        postId: Int,
        reason: String,
        account: Account,
        ctx: Context,
        focusManager: FocusManager,
        onBack: () -> Unit,
    ) {
        viewModelScope.launch {
            val form = CreatePostReport(
                post_id = postId,
                reason = reason,
                auth = account.jwt,
            )

            postReportRes = ApiState.Loading
            postReportRes =
                apiWrapper(
                    API.getInstance().createPostReport(form),
                )

            val message = when (val res = postReportRes) {
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
