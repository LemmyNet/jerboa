@file:OptIn(ExperimentalMaterial3Api::class)

package com.jerboa.ui.components.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.db.Account
import com.jerboa.ui.components.common.MarkdownTextField

@Composable
fun CreateReportHeader(
    navController: NavController = rememberNavController(),
    onCreateClick: () -> Unit,
    loading: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = "Report"
            )
        },
        actions = {
            IconButton(
                onClick = onCreateClick,
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = "TODO"
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Back"
                )
            }
        }
    )
}

@Composable
fun CreateReportBody(
    reason: TextFieldValue,
    onReasonChange: (TextFieldValue) -> Unit,
    account: Account?
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier.verticalScroll(scrollState)
    ) {
        MarkdownTextField(
            text = reason,
            onTextChange = onReasonChange,
            account = account,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Type your reason"
        )
    }
}
