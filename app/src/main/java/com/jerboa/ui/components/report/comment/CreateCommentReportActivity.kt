package com.jerboa.ui.components.report.comment

import android.util.Log
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
import com.jerboa.datatypes.types.CommentId
import com.jerboa.model.AccountViewModel
import com.jerboa.model.CreateReportViewModel
import com.jerboa.ui.components.common.CreateSubmitHeader
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.report.CreateReportBody
import com.jerboa.util.InitializeRoute

@Composable
fun CreateCommentReportActivity(
    commentId: CommentId,
    accountViewModel: AccountViewModel,
    onBack: () -> Unit,
) {
    Log.d("jerboa", "got to create comment report activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val createReportViewModel: CreateReportViewModel = viewModel()
    InitializeRoute(createReportViewModel) {
        createReportViewModel.setCommentId(commentId)
    }

    var reason by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    val loading = when (createReportViewModel.commentReportRes) {
        ApiState.Loading -> true
        else -> false
    }

    val focusManager = LocalFocusManager.current
    Scaffold(
        topBar = {
            CreateSubmitHeader(
                title = stringResource(R.string.create_report_report),
                loading = loading,
                onClickBack = onBack,
                onSubmitClick = {
                    account?.also { acct ->
                        createReportViewModel.createCommentReport(
                            reason = reason.text,
                            ctx = ctx,
                            onBack = onBack,
                            focusManager = focusManager,
                            account = acct,
                        )
                    }
                },
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
