package com.jerboa.ui.components.report.post

import android.util.Log
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import com.jerboa.db.AccountViewModel
import com.jerboa.ui.components.common.getCurrentAccount
import com.jerboa.ui.components.report.CreateReportBody
import com.jerboa.ui.components.report.CreateReportHeader
import com.jerboa.ui.components.report.CreateReportViewModel

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

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            topBar = {
                CreateReportHeader(
                    navController = navController,
                    loading = createReportViewModel.loading.value,
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
                    }
                )
            },
            content = {
                CreateReportBody(
                    reason = reason,
                    onReasonChange = { reason = it },
                    account = account,
                )
            }
        )
    }
}
