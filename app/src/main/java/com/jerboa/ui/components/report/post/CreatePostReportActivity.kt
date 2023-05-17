
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
import androidx.navigation.NavController
import com.jerboa.api.ApiState
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.report.CreateReportBody
import com.jerboa.ui.components.report.CreateReportHeader
import com.jerboa.ui.components.report.CreateReportViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostReportActivity(
    accountViewModel: AccountViewModel,
    navController: NavController,
    createReportViewModel: CreateReportViewModel,
) {
    Log.d("jerboa", "got to create post report activity")

    val ctx = LocalContext.current
    val account = getCurrentAccount(accountViewModel = accountViewModel)
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
