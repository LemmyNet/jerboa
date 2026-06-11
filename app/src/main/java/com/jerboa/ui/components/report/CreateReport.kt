package com.jerboa.ui.components.report

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import com.jerboa.R
import com.jerboa.db.entity.Account
import com.jerboa.ui.components.common.MarkdownTextField

@Composable
fun CreateReportBody(
    reason: TextFieldValue,
    onReasonChange: (TextFieldValue) -> Unit,
    account: Account,
    padding: PaddingValues,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier =
            Modifier
                .verticalScroll(scrollState)
                .padding(padding)
                .consumeWindowInsets(padding)
                .imePadding(),
    ) {
        MarkdownTextField(
            text = reason,
            onTextChange = onReasonChange,
            account = account,
            placeholder = stringResource(R.string.create_report_type_your_reason),
        )
    }
}
