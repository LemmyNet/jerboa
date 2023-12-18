package com.jerboa.ui.components.comment.replynode

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.jerboa.R
import com.jerboa.copyToClipboard
import com.jerboa.ui.components.common.PopupMenuItem
import com.jerboa.util.cascade.CascadeCenteredDropdownMenu
import it.vercruysse.lemmyapi.v0x19.datatypes.CommentReplyView
import it.vercruysse.lemmyapi.v0x19.datatypes.Person
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonId

@Composable
fun CommentReplyOptionsDropdown(
    commentReplyView: CommentReplyView,
    onDismissRequest: () -> Unit,
    onCommentLinkClick: (CommentReplyView) -> Unit,
    onPersonClick: (PersonId) -> Unit,
    onViewSourceClick: () -> Unit,
    onBlockCreatorClick: (Person) -> Unit,
    onReportClick: (CommentReplyView) -> Unit,
    isCreator: Boolean,
    viewSource: Boolean,
) {
    val localClipboardManager = LocalClipboardManager.current
    val ctx = LocalContext.current

    CascadeCenteredDropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest,
    ) {
        PopupMenuItem(
            text = stringResource(R.string.comment_node_goto_comment),
            icon = Icons.Outlined.Comment,
            onClick = {
                onDismissRequest()
                onCommentLinkClick(commentReplyView)
            },
        )

        PopupMenuItem(
            text = stringResource(R.string.comment_node_go_to, commentReplyView.creator.name),
            icon = Icons.Outlined.Person,
            onClick = {
                onDismissRequest()
                onPersonClick(commentReplyView.creator.id)
            },
        )

        PopupMenuItem(
            text = stringResource(R.string.copy),
            icon = Icons.Outlined.CopyAll,
        ) {
            PopupMenuItem(
                text = stringResource(R.string.comment_node_copy_permalink),
                icon = Icons.Outlined.Link,
                onClick = {
                    onDismissRequest()
                    val permalink = commentReplyView.comment.ap_id
                    localClipboardManager.setText(AnnotatedString(permalink))
                    Toast.makeText(
                        ctx,
                        ctx.getString(R.string.comment_node_permalink_copied),
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
            PopupMenuItem(
                text = stringResource(R.string.comment_node_copy_comment),
                icon = Icons.Outlined.ContentCopy,
                onClick = {
                    onDismissRequest()
                    if (copyToClipboard(ctx, commentReplyView.comment.content, "comment")) {
                        Toast.makeText(ctx, ctx.getString(R.string.comment_node_comment_copied), Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(ctx, ctx.getString(R.string.generic_error), Toast.LENGTH_SHORT).show()
                    }
                },
            )
        }

        PopupMenuItem(
            text =
                if (viewSource) {
                    stringResource(R.string.comment_node_view_original)
                } else {
                    stringResource(R.string.comment_node_view_source)
                },
            icon = Icons.Outlined.Description,
            onClick = {
                onDismissRequest()
                onViewSourceClick()
            },
        )

        if (!isCreator) {
            Divider()
            PopupMenuItem(
                text = stringResource(R.string.comment_node_block, commentReplyView.creator.name),
                icon = Icons.Outlined.Block,
                onClick = {
                    onDismissRequest()
                    onBlockCreatorClick(commentReplyView.creator)
                },
            )
            PopupMenuItem(
                text = stringResource(R.string.comment_node_report_comment),
                icon = Icons.Outlined.Flag,
                onClick = {
                    onDismissRequest()
                    onReportClick(commentReplyView)
                },
            )
        }
    }
}
