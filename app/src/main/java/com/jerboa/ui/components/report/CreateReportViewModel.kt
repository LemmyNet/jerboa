package com.jerboa.ui.components.report

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
import com.jerboa.api.createCommentReportWrapper
import com.jerboa.api.createPostReportWrapper
import com.jerboa.datatypes.api.CreateCommentReport
import com.jerboa.datatypes.api.CreatePostReport
import com.jerboa.db.Account
import kotlinx.coroutines.launch

class CreateReportViewModel : ViewModel() {

    private var commentId by mutableStateOf<Int?>(null)
    private var postId by mutableStateOf<Int?>(null)

    var loading = mutableStateOf(false)
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
                loading.value = true
                val form = CreateCommentReport(
                    comment_id = cId,
                    reason = reason,
                    auth = account.jwt,
                )
                val report = createCommentReportWrapper(form, ctx)?.comment_report_view
                loading.value = false

                if (report !== null) {
                    Toast.makeText(
                        ctx,
                        ctx.getString(R.string.create_report_view_model_report_created),
                        Toast.LENGTH_SHORT,
                    ).show()
                    focusManager.clearFocus()
                    navController.navigateUp()
                }
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
                loading.value = true
                val form = CreatePostReport(
                    post_id = pId,
                    reason = reason,
                    auth = account.jwt,
                )
                val report = createPostReportWrapper(form, ctx)?.post_report_view
                loading.value = false

                if (report !== null) {
                    Toast.makeText(ctx, ctx.getString(R.string.create_report_view_model_report_created), Toast.LENGTH_SHORT).show()

                    focusManager.clearFocus()
                    navController.navigateUp()
                }
            }
        }
    }
}
