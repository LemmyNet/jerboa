package com.jerboa.ui.components.comment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.GppBad
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.jerboa.R
import com.jerboa.amAdmin
import com.jerboa.api.API
import com.jerboa.datatypes.BanData
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.datatypes.getContent
import com.jerboa.feat.canModOrAdmin
import com.jerboa.feat.copyTextToClipboard
import com.jerboa.ui.components.common.BanFromCommunityPopupMenuItem
import com.jerboa.ui.components.common.BanPersonPopupMenuItem
import com.jerboa.ui.components.common.PopupMenuItem
import com.jerboa.util.cascade.CascadeCenteredDropdownMenu
import it.vercruysse.lemmyapi.datatypes.CommentId
import it.vercruysse.lemmyapi.datatypes.CommentView
import it.vercruysse.lemmyapi.datatypes.DeleteComment
import it.vercruysse.lemmyapi.datatypes.DistinguishComment
import it.vercruysse.lemmyapi.datatypes.MyUserInfo
import it.vercruysse.lemmyapi.datatypes.Person
import it.vercruysse.lemmyapi.datatypes.PersonId
import it.vercruysse.lemmyapi.datatypes.PersonView

@Composable
fun CommentOptionsDropdown(
    commentView: CommentView,
    admins: List<PersonView>,
    myUserInfo: MyUserInfo?,
    onDismissRequest: () -> Unit,
    onCommentLinkClick: (CommentView) -> Unit,
    onPersonClick: (PersonId) -> Unit,
    onViewSourceClick: () -> Unit,
    onEditCommentClick: (CommentView) -> Unit,
    onDeleteCommentClick: (DeleteComment) -> Unit,
    onBlockCreatorClick: (Person) -> Unit,
    onReportClick: (CommentId) -> Unit,
    onRemoveClick: (CommentView) -> Unit,
    onDistinguishClick: (DistinguishComment) -> Unit,
    onViewVotesClick: (CommentId) -> Unit,
    onBanPersonClick: (BanData) -> Unit,
    onBanFromCommunityClick: (BanFromCommunityData) -> Unit,
    viewSource: Boolean,
) {
    val ctx = LocalContext.current
    val commentId = commentView.comment.id

    val isCreator = commentView.comment.creator_id == myUserInfo?.local_user_view?.local_user?.person_id
    val canModOrAdmin = canModOrAdmin(
        canMod = commentView.can_mod,
        creatorId = commentView.comment.creator_id,
        myUserInfo = myUserInfo,
        admins = admins,
    )

    CascadeCenteredDropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest,
    ) {
        PopupMenuItem(
            text = stringResource(R.string.comment_node_goto_comment),
            icon = Icons.AutoMirrored.Outlined.Comment,
            onClick = {
                onDismissRequest()
                onCommentLinkClick(commentView)
            },
        )

        PopupMenuItem(
            text = stringResource(R.string.comment_node_go_to, commentView.creator.name),
            icon = Icons.Outlined.Person,
            onClick = {
                onDismissRequest()
                onPersonClick(commentView.creator.id)
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
                    val permalink = commentView.comment.ap_id
                    copyTextToClipboard(ctx, permalink, "Permalink", R.string.permalink_copied)
                },
            )
            val content = commentView.comment.getContent()
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

        HorizontalDivider()

        if (isCreator) {
            PopupMenuItem(
                text = stringResource(R.string.edit),
                icon = Icons.Outlined.Edit,
                onClick = {
                    onDismissRequest()
                    onEditCommentClick(commentView)
                },
            )

            val deleteForm =
                DeleteComment(
                    comment_id = commentId,
                    deleted = !commentView.comment.deleted,
                )
            if (commentView.comment.deleted) {

                PopupMenuItem(
                    text = stringResource(R.string.restore),
                    icon = Icons.Outlined.Restore,
                    onClick = {
                        onDismissRequest()
                        onDeleteCommentClick(deleteForm)
                    },
                )
            } else {
                PopupMenuItem(
                    text = stringResource(R.string.delete),
                    icon = Icons.Outlined.Delete,
                    onClick = {
                        onDismissRequest()
                        onDeleteCommentClick(deleteForm)
                    },
                )
            }
        } else {
            PopupMenuItem(
                text = stringResource(R.string.block_x, commentView.creator.name),
                icon = Icons.Outlined.Block,
                onClick = {
                    onDismissRequest()
                    onBlockCreatorClick(commentView.creator)
                },
            )
            PopupMenuItem(
                text = stringResource(R.string.comment_node_report_comment),
                icon = Icons.Outlined.Flag,
                onClick = {
                    onDismissRequest()
                    onReportClick(commentId)
                },
            )
        }

        // The moderation subfield
        if (canModOrAdmin) {
            PopupMenuItem(
                text = stringResource(R.string.moderation),
                icon = Icons.Outlined.Shield,
            ) {
                    HorizontalDivider()
                    val (removeText, removeIcon) =
                        if (commentView.comment.removed) {
                            Pair(stringResource(R.string.restore_comment), Icons.Outlined.Restore)
                        } else {
                            Pair(stringResource(R.string.remove_comment), Icons.Outlined.GppBad)
                        }

                    PopupMenuItem(
                        text = removeText,
                        icon = removeIcon,
                        onClick = {
                            onDismissRequest()
                            onRemoveClick(commentView)
                        },
                    )
                    if (myUserInfo.amAdmin()) {
                        BanPersonPopupMenuItem(
                            banData = BanData(
                                person = commentView.creator,
                                banned = commentView.creator_banned,
                            ),
                            onDismissRequest = onDismissRequest,
                            onBanPersonClick = onBanPersonClick
                        )
                    }

                    // Only show ban from community button if its a local community
                    if (commentView.community.local) {
                        BanFromCommunityPopupMenuItem(
                            BanFromCommunityData(
                                person = commentView.creator,
                                community = commentView.community,
                                banned = commentView.creator_banned_from_community,
                            ),
                            onDismissRequest,
                            onBanFromCommunityClick,
                        )
                    }

                // Are an admin or mod, and also the comment creator
                if (isCreator) {
                    val (distinguishText, distinguishIcon) =
                        if (commentView.comment.distinguished) {
                            Pair(stringResource(R.string.undistinguish_comment), Icons.Outlined.Shield)
                        } else {
                            Pair(stringResource(R.string.distinguish_comment), Icons.Filled.Shield)
                        }

                    PopupMenuItem(
                        text = distinguishText,
                        icon = distinguishIcon,
                        onClick = {
                            onDismissRequest()
                            onDistinguishClick(commentView)
                        },
                    )
                }

            }
        }
        // You can do these actions on mods above you
        if (myUserInfo.amAdmin() && API.getInstanceOrNull()?.FF?.listAdminVotes() == true) {
            PopupMenuItem(
                text = stringResource(R.string.view_votes),
                icon = ImageVector.vectorResource(R.drawable.up_filled),
                onClick = {
                    onDismissRequest()
                    onViewVotesClick(commentView.comment.id)
                },
            )
        }

    }
}
