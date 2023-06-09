@file:OptIn(ExperimentalMaterial3Api::class)

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
import com.jerboa.datatypes.PrivateMessageView
import com.jerboa.datatypes.samplePrivateMessageView
import com.jerboa.db.Account
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.MEDIUM_PADDING

@Composable
fun PrivateMessageReplyHeader(
    navController: NavController = rememberNavController(),
    onSendClick: () -> Unit,
    loading: Boolean,
) {
    TopAppBar(
        title = {
            Text(
                text = "Reply",
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
                    contentDescription = "Back",
                )
            }
        },
    )
}

@Composable
fun RepliedPrivateMessage(
    privateMessageView: PrivateMessageView,
    onPersonClick: (personId: Int) -> Unit,
) {
    Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
        PrivateMessageHeader(
            privateMessageView = privateMessageView,
            onPersonClick = onPersonClick,
            myPersonId = privateMessageView.recipient.id,
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
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState),
    ) {
        RepliedPrivateMessage(
            privateMessageView = privateMessageView,
            onPersonClick = onPersonClick,
        )
        Divider(modifier = Modifier.padding(vertical = LARGE_PADDING))
        MarkdownTextField(
            text = reply,
            onTextChange = onReplyChange,
            account = account,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Type your message",
        )
    }
}
