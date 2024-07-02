package com.jerboa.ui.components.report.comment

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.jerboa.R
import com.jerboa.api.ApiState
import com.jerboa.db.entity.isAnon
import com.jerboa.model.AccountViewModel
import com.jerboa.model.CreateReportViewModel
import com.jerboa.ui.components.common.ActionTopBar
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.report.CreateReportBody
import it.vercruysse.lemmyapi.datatypes.CommentId

@Composable
fun CreateCommentReportScreen(
    commentId: CommentId,
    accountViewModel: AccountViewModel,
    onBack: () -> Unit,
) {
    Log.d("jerboa", "got to create comment report screen")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val createReportViewModel: CreateReportViewModel = viewModel()

    var reason by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    val loading =
        when (createReportViewModel.commentReportRes) {
            ApiState.Loading -> true
            else -> false
        }

    val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            ActionTopBar(
                title = stringResource(R.string.create_report_report),
                loading = loading,
                onBackClick = onBack,
                onActionClick = {
                    if (!account.isAnon()) {
                        createReportViewModel.createCommentReport(
                            commentId = commentId,
                            reason = reason.text,
                            ctx = ctx,
                            onBack = onBack,
                            focusManager = focusManager,
                        )
                    }
                },
                actionText = R.string.form_submit,
                actionIcon = Icons.AutoMirrored.Outlined.Send,
            )
        },
        content = { padding ->
            CreateReportBody(
                reason = reason,
                onReasonChange = { reason = it },
                account = account,
                padding = padding,
            )
        },
    )
}
