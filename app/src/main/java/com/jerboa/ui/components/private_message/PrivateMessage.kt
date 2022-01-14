package com.jerboa.ui.components.private_message

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reply
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.jerboa.ActionBarButton
import com.jerboa.MyMarkdownText
import com.jerboa.datatypes.PrivateMessageView
import com.jerboa.datatypes.samplePrivateMessageView
import com.jerboa.ui.components.common.TimeAgo
import com.jerboa.ui.components.person.PersonProfileLink
import com.jerboa.ui.theme.LARGE_PADDING
import com.jerboa.ui.theme.Muted
import com.jerboa.ui.theme.SMALL_PADDING
import com.jerboa.ui.theme.XXL_PADDING

@Composable
fun PrivateMessageHeader(
    privateMessageView: PrivateMessageView,
    onPersonClick: (personId: Int) -> Unit = {},
    myPersonId: Int,
) {

    val fromOrTo = if (isCreator(myPersonId, privateMessageView)) {
        "to "
    } else {
        "from "
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SMALL_PADDING)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = fromOrTo, color = Muted)
            PersonProfileLink(
                person = privateMessageView.creator,
                onClick = { onPersonClick(privateMessageView.creator.id) },
            )
        }

        TimeAgo(dateStr = privateMessageView.private_message.published)
    }
}

fun isCreator(myPersonId: Int, privateMessageView: PrivateMessageView): Boolean {
    return myPersonId == privateMessageView.creator.id
}

@Preview
@Composable
fun PrivateMessageViewPreview() {
    PrivateMessageHeader(privateMessageView = samplePrivateMessageView, myPersonId = 23)
}

@Composable
fun PrivateMessageBody(privateMessageView: PrivateMessageView) {
    MyMarkdownText(markdown = privateMessageView.private_message.content)
}

@Composable
fun PrivateMessage(
    privateMessageView: PrivateMessageView,
    onReplyClick: (privateMessageView: PrivateMessageView) -> Unit = {},
    onMarkAsReadClick: (privateMessageView: PrivateMessageView) -> Unit = {},
    onPersonClick: (personId: Int) -> Unit = {},
    myPersonId: Int, // Required so we know the from / to
) {
    Column(
        modifier = Modifier
            .padding(
                horizontal = LARGE_PADDING,
                vertical = SMALL_PADDING,
            )
    ) {

        PrivateMessageHeader(
            privateMessageView = privateMessageView,
            onPersonClick = onPersonClick,
            myPersonId = myPersonId,
        )
        PrivateMessageBody(privateMessageView = privateMessageView)
        PrivateMessageFooterLine(
            privateMessageView = privateMessageView,
            onReplyClick = onReplyClick,
            onMarkAsReadClick = onMarkAsReadClick,
            myPersonId = myPersonId,
        )
    }
}

@Composable
fun PrivateMessageFooterLine(
    privateMessageView: PrivateMessageView,
    onReplyClick: (privateMessageView: PrivateMessageView) -> Unit = {},
    onMarkAsReadClick: (privateMessageView: PrivateMessageView) -> Unit = {},
    myPersonId: Int,
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = LARGE_PADDING, bottom = SMALL_PADDING)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(XXL_PADDING),
        ) {
            if (!isCreator(myPersonId, privateMessageView)) {
                ActionBarButton(
                    icon = Icons.Filled.Check,
                    onClick = { onMarkAsReadClick(privateMessageView) },
                    contentColor = if (privateMessageView.private_message.read) {
                        Color.Green
                    } else {
                        Muted
                    },
                )
                ActionBarButton(
                    icon = Icons.Filled.Reply,
                    onClick = { onReplyClick(privateMessageView) },
                )
            }
            ActionBarButton(icon = Icons.Filled.MoreVert)
        }
    }
}

@Preview
@Composable
fun PrivateMessagePreview() {
    PrivateMessage(privateMessageView = samplePrivateMessageView, myPersonId = 23)
}
