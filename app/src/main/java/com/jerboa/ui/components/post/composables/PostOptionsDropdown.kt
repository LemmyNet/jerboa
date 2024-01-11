package com.jerboa.ui.components.post.composables

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.jerboa.PostType
import com.jerboa.R
import com.jerboa.api.API
import com.jerboa.communityNameShown
import com.jerboa.copyToClipboard
import com.jerboa.feat.shareLink
import com.jerboa.feat.shareMedia
import com.jerboa.isMedia
import com.jerboa.ui.components.common.PopupMenuItem
import com.jerboa.util.blockCommunity
import com.jerboa.util.blockPerson
import com.jerboa.util.cascade.CascadeCenteredDropdownMenu
import com.jerboa.util.getInstanceFromCommunityUrl
import com.jerboa.util.showBlockCommunityToast
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockCommunity
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockInstance
import it.vercruysse.lemmyapi.v0x19.datatypes.BlockPerson
import it.vercruysse.lemmyapi.v0x19.datatypes.Community
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonId
import it.vercruysse.lemmyapi.v0x19.datatypes.PostView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PostOptionsDropdown(
    postView: PostView,
    onDismissRequest: () -> Unit,
    onCommunityClick: (Community) -> Unit,
    onPersonClick: (PersonId) -> Unit,
    onEditPostClick: (PostView) -> Unit,
    onDeletePostClick: (PostView) -> Unit,
    onReportClick: (PostView) -> Unit,
    onRemoveClick: (PostView) -> Unit,
    onViewSourceClick: () -> Unit,
    isCreator: Boolean,
    canMod: Boolean,
    viewSource: Boolean,
    showViewSource: Boolean,
    scope: CoroutineScope,
) {
    val ctx = LocalContext.current
    val localClipboardManager = LocalClipboardManager.current

    CascadeCenteredDropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest,
    ) {
        PopupMenuItem(
            text = stringResource(R.string.post_listing_go_to, communityNameShown(postView.community)),
            icon = Icons.Outlined.Forum,
            onClick = {
                onDismissRequest()
                onCommunityClick(postView.community)
            },
        )

        PopupMenuItem(
            text = stringResource(R.string.post_listing_go_to, postView.creator.name),
            icon = Icons.Outlined.Person,
            onClick = {
                onDismissRequest()
                onPersonClick(postView.creator.id)
            },
        )

        PopupMenuItem(
            text = stringResource(R.string.copy),
            icon = Icons.Outlined.CopyAll,
        ) {
            postView.post.url?.also {
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_copy_link),
                    icon = Icons.Outlined.Link,
                    onClick = {
                        onDismissRequest()
                        localClipboardManager.setText(AnnotatedString(it))
                        Toast.makeText(
                            ctx,
                            ctx.getString(R.string.post_listing_link_copied),
                            Toast.LENGTH_SHORT,
                        ).show()
                    },
                )
            }

            PopupMenuItem(
                text = stringResource(R.string.post_listing_copy_permalink),
                icon = Icons.Outlined.Link,
                onClick = {
                    onDismissRequest()
                    val permalink = postView.post.ap_id
                    localClipboardManager.setText(AnnotatedString(permalink))
                    Toast.makeText(
                        ctx,
                        ctx.getString(R.string.post_listing_permalink_copied),
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )

            postView.post.thumbnail_url?.also {
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_copy_thumbnail_link),
                    icon = Icons.Outlined.Link,
                    onClick = {
                        onDismissRequest()
                        if (copyToClipboard(
                                ctx,
                                postView.post.thumbnail_url ?: "",
                                "thumbnail link",
                            )
                        ) {
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.post_listing_thumbnail_link_copied),
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.generic_error),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    },
                )
            }

            postView.post.embed_description?.also {
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_copy_title),
                    icon = Icons.Outlined.ContentCopy,
                    onClick = {
                        onDismissRequest()
                        if (copyToClipboard(
                                ctx,
                                it,
                                "post title",
                            )
                        ) {
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.post_listing_title_copied),
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.generic_error),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    },
                )
            }

            postView.post.name.also {
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_copy_name),
                    icon = Icons.Outlined.ContentCopy,
                    onClick = {
                        onDismissRequest()
                        if (copyToClipboard(ctx, postView.post.name, "post name")) {
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.post_listing_name_copied),
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.generic_error),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    },
                )
            }

            postView.post.body?.also {
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_copy_text),
                    icon = Icons.Outlined.ContentCopy,
                    onClick = {
                        onDismissRequest()
                        if (copyToClipboard(ctx, postView.post.body ?: "", "post text")) {
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.post_listing_text_copied),
                                Toast.LENGTH_SHORT,
                            ).show()
                        } else {
                            Toast.makeText(
                                ctx,
                                ctx.getString(R.string.generic_error),
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    },
                )
            }
        }

        PopupMenuItem(
            text = stringResource(R.string.share),
            icon = Icons.Outlined.Share,
        ) {
            postView.post.url?.also { url ->
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_share_link),
                    icon = Icons.Outlined.Share,
                    onClick = {
                        onDismissRequest()
                        shareLink(url, ctx)
                    },
                )

                when (val mediaType = PostType.fromURL(url)) {
                    PostType.Image ->
                        PopupMenuItem(
                            text = stringResource(R.string.share_image),
                            icon = Icons.Outlined.Share,
                            onClick = {
                                onDismissRequest()
                                shareMedia(scope, ctx, url, mediaType)
                            },
                        )

                    PostType.Video ->
                        PopupMenuItem(
                            text = stringResource(R.string.share_video),
                            icon = Icons.Outlined.Share,
                            onClick = {
                                onDismissRequest()
                                shareMedia(scope, ctx, url, mediaType)
                            },
                        )

                    PostType.Link ->
                        if (isMedia(url)) {
                            PopupMenuItem(
                                text = stringResource(R.string.share_media),
                                icon = Icons.Outlined.Share,
                                onClick = {
                                    onDismissRequest()
                                    shareMedia(scope, ctx, url, PostType.Link)
                                },
                            )
                        }
                }
            }

            PopupMenuItem(
                text = stringResource(R.string.post_listing_share_post),
                icon = Icons.Outlined.Share,
                onClick = {
                    onDismissRequest()
                    shareLink(postView.post.ap_id, ctx)
                },
            )
        }

        // Only visible from PostActivity
        if (showViewSource) {
            postView.post.body?.also {
                PopupMenuItem(
                    text =
                        if (viewSource) {
                            stringResource(R.string.post_listing_view_original)
                        } else {
                            stringResource(R.string.post_listing_view_source)
                        },
                    icon = Icons.Outlined.Description,
                    onClick = {
                        onDismissRequest()
                        onViewSourceClick()
                    },
                )
            }
        }

        Divider()

        if (isCreator) {
            PopupMenuItem(
                text = stringResource(R.string.post_listing_edit),
                icon = Icons.Outlined.Edit,
                onClick = {
                    onDismissRequest()
                    onEditPostClick(postView)
                },
            )

            if (postView.post.deleted) {
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_restore),
                    icon = Icons.Outlined.Restore,
                    onClick = {
                        onDismissRequest()
                        onDeletePostClick(postView)
                    },
                )
            } else {
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_delete),
                    icon = Icons.Outlined.Delete,
                    onClick = {
                        onDismissRequest()
                        onDeletePostClick(postView)
                    },
                )
            }
        } else {
            PopupMenuItem(
                // Reuse existing translations
                text = stringResource(R.string.post_listing_block, ""),
                icon = Icons.Outlined.Block,
            ) {
                PopupMenuItem(
                    text = stringResource(R.string.post_listing_block, postView.creator.name),
                    icon = Icons.Outlined.Block,
                    onClick = {
                        onDismissRequest()
                        blockPerson(
                            scope,
                            BlockPerson(
                                person_id = postView.creator.id,
                                block = true,
                            ),
                            ctx,
                        )
                    },
                )

                PopupMenuItem(
                    text = stringResource(R.string.post_listing_block, postView.community.name),
                    icon = Icons.Outlined.Block,
                    onClick = {
                        onDismissRequest()
                        blockCommunity(
                            scope,
                            BlockCommunity(
                                community_id = postView.community.id,
                                block = true,
                            ),
                            ctx,
                        )
                    },
                )

                val api = API.getInstanceOrNull()

                if (api != null && api.FF.instanceBlock()) {
                    val instance = getInstanceFromCommunityUrl(postView.community.actor_id)
                    PopupMenuItem(
                        text = stringResource(R.string.post_listing_block, instance),
                        icon = Icons.Outlined.Block,
                        onClick = {
                            onDismissRequest()
                            scope.launch(Dispatchers.IO) {
                                val resp = api.blockInstance(BlockInstance(postView.community.instance_id, true))
                                withContext(Dispatchers.Main) {
                                    showBlockCommunityToast(resp, instance, ctx)
                                }
                            }
                        },
                    )
                }
            }

            PopupMenuItem(
                text = stringResource(R.string.post_listing_report_post),
                icon = Icons.Outlined.Flag,
                onClick = {
                    onDismissRequest()
                    onReportClick(postView)
                },
            )

            if (canMod) {
                Divider()
                val (removeText, removeIcon) =
                    if (postView.post.removed) {
                        Pair(stringResource(R.string.restore_post), Icons.Outlined.Restore)
                    } else {
                        Pair(stringResource(R.string.remove_post), Icons.Outlined.Gavel)
                    }

                PopupMenuItem(
                    text = removeText,
                    icon = removeIcon,
                    onClick = {
                        onDismissRequest()
                        onRemoveClick(postView)
                    },
                )
            }
        }
    }
}
