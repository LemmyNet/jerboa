package com.jerboa.ui.components.ban

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.jerboa.ui.components.common.CheckboxField
import com.jerboa.ui.components.common.ExpiresField
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.theme.MEDIUM_PADDING

@Composable
fun BanPersonBody(
    isBan: Boolean,
    reason: TextFieldValue,
    onReasonChange: (TextFieldValue) -> Unit,
    expireDays: Long?,
    onExpiresChange: (Long?) -> Unit,
    permaBan: Boolean,
    onPermaBanChange: (Boolean) -> Unit,
    removeData: Boolean,
    onRemoveDataChange: (Boolean) -> Unit,
    isValid: Boolean,
    account: Account,
    padding: PaddingValues,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier =
            Modifier
                .verticalScroll(scrollState)
                .padding(
                    vertical = padding.calculateTopPadding(),
                    horizontal = MEDIUM_PADDING,
                ).imePadding(),
    ) {
        MarkdownTextField(
            text = reason,
            onTextChange = onReasonChange,
            account = account,
            placeholder = stringResource(R.string.type_your_reason),
        )

        // Only show these fields for a ban, not an unban
        if (isBan) {
            if (!permaBan) {
                ExpiresField(
                    value = expireDays,
                    onIntChange = onExpiresChange,
                    isValid = isValid,
                )
            }

            CheckboxField(
                label = stringResource(R.string.remove_content),
                checked = removeData,
                onCheckedChange = onRemoveDataChange,
            )

            CheckboxField(
                label = stringResource(R.string.permaban),
                checked = permaBan,
                onCheckedChange = onPermaBanChange,
            )
        }
    }
}
