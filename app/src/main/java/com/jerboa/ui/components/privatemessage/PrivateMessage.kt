package com.jerboa.ui.components.privatemessage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.outlined.MarkChatRead
import androidx.compose.material.icons.outlined.MarkChatUnread
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.R
import com.jerboa.datatypes.samplePrivateMessageView
import com.jerboa.db.entity.Account
import com.jerboa.db.entity.AnonAccount
import com.jerboa.ui.components.common.ActionBarButton
import com.jerboa.ui.components.common.MyMarkdownText
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XXL_PADDING
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PrivateMessageView

@Composable
fun PrivateMessageHeader(
    privateMessageView: PrivateMessageView,
    onPersonClick: (personId: PersonId) -> Unit,
    myPersonId: PersonId,
    showAvatar: Boolean,
) {
    val otherPerson: Person
    val fromOrTo: String

    if (isCreator(myPersonId, privateMessageView)) {
        otherPerson = privateMessageView.recipient
        fromOrTo = stringResource(R.string.private_message_to)
    } else {
        otherPerson = privateMessageView.creator
        fromOrTo = stringResource(R.string.private_message_from)
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = SMALL_PADDING),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = fromOrTo,
                color = MaterialTheme.colorScheme.outline,
            )
            PersonProfileLink(
                person = otherPerson,
                onClick = { onPersonClick(otherPerson.id) },
                showAvatar = showAvatar,
            )
        }

        TimeAgo(
            published = privateMessageView.private_message.published,
            updated = privateMessageView.private_message.updated,
        )
    }
}

fun isCreator(
    myPersonId: PersonId,
    privateMessageView: PrivateMessageView,
): Boolean = myPersonId == privateMessageView.creator.id

@Preview
@Composable
fun PrivateMessageViewPreview() {
    PrivateMessageHeader(
        privateMessageView = samplePrivateMessageView,
        myPersonId = 23,
        onPersonClick = {},
        showAvatar = true,
    )
}

@Composable
fun PrivateMessageBody(privateMessageView: PrivateMessageView) {
    MyMarkdownText(
        markdown = privateMessageView.private_message.content,
        onClick = {},
    )
}

@Composable
fun PrivateMessage(
    privateMessageView: PrivateMessageView,
    onReplyClick: (privateMessageView: PrivateMessageView) -> Unit,
    onMarkAsReadClick: (privateMessageView: PrivateMessageView) -> Unit,
    onPersonClick: (personId: PersonId) -> Unit,
    // Required so we know the from / to
    myPersonId: PersonId,
    account: Account,
    showAvatar: Boolean,
) {
    Column(
        modifier =
            Modifier
                .padding(
                    horizontal = LARGE_PADDING,
                    vertical = SMALL_PADDING,
                ),
    ) {
        PrivateMessageHeader(
            privateMessageView = privateMessageView,
            onPersonClick = onPersonClick,
            myPersonId = myPersonId,
            showAvatar = showAvatar,
        )
        PrivateMessageBody(privateMessageView = privateMessageView)
        PrivateMessageFooterLine(
            privateMessageView = privateMessageView,
            onReplyClick = onReplyClick,
            onMarkAsReadClick = onMarkAsReadClick,
            myPersonId = myPersonId,
            account = account,
        )
    }
}

@Composable
fun PrivateMessageFooterLine(
    privateMessageView: PrivateMessageView,
    onReplyClick: (privateMessageView: PrivateMessageView) -> Unit,
    onMarkAsReadClick: (privateMessageView: PrivateMessageView) -> Unit,
    myPersonId: PersonId,
    account: Account,
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(top = LARGE_PADDING, bottom = SMALL_PADDING),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(XXL_PADDING),
        ) {
            if (!isCreator(myPersonId, privateMessageView)) {
                ActionBarButton(
                    icon =
                        if (privateMessageView.private_message.read) {
                            Icons.Outlined.MarkChatRead
                        } else {
                            Icons.Outlined.MarkChatUnread
                        },
                    contentDescription =
                        if (privateMessageView.private_message.read) {
                            stringResource(R.string.markUnread)
                        } else {
                            stringResource(R.string.markRead)
                        },
                    onClick = { onMarkAsReadClick(privateMessageView) },
                    contentColor =
                        if (privateMessageView.private_message.read) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.outline
                        },
                    account = account,
                )
                ActionBarButton(
                    icon = Icons.AutoMirrored.Outlined.Comment,
                    contentDescription = stringResource(R.string.privateMessage_reply),
                    onClick = { onReplyClick(privateMessageView) },
                    account = account,
                )
            }
        }
    }
}

@Preview
@Composable
fun PrivateMessagePreview() {
    PrivateMessage(
        privateMessageView = samplePrivateMessageView,
        myPersonId = 23,
        account = AnonAccount,
        onPersonClick = {},
        onReplyClick = {},
        onMarkAsReadClick = {},
        showAvatar = true,
    )
}
