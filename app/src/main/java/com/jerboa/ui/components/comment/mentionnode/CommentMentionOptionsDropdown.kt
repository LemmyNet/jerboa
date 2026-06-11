package com.jerboa.ui.components.comment.mentionnode

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.jerboa.R
import com.jerboa.datatypes.getContent
import com.jerboa.feat.copyTextToClipboard
import com.jerboa.ui.components.common.PopupMenuItem
import com.jerboa.util.cascade.CascadeCenteredDropdownMenu
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PersonMentionView

@Composable
fun CommentMentionsOptionsDropdown(
    personMentionView: PersonMentionView,
    onDismissRequest: () -> Unit,
    onCommentLinkClick: (PersonMentionView) -> Unit,
    onPersonClick: (PersonId) -> Unit,
    onViewSourceClick: () -> Unit,
    onBlockCreatorClick: (Person) -> Unit,
    onReportClick: (PersonMentionView) -> Unit,
    onRemoveClick: (PersonMentionView) -> Unit,
    isCreator: Boolean,
    canMod: Boolean,
    viewSource: Boolean,
) {
    val ctx = LocalContext.current

    CascadeCenteredDropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest,
    ) {
        PopupMenuItem(
            text = stringResource(R.string.comment_node_goto_comment),
            icon = Icons.AutoMirrored.Outlined.Comment,
            onClick = {
                onDismissRequest()
                onCommentLinkClick(personMentionView)
            },
        )

        PopupMenuItem(
            text = stringResource(R.string.comment_node_go_to, personMentionView.creator.name),
            icon = Icons.Outlined.Person,
            onClick = {
                onDismissRequest()
                onPersonClick(personMentionView.creator.id)
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
                    val permalink = personMentionView.comment.ap_id
                    copyTextToClipboard(ctx, permalink, "Permalink", R.string.permalink_copied)
                },
            )
            val content = personMentionView.comment.getContent()
            PopupMenuItem(
                text = stringResource(R.string.comment_node_copy_comment),
                icon = Icons.Outlined.ContentCopy,
                onClick = {
                    onDismissRequest()
                    copyTextToClipboard(ctx, content, "comment", R.string.comment_node_comment_copied)
                },
            )
        }

        PopupMenuItem(
            text =
                if (viewSource) {
                    stringResource(R.string.comment_node_view_original)
                } else {
                    stringResource(R.string.view_source)
                },
            icon = Icons.Outlined.Description,
            onClick = {
                onDismissRequest()
                onViewSourceClick()
            },
        )

        if (!isCreator) {
            HorizontalDivider()
            PopupMenuItem(
                text = stringResource(R.string.block_person, personMentionView.creator.name),
                icon = Icons.Outlined.Block,
                onClick = {
                    onDismissRequest()
                    onBlockCreatorClick(personMentionView.creator)
                },
            )

            PopupMenuItem(
                text = stringResource(R.string.comment_node_report_comment),
                icon = Icons.Outlined.Flag,
                onClick = {
                    onDismissRequest()
                    onReportClick(personMentionView)
                },
            )
        }

        if (canMod) {
            HorizontalDivider()
            val (removeText, removeIcon) =
                if (personMentionView.comment.removed) {
                    Pair(stringResource(R.string.restore_comment), Icons.Outlined.Restore)
                } else {
                    Pair(stringResource(R.string.remove_comment), Icons.Outlined.Gavel)
                }

            PopupMenuItem(
                text = removeText,
                icon = removeIcon,
                onClick = {
                    onDismissRequest()
                    onRemoveClick(personMentionView)
                },
            )
        }
    }
}
