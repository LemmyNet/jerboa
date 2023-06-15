
package com.jerboa.ui.components.privatemessage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.R
import com.jerboa.datatypes.samplePrivateMessageView
import com.jerboa.datatypes.types.PrivateMessageView
import com.jerboa.db.Account
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.MEDIUM_PADDING

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivateMessageReplyHeader(
    navController: NavController = rememberNavController(),
    onSendClick: () -> Unit,
    loading: Boolean,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.private_message_reply_reply),
            )
        },
        actions = {
            IconButton(
                onClick = onSendClick,
                enabled = !loading,
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Send,
                        contentDescription = stringResource(R.string.form_submit),
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.private_message_reply_back),
                )
            }
        },
    )
}

@Composable
fun RepliedPrivateMessage(
    privateMessageView: PrivateMessageView,
    onPersonClick: (personId: Int) -> Unit,
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
    onPersonClick: (personId: Int) -> Unit,
    account: Account?,
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
        Divider(modifier = Modifier.padding(vertical = LARGE_PADDING))
        MarkdownTextField(
            text = reply,
            onTextChange = onReplyChange,
            account = account,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(R.string.private_message_reply_type_your_message_placeholder),
        )
    }
}
