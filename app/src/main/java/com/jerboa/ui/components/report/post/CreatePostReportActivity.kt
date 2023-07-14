
package com.jerboa.ui.components.report.post

import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.jerboa.api.ApiState
import com.jerboa.datatypes.types.PostId
import com.jerboa.model.AccountViewModel
import com.jerboa.model.CreateReportViewModel
import com.jerboa.ui.components.common.InitializeRoute
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.report.CreateReportBody
import com.jerboa.ui.components.report.CreateReportHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostReportActivity(
    postId: PostId,
    accountViewModel: AccountViewModel,
    navController: NavController,
) {
    Log.d("jerboa", "got to create post report activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)

    val createReportViewModel: CreateReportViewModel = viewModel()
    InitializeRoute(createReportViewModel) {
        createReportViewModel.setPostId(postId)
    }

    var reason by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }

    val focusManager = LocalFocusManager.current
    val loading = when (createReportViewModel.postReportRes) {
        ApiState.Loading -> true
        else -> false
    }

    Scaffold(
        topBar = {
            CreateReportHeader(
                navController = navController,
                loading = loading,
                onCreateClick = {
                    account?.also { acct ->
                        createReportViewModel.createPostReport(
                            reason = reason.text,
                            ctx = ctx,
                            navController = navController,
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
