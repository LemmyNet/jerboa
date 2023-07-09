package com.jerboa.model

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.api.ApiState
import com.jerboa.api.apiWrapper
import com.jerboa.datatypes.types.CommentReportResponse
import com.jerboa.datatypes.types.CreateCommentReport
import com.jerboa.datatypes.types.CreatePostReport
import com.jerboa.datatypes.types.PostReportResponse
import com.jerboa.db.entity.Account
import com.jerboa.ui.components.common.Initializable
import kotlinx.coroutines.launch

class CreateReportViewModel : ViewModel(), Initializable {
    override var initialized by mutableStateOf(false)

    private var commentId by mutableStateOf<Int?>(null)
    private var postId by mutableStateOf<Int?>(null)

    var commentReportRes: ApiState<CommentReportResponse> by mutableStateOf(ApiState.Empty)
        private set
    var postReportRes: ApiState<PostReportResponse> by mutableStateOf(ApiState.Empty)
        private set

    fun setCommentId(
        newCommentId: Int,
    ) {
        commentId = newCommentId
        postId = null
    }

    fun setPostId(
        newPostId: Int,
    ) {
        postId = newPostId
        commentId = null
    }

    fun createCommentReport(
        reason: String,
        account: Account,
        ctx: Context,
        navController: NavController,
        focusManager: FocusManager,
    ) {
        commentId?.also { cId ->
            viewModelScope.launch {
                val form = CreateCommentReport(
                    comment_id = cId,
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
                navController.navigateUp()
            }
        }
    }

    fun createPostReport(
        reason: String,
        account: Account,
        ctx: Context,
        navController: NavController,
        focusManager: FocusManager,
    ) {
        postId?.also { pId ->
            viewModelScope.launch {
                val form = CreatePostReport(
                    post_id = pId,
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
                navController.navigateUp()
            }
        }
    }
}
