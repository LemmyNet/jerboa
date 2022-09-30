package com.jerboa.ui.components.private_message

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jerboa.datatypes.PrivateMessageView
import com.jerboa.datatypes.samplePrivateMessageView
import com.jerboa.db.Account
import com.jerboa.ui.components.common.MarkdownTextField
import com.jerboa.ui.components.common.simpleVerticalScrollbar
import com.jerboa.ui.theme.APP_BAR_ELEVATION
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.MEDIUM_PADDING

@Composable
fun PrivateMessageReplyHeader(
    navController: NavController = rememberNavController(),
    onSendClick: () -> Unit,
    loading: Boolean
) {
    val backgroundColor = MaterialTheme.colors.primarySurface
    val contentColor = contentColorFor(backgroundColor)

    TopAppBar(
        title = {
            Text(
                text = "Reply"
            )
        },
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        elevation = APP_BAR_ELEVATION,
        actions = {
            IconButton(
                onClick = onSendClick,
                enabled = !loading
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colors.onSurface
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Send,
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
                    Icons.Filled.Close,
                    contentDescription = "Back"
                )
            }
        }
    )
}

@Composable
fun RepliedPrivateMessage(
    privateMessageView: PrivateMessageView,
    onPersonClick: (personId: Int) -> Unit
) {
    Column(modifier = Modifier.padding(MEDIUM_PADDING)) {
        PrivateMessageHeader(
            privateMessageView = privateMessageView,
            onPersonClick = onPersonClick,
            myPersonId = privateMessageView.recipient.id
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
        onPersonClick = {}
    )
}

@Composable
fun PrivateMessageReply(
    privateMessageView: PrivateMessageView,
    reply: TextFieldValue,
    onReplyChange: (TextFieldValue) -> Unit,
    onPersonClick: (personId: Int) -> Unit,
    account: Account?
) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        modifier = Modifier.simpleVerticalScrollbar(listState)
    ) {
        item {
            RepliedPrivateMessage(
                privateMessageView = privateMessageView,
                onPersonClick = onPersonClick
            )
        }
        item {
            Divider(modifier = Modifier.padding(vertical = LARGE_PADDING))
        }
        item {
            MarkdownTextField(
                text = reply,
                onTextChange = onReplyChange,
                account = account,
                modifier = Modifier.fillMaxWidth(),
                placeholder = "Type your message"
            )
        }
    }
}
