
package com.jerboa.ui.components.privatemessage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.datatypes.samplePrivateMessageView
import com.jerboa.db.entity.Account
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.MEDIUM_PADDING
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PrivateMessageView

@Composable
fun RepliedPrivateMessage(
    privateMessageView: PrivateMessageView,
    onPersonClick: (personId: PersonId) -> Unit,
    showAvatar: Boolean,
) {
    Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
        PrivateMessageHeader(
            privateMessageView = privateMessageView,
            onPersonClick = onPersonClick,
            myPersonId = privateMessageView.recipient.id,
            showAvatar = showAvatar,
        )
        SelectionContainer {
            Text(text = privateMessageView.private_message.content)
        }
    }
}

@Preview
@Composable
fun RepliedPrivateMessagePreview() {
    RepliedPrivateMessage(
        privateMessageView = samplePrivateMessageView,
        onPersonClick = {},
        showAvatar = true,
    )
}

@Composable
fun PrivateMessageReply(
    privateMessageView: PrivateMessageView,
    reply: TextFieldValue,
    onReplyChange: (TextFieldValue) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    account: Account,
    modifier: Modifier = Modifier,
    showAvatar: Boolean,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
    ) {
        RepliedPrivateMessage(
            privateMessageView = privateMessageView,
            onPersonClick = onPersonClick,
            showAvatar = showAvatar,
        )
        HorizontalDivider(modifier = Modifier.padding(vertical = LARGE_PADDING))
        MarkdownTextField(
            text = reply,
            onTextChange = onReplyChange,
            account = account,
            placeholder = stringResource(R.string.private_message_reply_type_your_message_placeholder),
        )
    }
}
